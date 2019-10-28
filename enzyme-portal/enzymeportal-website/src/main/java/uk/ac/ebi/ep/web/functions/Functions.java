package uk.ac.ebi.ep.web.functions;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import uk.ac.ebi.ep.indexservice.model.protein.WithCofactor;
import uk.ac.ebi.ep.indexservice.model.protein.WithMetabolite;
import uk.ac.ebi.ep.indexservice.model.protein.WithPathway;
import uk.ac.ebi.ep.indexservice.model.protein.WithProteinFamily;
import uk.ac.ebi.ep.indexservice.model.protein.WithTaxonomy;

/**
 * Due to no similar functionality in JSTL, this function was designed to help
 * various operation in a Collection.
 *
 * @author joseph
 */
public final class Functions {

    public static WithMetabolite withMetabolite(List<WithMetabolite> metabolites, String metaboliteId, String accession, String commonName, String entryType) {
        return metabolites
                .parallelStream()
                .filter(metabolite -> metabolite.getMetaboliteId().equalsIgnoreCase(metaboliteId))
                .sorted(Comparator.comparing(WithMetabolite::getEntryType))
                //.findFirst().orElse(new WithMetabolite());
                .findFirst().orElse(new WithMetabolite(metaboliteId, accession, commonName, entryType));

    }

    public static WithCofactor withCofactor(List<WithCofactor> cofactors, String cofactorId, String accession, String commonName, String entryType) {
        String cid = cofactorId.replaceAll("\"", "").replaceAll("CHEBI:", "");

        return cofactors
                .parallelStream()
                .filter(cofactor -> cofactor.getCofactorId().equalsIgnoreCase(cid))
                .sorted(Comparator.comparing(WithCofactor::getEntryType))
                .findFirst().orElse(new WithCofactor(cid, accession, commonName, entryType));

    }

    public static WithProteinFamily withProteinFamily(List<WithProteinFamily> proteinFamilies, String familyId, String accession, String commonName, String entryType) {

        return proteinFamilies
                .parallelStream()
                .filter(family -> family.getFamilyId().equalsIgnoreCase(familyId))
                .sorted(Comparator.comparing(WithProteinFamily::getEntryType))
                .findFirst().orElse(new WithProteinFamily(familyId, accession, commonName, entryType));

    }

    public static WithTaxonomy withTaxonomy(List<WithTaxonomy> organisms, String taxId, String accession, String commonName, String entryType) {

        return organisms.parallelStream()
                .filter(organism -> organism.getTaxonomyId().equalsIgnoreCase(taxId))
                .findFirst().orElse(new WithTaxonomy(taxId, accession, commonName, entryType));

    }

    public static WithPathway withPathway(List<WithPathway> pathways, String pathwayId, String accession, String commonName, String entryType) {

        String pid = pathwayId.replace("R-", "");
        return pathways
                .stream()
                .filter(p -> p.getPathwayId().contains(pid))//TODO
                //.filter(pathway -> pathway.getPathwayId().equalsIgnoreCase(pathwayId))
                .sorted(Comparator.comparing(WithPathway::getEntryType))
                .findFirst().orElse(new WithPathway(pathwayId, accession, commonName, entryType));

    }

    public static boolean startsWithDigit(String data) {
        return Character.isDigit(data.charAt(0));
    }

    /**
     * This function is to enable using capital letter case in checking if a
     * string starts with the letter
     *
     * @param data the original string
     * @param letter the first letter
     * @return true if the string starts with the first letter
     */
    public static boolean startsWithLowerCase(String data, String letter) {
        String current = data;
        if (startsWithDigit(data)) {
            current = data.replaceAll("(-)?\\d+(\\-\\d*)?", "").trim();

        }
        boolean lCase = current.startsWith(letter.toLowerCase());
        boolean nCase = current.startsWith(letter);
        return current.startsWith(letter) ? nCase : lCase;
    }

    /**
     *
     * @param collection
     * @param item
     * @return true if the item is contained in the collection
     */
    public static boolean contains(Collection collection, Object item) {
        return collection.contains(item);
    }

    /**
     *
     * @param collection list of items
     * @param last the last item in the list
     * @return true if the item is the last in the list
     */
    public static boolean lastInList(List<Object> collection, Object last) {
        boolean eval = false;
        LinkedList<Object> list = new LinkedList<>(collection);
        if (last != null) {
            if (last.equals(list.getLast())) {
                eval = true;
            }
        }

        return eval;
    }

    public static String removeSlash(String url) {
        String link = url;
        if (url.contains("/")) {
            link = url.replace("/", "");

        }

        return "#omim" + link;
    }

    /**
     * split with = and - to return only the search term
     *
     * @param text of this format searchparams.text=cathepsin-1.1.1.1 for
     * example
     * @return index[1] - cathepsin
     */
    public static String splitAndGetValue(String text) {
        String result = "";

        if (text == null || "".equals(text)) {
            return result;
        } else {

            String data[] = text.split("searchKey=");

            if (data.length > 1) {
                String keyword[] = data[1].split("-");
                result = keyword[0];
            }
        }
        return result;
    }

}
