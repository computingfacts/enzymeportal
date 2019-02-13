package uk.ac.ebi.ep.xml.transformer;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import uk.ac.ebi.ep.xml.entity.enzyme.EnzymePortalUniqueEc;
import uk.ac.ebi.ep.xml.entity.enzyme.IntenzAltNames;
import uk.ac.ebi.ep.xml.entity.enzyme.UniprotEntryEnzyme;
import uk.ac.ebi.ep.xml.schema.AdditionalFields;
import uk.ac.ebi.ep.xml.schema.CrossReferences;
import uk.ac.ebi.ep.xml.schema.Entry;
import uk.ac.ebi.ep.xml.schema.Field;
import uk.ac.ebi.ep.xml.schema.Ref;
import uk.ac.ebi.ep.xml.util.FieldName;

/**
 *
 * @author <a href="mailto:joseph@ebi.ac.uk">Joseph</a>
 */
@Slf4j
public class EnzymeProcessor extends XmlTransformer implements ItemProcessor<EnzymePortalUniqueEc, Entry> {

    private final AtomicInteger count = new AtomicInteger(1);
//    public EnzymeProcessor(XmlFileProperties xmlFileProperties) {
//        super(xmlFileProperties);
//    }
//    private void processOnlyEcWithSwissProtOrEvidence(Set<EnzymePortalEcNumbers> enzymes, Set<Field> fields, Set<Ref> refs) {
//        enzymes
//                .stream()
//                .filter(u -> (u.getUniprotAccession().getEntryType() == 0)
//                || u.getUniprotAccession().getExpEvidenceFlag() == BigInteger.ONE)
//                .forEach(ec -> processUniprotEntry(ec.getUniprotAccession(), fields, refs));
//
//    }

    //@Transactional
    @Override
    public Entry process(EnzymePortalUniqueEc enzyme) throws Exception {
        CopyOnWriteArraySet<Field> fields = new CopyOnWriteArraySet<>();
         CopyOnWriteArraySet<Ref> refs = new CopyOnWriteArraySet<>();
        //Set s = Collections.synchronizedSet(new HashSet<>());
        //Set<Ref> refs = new HashSet<>();
        Entry entry = new Entry();
        entry.setId(enzyme.getEcNumber());
        entry.setName(enzyme.getEnzymeName());

        String description = String.format("%s %s", enzyme.getEcNumber(), enzyme.getEnzymeName());
        entry.setDescription(description);

        addEnzymeFamilyField(enzyme.getEcNumber(), fields);

        addCofactorsField(enzyme.getCofactor(), fields);
        addCatalyticActivityField(enzyme.getCatalyticActivity(), fields);
        addAltNamesField(enzyme.getIntenzAltNamesSet(), fields);
        addEcSource(enzyme.getEcNumber(), refs);

        int numEnzymes = enzyme.getEnzymePortalEcNumbersSet().size();
        log.warn(enzyme.getEcNumber() + " Number of ezymes to process " + numEnzymes + " count : "+ count.getAndIncrement());
        
            enzyme.getEnzymePortalEcNumbersSet()
                    .stream()
                    .parallel()
                    .map(uniprotEntry -> CompletableFuture.runAsync(() -> {
                processUniprotEntry(uniprotEntry.getUniprotAccession(), fields, refs);

            }));
//        if (numEnzymes <= 1000) {
//            enzyme.getEnzymePortalEcNumbersSet()
//                    .stream()
//                    //.parallel()
//                    .forEach(ec -> processUniprotEntry(ec.getUniprotAccession(), fields, refs));
//        } else {
//
//            enzyme.getEnzymePortalEcNumbersSet()
//                    .stream()
//                    //.parallel()
//                    .map(uniprotEntry -> CompletableFuture.runAsync(() -> {
//                processUniprotEntry(uniprotEntry.getUniprotAccession(), fields, refs);
//
//            }));
//        }

        //default
//        enzyme.getEnzymePortalEcNumbersSet()
//                .stream().parallel()
//                .forEach(ec -> processUniprotEntry(ec.getUniprotAccession(), fields, refs));
        AdditionalFields additionalFields = new AdditionalFields();
        additionalFields.setField(fields);
        entry.setAdditionalFields(additionalFields);

        CrossReferences cr = new CrossReferences();
        cr.setRef(refs);
        entry.setCrossReferences(cr);

        return entry;

    }

//    private void processInParallel(EnzymePortalUniqueEc enzyme, Set<Field> fields, Set<Ref> refs) {
//        // List<CompletableFuture<Void>> futures = 
//        enzyme.getEnzymePortalEcNumbersSet()
//                .stream()
//                .map(uniprotEntry -> CompletableFuture.runAsync(() -> {
//            processUniprotEntry(uniprotEntry.getUniprotAccession(), fields, refs);
//        }));
//    }
//synchronized
    private synchronized void processUniprotEntry(UniprotEntryEnzyme uniprotEntry, CopyOnWriteArraySet<Field> fields, CopyOnWriteArraySet<Ref> refs) {
        // addUniprotIdFields(uniprotEntry, fields);
        addProteinNameFields(uniprotEntry.getProteinName(), fields);

        addScientificNameFields(uniprotEntry.getScientificName(), fields);
        addCommonNameFields(uniprotEntry.getCommonName(), fields);
        addGeneNameFields(uniprotEntry.getEntryToGeneMappingSet(), fields);

        addUniprotFamilyFieldsAndXrefs(uniprotEntry.getUniprotFamiliesSet(), fields, refs);

        addSynonymFields(uniprotEntry.getSynonymNames(), uniprotEntry.getProteinName(), fields);
        //addSource(enzyme, refs);
        addAccessionXrefs(uniprotEntry.getAccession(), refs);
        addTaxonomyXrefs(uniprotEntry.getTaxId(), refs);

        addCompoundFieldsAndXrefs(uniprotEntry.getEnzymePortalCompoundSet(), fields, refs);
        addDiseaseFieldsAndXrefs(uniprotEntry.getEnzymePortalDiseaseSet(), fields, refs);
        addPathwaysXrefs(uniprotEntry.getEnzymePortalPathwaysSet(), refs);
        addReactantFields(uniprotEntry.getEnzymePortalReactantSet(), fields);
        addReactionFieldsAndXrefs(uniprotEntry.getEnzymePortalReactionSet(), fields, refs);
    }

    private void addAltNamesField(Set<IntenzAltNames> altNames, Set<Field> fields) {

        altNames
                .stream()
                .map(altName -> new Field(FieldName.INTENZ_ALT_NAMES.getName(), altName.getAltName()))
                .forEach(field -> fields.add(field));

    }

    private void addCofactorsField(String cofactor, Set<Field> fields) {
        if (cofactor != null) {
            Field field = new Field(FieldName.INTENZ_COFACTORS.getName(), cofactor);
            fields.add(field);
        }
    }

    private void addCatalyticActivityField(String catalyticActivity, Set<Field> fields) {
        if (catalyticActivity != null) {
            Field field = new Field(FieldName.CATALYTIC_ACTIVITY.getName(), catalyticActivity);
            fields.add(field);
        }
    }

//    @Deprecated
//    private void addReactionMechanism(Set<ReactionMechanism> reactionMechanismSet, Set<Field> fields) {
//
//        reactionMechanismSet
//                .stream()
//                .map(rm -> rm.getMcsaId() + ";" + rm.getEnzymeName() + ";" + rm.getImageId() + ";" + rm.getMechanismDescription())
//                .map(mechanism -> new Field(FieldName.REACTION_MECHANISM.getName(), mechanism))
//                .forEachOrdered(pdbfield -> fields.add(pdbfield));
//
//    }
}
