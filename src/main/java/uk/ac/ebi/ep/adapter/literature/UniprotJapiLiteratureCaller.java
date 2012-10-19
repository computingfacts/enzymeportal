package uk.ac.ebi.ep.adapter.literature;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import uk.ac.ebi.biobabel.citations.CitexploreWSClient;
import uk.ac.ebi.biobabel.citations.DataSource;
import uk.ac.ebi.cdb.webservice.Journal;
import uk.ac.ebi.cdb.webservice.JournalInfo;
import uk.ac.ebi.kraken.interfaces.uniprot.Citation;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.AgricolaId;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.Author;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.Book;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.ElectronicArticle;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.HasAgricolaId;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.HasAuthors;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.HasJournalName;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.HasPages;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.HasPubMedId;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.HasPublicationDate;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.HasTitle;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.HasVolume;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.JournalArticle;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.Page;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.Patent;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.PubMedId;
import uk.ac.ebi.kraken.interfaces.uniprot.citations.Volume;
import uk.ac.ebi.kraken.uuw.services.remoting.Attribute;
import uk.ac.ebi.kraken.uuw.services.remoting.AttributeIterator;
import uk.ac.ebi.kraken.uuw.services.remoting.Query;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtJAPI;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryBuilder;
import uk.ac.ebi.kraken.uuw.services.remoting.UniProtQueryService;

/**
 * Caller to retrieve citations from UniProt using the UniProtJAPI.
 * @author rafa
 *
 */
public class UniprotJapiLiteratureCaller
implements Callable<Set<uk.ac.ebi.cdb.webservice.Result>> {

	private static final Logger LOGGER = Logger.getLogger(UniprotJapiLiteratureCaller.class);
	
	private String uniprotId;
	
	/**
	 * Constructor with a UniProt ID to search.
	 * @param uniprotId The UniProt ID to get citations from.
	 */
	public UniprotJapiLiteratureCaller(String uniprotId){
		this.uniprotId = uniprotId;
	}
	
	public Set<uk.ac.ebi.cdb.webservice.Result> call()
	throws Exception {
		// CiteXplore citations:
		HashSet<uk.ac.ebi.cdb.webservice.Result> cxCits = null;
		UniProtQueryService uniProtQueryService =
				UniProtJAPI.factory.getUniProtQueryService();
		Query query = UniProtQueryBuilder.buildIDListQuery(
				Arrays.asList(new String[]{ uniprotId }));
		AttributeIterator<UniProtEntry> it =
				uniProtQueryService.getAttributes(query, "ognl:citations");
		if (it.hasNext()){
			cxCits = new HashSet<uk.ac.ebi.cdb.webservice.Result>();
			for (Attribute att: it){
				// UniProt citations:
				@SuppressWarnings("unchecked")
				List<Citation> upCits = (List<Citation>) att.getValue();
				for (Citation upCit :upCits){
					uk.ac.ebi.cdb.webservice.Result cxCit = null;
					// Try to get it from CiteXplore:
					cxCit = getCitationFromCitexplore(upCit);
					if (cxCit == null){
						// Otherwise, build one:
						LOGGER.warn("No literature identifier found for one citation: " + uniprotId);
						//cxCit = buildCitation(upCit);
					} else {
						cxCits.add(cxCit);
					}
				}
			}
		} else {
			LOGGER.warn("No citations retrieved from " + uniprotId);
		}
		return cxCits;
	}

	private uk.ac.ebi.cdb.webservice.Result buildCitation(Citation upCit) {
		uk.ac.ebi.cdb.webservice.Result cxCit =
				new uk.ac.ebi.cdb.webservice.Result();
		JournalInfo issue = new JournalInfo();
		Journal journal = new Journal();
		issue.setJournal(journal);
		cxCit.setJournalInfo(issue);
		loadCoreMetadata(upCit, cxCit);
		loadXrefs(upCit, cxCit);
		// Special bits:
		switch (upCit.getCitationType()){
		case BOOK:
			Book book = (Book) upCit;
			load(cxCit, book);
			break;
		case PATENT:
			Patent patent = (Patent) upCit;
			load(cxCit, patent);
			break;
		/* Ignoring other cases */
		}
		return cxCit;
	}

	/**
	 * Loads core metadata - title, abstract, authors, year - from a UniProt
	 * citation into a CiteXplore citation.
	 * @param upCit
	 * @param cxCit
	 */
	private void loadCoreMetadata(Citation upCit,
			uk.ac.ebi.cdb.webservice.Result cxCit) {
		// Title:
		if (upCit instanceof HasTitle){
			cxCit.setTitle(((HasTitle) upCit).getTitle().getValue());
		}
		// Abstract:
		/* No info from old Citation objects (wait for citationNew package)
		List<CitationSummary> upAbstracts = upCit.getCitationSummary();
		if (upAbstracts != null && !upAbstracts.isEmpty()){
			cxCit.setAbstractText(upAbstracts.get(0).getValue());
		}
		*/
		// Authors:
		if (upCit instanceof HasAuthors){
			for (Author upAuthor: ((HasAuthors) upCit).getAuthors()){
				uk.ac.ebi.cdb.webservice.Authors cxAuthor =
						new uk.ac.ebi.cdb.webservice.Authors();
				cxAuthor.setFullName(upAuthor.getValue());
				cxCit.getAuthorList().getAuthor().add(cxAuthor);
			}
		}
		// Year:
		if (upCit instanceof HasPublicationDate){
			// UniProt's publication date might come as 'JUL-2010':
			Pattern p = Pattern.compile("(?:\\w{3}-)?(\\d{4})(?:-\\d{2}(?:-\\d{2}(?:.*)?)?)?");
			String upDate = ((HasPublicationDate) upCit)
					.getPublicationDate().getValue();
			Matcher m = p.matcher(upDate);
			if (m.matches()){
				cxCit.getJournalInfo().setYearOfPublication(Short.valueOf(m.group(1)));
			} else {
				LOGGER.warn("Date pattern not recognised: " + upDate);
			}
		}
		// Volume:
		if (upCit instanceof HasVolume){
			Volume volume = ((HasVolume) upCit).getVolume();
			if (volume != null){
				cxCit.getJournalInfo().setVolume(volume.getValue());
			}
		}
		// Journal name:
		if (upCit instanceof HasJournalName){
			cxCit.getJournalInfo().getJournal().setTitle(
					((HasJournalName) upCit).getJournalName().getValue());
		}
		// Pages:
		if (upCit instanceof HasPages){
			setPages(cxCit, ((HasPages) upCit).getFirstPage(),
					((HasPages) upCit).getLastPage());
		}
	}

	/**
	 * Loads metadata from a UniProt Book object
	 * into a CiteXplore Result object.
	 * @param cxCit The CiteXplore Result to load metadata into.
	 * @param book The UniProt object to get metadata from.
	 */
	private void load(uk.ac.ebi.cdb.webservice.Result cxCit, Book book) {
		// Book name:
		cxCit.getJournalInfo().getJournal()
				.setTitle(book.getBookName().getValue());
	}

	/**
	 * Loads metadata from a UniProt ElectronicArticle object
	 * into a CiteXplore Result object.
	 * @param cxCit The CiteXplore Result to load metadata into.
	 * @param eArticle The UniProt object to get metadata from.
	 */
	private void load(uk.ac.ebi.cdb.webservice.Result cxCit,
			ElectronicArticle eArticle) {
	}

	/**
	 * Loads metadata from a UniProt JournalArticle object
	 * into a CiteXplore Result object.
	 * @param cxCit The CiteXplore Result to load metadata into.
	 * @param jArticle The UniProt object to get metadata from.
	 */
	private void load(uk.ac.ebi.cdb.webservice.Result cxCit,
			JournalArticle jArticle) {
	}

	/**
	 * Loads metadata from a UniProt Patent object
	 * into a CiteXplore Result object.
	 * @param cxCit The CiteXplore Result to load metadata into.
	 * @param patent The UniProt object to get metadata from.
	 */
	private void load(uk.ac.ebi.cdb.webservice.Result cxCit, Patent patent) {
		// How to handle this?
//		cxCit.getPatentDetails().setPatentDetailId(
//				Integer.valueOf(patent.getPatentNumber().getValue()));
	}

	private void setPages(uk.ac.ebi.cdb.webservice.Result cxCit, Page firstPage,
			Page lastPage) {
		if (firstPage != null){
			StringBuilder pages = new StringBuilder(firstPage.getValue());
			if (lastPage != null){
				pages.append('-').append(lastPage.getValue());
			}
			cxCit.setPageInfo(pages.toString());
		}
	}

	/**
	 * Loads bibliography database xrefs from a UniProt citation into
	 * a CiteXplore citation. Currently, only PubMed and Agricola are
	 * supported.
	 * @param upCit
	 * @param cxCit
	 */
	private void loadXrefs(Citation upCit,
			uk.ac.ebi.cdb.webservice.Result cxCit) {
		if (upCit instanceof HasPubMedId){
			PubMedId pubmedId = ((HasPubMedId) upCit).getPubMedId();
			if (pubmedId != null){
				cxCit.setSource(DataSource.MED.name());
				cxCit.setId(pubmedId.getValue());
			}
		} else if (upCit instanceof HasAgricolaId){
			AgricolaId agricolaId = ((HasAgricolaId) upCit).getAgricolaId();
			if (agricolaId != null){
				cxCit.setSource(DataSource.AGR.name());
				cxCit.setId(agricolaId.getValue());
			}
		}
	}
	
	private uk.ac.ebi.cdb.webservice.Result getCitationFromCitexplore(Citation upCit){
		uk.ac.ebi.cdb.webservice.Result cxCit = null;
		CitexploreWSClient citexploreClient = null;
		try {
			citexploreClient =
					CitexploreWSClientPool.getInstance().borrowObject();
			if (upCit instanceof HasPubMedId){
				PubMedId pubmedId = ((HasPubMedId) upCit).getPubMedId();
				if (pubmedId != null){
					String val = pubmedId.getValue();
					if (val != null && val.length() > 0){
						cxCit = citexploreClient.retrieveCitation(
								DataSource.MED, pubmedId.getValue());
					}
				}
			} else if (upCit instanceof HasAgricolaId){
				AgricolaId agricolaId = ((HasAgricolaId) upCit).getAgricolaId();
				if (agricolaId != null){
					String val = agricolaId.getValue();
					if (val != null && val.length() > 0){
						cxCit = citexploreClient.retrieveCitation(
								DataSource.AGR, agricolaId.getValue());
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unable to get citation from CiteXplore", e);
		} finally {
			if (citexploreClient != null){
				try {
					CitexploreWSClientPool.getInstance()
							.returnObject(citexploreClient);
				} catch (Exception e) {
					LOGGER.error("Unable to return CiteXplore client", e);
				}
			}
		}
		return cxCit;
	}

}
