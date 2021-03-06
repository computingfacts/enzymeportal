package uk.ac.ebi.ep.xml.transformer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import uk.ac.ebi.ep.xml.entities.PrimaryProtein;
import uk.ac.ebi.ep.xml.entities.Protein;
import uk.ac.ebi.ep.xml.entities.ProteinGroups;
import uk.ac.ebi.ep.xml.entities.repositories.ProteinXmlRepository;
import uk.ac.ebi.ep.xml.schema.AdditionalFields;
import uk.ac.ebi.ep.xml.schema.CrossReferences;
import uk.ac.ebi.ep.xml.schema.Entry;
import uk.ac.ebi.ep.xml.schema.Field;
import uk.ac.ebi.ep.xml.schema.Ref;
import uk.ac.ebi.ep.xml.util.DatabaseName;
import uk.ac.ebi.ep.xml.util.FieldName;
import uk.ac.ebi.ep.xml.util.ModelOrganisms;

/**
 *
 * @author Joseph <joseph@ebi.ac.uk>
 */
@Slf4j
public class ProteinGroupsProcessor extends XmlTransformer implements ItemProcessor<ProteinGroups, Entry> {

    protected static final String REVIEWED = "reviewed";
    protected static final String UNREVIEWED = "unreviewed";
    protected static final String PRIMARY = "primary_";
    private final AtomicInteger count = new AtomicInteger(0);

    private final ProteinXmlRepository proteinXmlRepository;

    public ProteinGroupsProcessor(ProteinXmlRepository repository) {
        this.proteinXmlRepository = repository;
    }

    private void addPrimaryProteinField(PrimaryProtein primaryProtein, Set<Field> fields) {

        String accession = primaryProtein.getAccession();
        String commonName = primaryProtein.getCommonName();
        if (commonName == null || commonName.isEmpty()) {
            commonName = primaryProtein.getScientificName();
        }
        String primaryOrganism = String.format("%s%s", PRIMARY, commonName);
        String primaryAccession = String.format("%s%s", PRIMARY, accession);

        fields.add(new Field(FieldName.PRIMARY_ORGANISM.getName(), primaryOrganism));

        fields.add(new Field(FieldName.PRIMARY_ACCESSION.getName(), primaryAccession));

    }

    private void addPrimaryFunctionFields(PrimaryProtein primaryProtein, Set<Field> fields) {

        if (primaryProtein.getFunction() != null && !StringUtils.isEmpty(primaryProtein.getFunction())) {
            fields.add(new Field(FieldName.FUNCTION.getName(), primaryProtein.getFunction()));
        }

    }

    private void addPrimaryImage(PrimaryProtein primaryProtein, Set<Field> fields) {

        char hasPdbFlag = 'Y';

        if (primaryProtein.getPdbFlag().equals(hasPdbFlag)) {
            fields.add(new Field(FieldName.PRIMARY_IMAGE.getName(), primaryProtein.getPdbId() + "|" + primaryProtein.getPdbSpecies() + "|" + primaryProtein.getPdbLinkedAcc()));
        }

    }

    private void addEntryTypeFields(PrimaryProtein primaryProtein, Set<Field> fields) {

        if (primaryProtein.getEntryType() != null) {
            fields.add(new Field(FieldName.ENTRY_TYPE.getName(), "" + primaryProtein.getEntryType().intValue()));
        }

    }

    @Override
    public Entry process(ProteinGroups proteinGroups) throws Exception {
        AdditionalFields additionalFields = new AdditionalFields();
        CrossReferences cr = new CrossReferences();

        Set<Field> fields = new HashSet<>();
        Set<Ref> refs = new HashSet<>();
        Set<String> relSpecies = new HashSet<>();
        MultiValueMap<String, ProteinMapper> multiValueProteinMapper = new LinkedMultiValueMap<>();

        // if (log.isDebugEnabled()) {
        log.info("Processor " + Runtime.getRuntime().availableProcessors() + " current entry : " + proteinGroups.getProteinGroupId() + "  entry count : " + count.getAndIncrement());

        //}
        Entry entry = new Entry();

        entry.setId(proteinGroups.getProteinGroupId());
        entry.setName(proteinGroups.getProteinName());
        entry.setDescription(proteinGroups.getProteinName());

        addPrimaryProtein(proteinGroups, fields);

        addProteinInformation(proteinGroups, fields, refs, relSpecies, multiValueProteinMapper);
        additionalFields.setField(fields);
        entry.setAdditionalFields(additionalFields);
        cr.setRef(refs);
        entry.setCrossReferences(cr);
        return entry;
    }

    private void addProteinInformation(ProteinGroups proteinGroups, Set<Field> fields, Set<Ref> refs, Set<String> relSpecies, MultiValueMap<String, ProteinMapper> multiValueProteinMapper) {
        try (Stream<Protein> protein = proteinXmlRepository.streamProteinByProteinGroupId(proteinGroups.getProteinGroupId())) {

            protein
                    .parallel()
                    .forEach(data -> processEntries(data, relSpecies, fields, refs, multiValueProteinMapper));
            addRelatedSpeciesField(relSpecies, fields);
            processMultiValueProteinMapper(multiValueProteinMapper, fields);

        }

    }

    private void addPrimaryProtein(ProteinGroups proteinGroups, Set<Field> fields) {
        PrimaryProtein primaryProtein = proteinGroups.getPrimaryProtein();
        if (primaryProtein != null) {

            addPrimaryProteinField(primaryProtein, fields);
            addPrimaryImage(primaryProtein, fields);
            addEntryTypeFields(primaryProtein, fields);
            addPrimaryFunctionFields(primaryProtein, fields);

        }

    }

    private void processEntries(Protein uniprotEntry, Set<String> relSpecies, Set<Field> fields, Set<Ref> refs, MultiValueMap<String, ProteinMapper> multiValueProteinMapper) {

        addRelatedSpecies(uniprotEntry, relSpecies, fields, refs);
        addAccessionTaxonomyAndSpeciesData(uniprotEntry, fields, refs);
        addReactantFieldsAndXrefs(uniprotEntry, fields, refs);

        addCompoundFieldsAndXrefs(uniprotEntry, fields, refs);
        addChebiCompoundFieldsAndXrefs(uniprotEntry, fields, refs);
        addMetaboliteFieldsAndXrefs(uniprotEntry, fields, refs, multiValueProteinMapper);
        addDiseaseFieldsAndXrefs(uniprotEntry, fields, refs);

        addPrimaryEntities(uniprotEntry, fields);
        //now moved to primary entities addEcField(uniprotEntry, fields);
        addEcXrefs(uniprotEntry, refs);
        //now moved to primary entities addTaxonomyFieldAndXrefs(uniprotEntry, fields, refs);

        addUniprotFamilyFieldsAndXrefs(uniprotEntry, fields, refs);
        addPathwayFieldsAndXrefs(uniprotEntry, fields, refs);
        addReactionFieldsAndXrefs(uniprotEntry, fields, refs);
    }

    private void addPrimaryEntities(Protein entry, Set<Field> fields) {
        if (entry.getAccession().equals(entry.getPrimaryAccession())) {

            addPrimarySynonymFields(entry, fields);
            addPrimaryEc(entry, fields);
            addPrimaryCatalyticActivityFields(entry, fields);
            addPrimaryGeneNameFields(entry, fields);
            addEnzymeFamilyToProteinField(entry, fields);
            addEcField(entry, fields);
        }
    }

    private void addPrimarySynonymFields(Protein entry, Set<Field> fields) {

        if (Objects.nonNull(entry.getSynonymNames()) && Objects.nonNull(entry.getProteinName())) {

            Optional<String> synonymName = Optional.ofNullable(entry.getSynonymNames());
            computeSynonymsAndBuildFields(synonymName.orElse(""), entry.getProteinName(), fields);

        }

    }

    private void addPrimaryEc(Protein entry, Set<Field> fields) {
        String primaryEc = String.format("%s%s", PRIMARY, entry.getEcNumber());
        addField(FieldName.PRIMARY_EC.getName(), primaryEc, fields);

    }

    private void addEcField(Protein entry, Set<Field> fields) {
        addField(FieldName.EC.getName(), entry.getEcNumber(), fields);

    }

    private void addEcXrefs(Protein entry, Set<Ref> refs) {

        Ref xref = new Ref(entry.getEcNumber(), DatabaseName.INTENZ.getDbName());
        refs.add(xref);

    }

    private void addEnzymeFamilyToProteinField(Protein entry, Set<Field> fields) {
        if (Objects.nonNull(entry.getEcFamily())) {
            addField(FieldName.ENZYME_FAMILY.getName(), computeEcToFamilyName(entry.getEcFamily()), fields);
        }
    }

    private void addPrimaryGeneNameFields(Protein entry, Set<Field> fields) {
        addGeneNameFields(entry, fields);

    }

    private void addPrimaryCatalyticActivityFields(Protein entry, Set<Field> fields) {
        if (Objects.nonNull(entry.getCatalyticActivity())) {
            addField(FieldName.CATALYTIC_ACTIVITY.getName(), entry.getCatalyticActivity(), fields);
        }
    }

    private void addRelatedSpeciesField(Set<String> relSpecies, Set<Field> fields) {
        if (!relSpecies.isEmpty()) {
            String rsField = relSpecies
                    .stream()
                    .collect(Collectors.joining("|"));
            Field field = new Field(FieldName.RELATED_SPECIES.getName(), rsField);
            fields.add(field);
        }
    }

    private void addRelatedSpecies(Protein uniprotEntry, Set<String> relSpecies, Set<Field> fields, Set<Ref> refs) {

        if (Objects.equals(uniprotEntry.getRelatedProteinsId(), uniprotEntry.getPrimaryRelatedProteinsId())) {
            relSpecies.add(uniprotEntry.getAccession() + ";" + uniprotEntry.getOrganismName() + ";" + uniprotEntry.getScientificName() + ";" + uniprotEntry.getExpEvidenceFlag() + ";" + uniprotEntry.getTaxId());

        }
    }

    private void addAccessionTaxonomyAndSpeciesData(Protein uniprotEntry, Set<Field> fields, Set<Ref> refs) {

        if (Objects.equals(uniprotEntry.getRelatedProteinsId(), uniprotEntry.getPrimaryRelatedProteinsId())) {

            addScientificNameFields(uniprotEntry.getScientificName(), fields);
            addCommonNameFields(uniprotEntry.getOrganismName(), fields);
            addAccessionXrefs(uniprotEntry.getAccession(), refs);
            addGroupAccessionFields(uniprotEntry.getAccession(), fields);
            addTaxonomyFieldAndXrefs(uniprotEntry, fields, refs);

        }
    }

    protected void addGroupAccessionFields(String accession, Set<Field> fields) {
        if (!StringUtils.isEmpty(accession)) {
            fields.add(new Field(FieldName.GROUP_ACCESSION.getName(), accession));

        }
    }

    @Override
    void addDiseaseFieldsAndXrefs(Protein disease, Set<Field> fields, Set<Ref> refs) {

        if (Objects.nonNull(disease.getOmimNumber()) && Objects.nonNull(disease.getDiseaseName())) {

            fields.add(new Field(FieldName.DISEASE_NAME.getName(), disease.getDiseaseName()));

            fields.add(new Field(FieldName.WITH_DISEASE.getName(), withResourceField(disease.getOmimNumber(), disease.getAccession(), disease.getOrganismName(), disease.getEntryType())));
            //fields.add(new Field(FieldName.HAS_DISEASE.getName(), HAS_DISEASE));
            refs.add(new Ref(disease.getOmimNumber(), DatabaseName.OMIM.getDbName()));
        }
    }

    @Override
    void addUniprotFamilyFieldsAndXrefs(Protein family, Set<Field> fields, Set<Ref> refs) {

        if (Objects.nonNull(family.getFamilyGroupId())) {

            fields.add(new Field(FieldName.PROTEIN_FAMILY_ID.getName(), family.getFamilyGroupId()));

            fields.add(new Field(FieldName.WITH_PROTEIN_FAMILY.getName(), withResourceField(family.getFamilyGroupId(), family.getAccession(), family.getOrganismName(), family.getEntryType())));
            //fields.add(new Field(FieldName.HAS_PROTEIN_FAMILY.getName(), HAS_PROTEIN_FAMILY));
            refs.add(new Ref(family.getFamilyGroupId(), DatabaseName.PROTEIN_FAMILY.getDbName()));
        }
    }

    @Override
    void addReactantFieldsAndXrefs(Protein reactant, Set<Field> fields, Set<Ref> refs) {

        if (Objects.nonNull(reactant.getReactantSource())) {

            if (reactant.getReactantSource().toUpperCase().equalsIgnoreCase(RHEA)) {
                fields.add(new Field(FieldName.RHEA_ID.getName(), reactant.getReactantId()));
            }

            refs.add(new Ref(reactant.getReactantId(), reactant.getReactantSource().toUpperCase()));
        }
    }

    @Override
    void addPathwayFieldsAndXrefs(Protein pathway, Set<Field> fields, Set<Ref> refs) {

        if (Objects.nonNull(pathway.getPathwayId())) {

            fields.add(new Field(FieldName.WITH_PATHWAY.getName(), withResourceField(parseReactomePathwayId(pathway.getPathwayId()), pathway.getAccession(), pathway.getOrganismName(), pathway.getEntryType())));
            //fields.add(new Field(FieldName.HAS_PATHWAY.getName(), HAS_PATHWAY));
            refs.add(new Ref(parseReactomePathwayId(pathway.getPathwayId()), DatabaseName.REACTOME.getDbName()));
        }
    }

    private void processMultiValueProteinMapper(MultiValueMap<String, ProteinMapper> multiValueProteinMapper, Set<Field> fields) {

//        for (Map.Entry<String, List<ProteinMapper>> entry : multiValueProteinMapper.entrySet()) {
//            addWithMetaboliteFields(entry, fields);
//        }
//        
        multiValueProteinMapper.entrySet().forEach(entry -> addWithMetaboliteFields(entry, fields));

        multiValueProteinMapper.clear();
    }

    private void addWithMetaboliteFields(Map.Entry<String, List<ProteinMapper>> entry, Set<Field> fields) {
        String key = entry.getKey();

        Set<ProteinMapper> proteinMapperSet = entry.getValue().stream().distinct().collect(Collectors.toSet());

        TreeMap<Integer, ProteinMapper> priorityMapper = new TreeMap<>();
        AtomicInteger counter = new AtomicInteger(10);

        proteinMapperSet.stream()
                .filter(exp -> exp.getExpEvidence() == 1)
                .sorted(Comparator.comparing(ProteinMapper::getExpEvidence).reversed())
                .forEach(s -> orderByModelOrganism(s, priorityMapper, counter));

        if (priorityMapper.isEmpty()) {

            proteinMapperSet.forEach(s -> orderByModelOrganism(s, priorityMapper, counter));

        }

        ProteinMapper pm = priorityMapper.firstEntry().getValue();

        fields.add(new Field(FieldName.WITH_METABOLITE.getName(), withResourceField(key, pm.getAccession(), pm.getOrganismName(), pm.getExpEvidence())));
        priorityMapper.clear();
    }

    private void orderByModelOrganism(ProteinMapper mapper, TreeMap<Integer, ProteinMapper> priorityMapper, AtomicInteger counter) {

        if (mapper.getTaxid() == ModelOrganisms.HUMAN.getTaxId()) {

            priorityMapper.put(0, mapper);

        } else if ((mapper.getTaxid() == ModelOrganisms.MOUSE.getTaxId())) {

            priorityMapper.put(1, mapper);
        } else if ((mapper.getTaxid() == ModelOrganisms.MOUSE_EAR_CRESS.getTaxId())) {

            priorityMapper.put(2, mapper);

        } else if ((mapper.getTaxid() == ModelOrganisms.FRUIT_FLY.getTaxId())) {

            priorityMapper.put(3, mapper);
        } else if ((mapper.getTaxid() == ModelOrganisms.ECOLI.getTaxId())) {

            priorityMapper.put(4, mapper);
        } else if ((mapper.getTaxid() == ModelOrganisms.BAKER_YEAST.getTaxId())) {

            priorityMapper.put(5, mapper);
        } else if ((mapper.getTaxid() == ModelOrganisms.RAT.getTaxId())) {

            priorityMapper.put(6, mapper);
        } else {

            priorityMapper.put(counter.getAndIncrement(), mapper);
        }

    }

    public void addMetaboliteFieldsAndXrefs(Protein chebiCompound, Set<Field> fields, Set<Ref> refs, MultiValueMap<String, ProteinMapper> multiValueMap) {

        if (Objects.nonNull(chebiCompound.getChebiCompoundRole()) && Objects.nonNull(chebiCompound.getChebiCompoundId()) && Objects.nonNull(chebiCompound.getChebiCompoundName())) {

            if (chebiCompound.getChebiCompoundRole().equalsIgnoreCase(METABOLITE)) {

                ProteinMapper pm = new ProteinMapper();
                pm.setAccession(chebiCompound.getAccession());
                pm.setEntryType(chebiCompound.getEntryType().intValue());
                pm.setExpEvidence(chebiCompound.getExpEvidenceFlag());
                pm.setOrganismName(chebiCompound.getOrganismName());
                pm.setTaxid(chebiCompound.getTaxId());

                multiValueMap.add(chebiCompound.getChebiCompoundId().replace(CHEBI_PREFIX, METABOLIGHTS_PREFIX), pm);

                String metaboliteId = chebiCompound.getChebiCompoundId().replace(CHEBI_PREFIX, METABOLIGHTS_PREFIX);
                fields.add(new Field(FieldName.METABOLITE.getName(), metaboliteId));
                fields.add(new Field(FieldName.METABOLITE_NAME.getName(), chebiCompound.getChebiCompoundName()));
                String metabolightId = chebiCompound.getChebiCompoundId().replace(CHEBI_PREFIX, METABOLIGHTS_PREFIX);
                refs.add(new Ref(metabolightId, DatabaseName.METABOLIGHTS.getDbName()));

            }

        }

    }

//    @Override
//    public void addMetaboliteFieldsAndXrefs(Protein chebiCompound, Set<Field> fields, Set<Ref> refs) {
//
//        if (Objects.nonNull(chebiCompound.getChebiCompoundRole()) && Objects.nonNull(chebiCompound.getChebiCompoundId()) && Objects.nonNull(chebiCompound.getChebiCompoundName())) {
//
//            if (chebiCompound.getChebiCompoundRole().equalsIgnoreCase(METABOLITE)) {
//                fields.add(new Field(FieldName.HAS_METABOLITE.getName(), HAS_METABOLITE));
//                fields.add(new Field(FieldName.WITH_METABOLITE.getName(), withResourceField(chebiCompound.getChebiCompoundId().replace(CHEBI_PREFIX, ""), chebiCompound.getAccession(), chebiCompound.getOrganismName(), chebiCompound.getEntryType())));
//                String metaboliteId = chebiCompound.getChebiCompoundId().replace(CHEBI_PREFIX, METABOLITE.toLowerCase());
//                fields.add(new Field(FieldName.METABOLITE.getName(), metaboliteId));
//                fields.add(new Field(FieldName.METABOLITE_NAME.getName(), chebiCompound.getChebiCompoundName()));
//                String metabolightId = chebiCompound.getChebiCompoundId().replace(CHEBI_PREFIX, METABOLIGHTS_PREFIX);
//                refs.add(new Ref(metabolightId, DatabaseName.METABOLIGHTS.getDbName()));
//
//            }
//
//        }
//
//    }
    @Override
    public void addChebiCompoundFieldsAndXrefs(Protein chebiCompound, Set<Field> fields, Set<Ref> refs) {

        if (Objects.nonNull(chebiCompound.getChebiCompoundRole()) && Objects.nonNull(chebiCompound.getChebiCompoundId()) && Objects.nonNull(chebiCompound.getChebiCompoundName())) {
            switch (chebiCompound.getChebiCompoundRole()) {

                case REACTANT:
                    addChebiCompoundField(chebiCompound, fields, refs);
                    fields.add(new Field(FieldName.REACTANT.getName(), chebiCompound.getChebiCompoundName()));
                    break;
                default:

                    fields.add(new Field(FieldName.CHEBI_ID.getName(), chebiCompound.getChebiCompoundId()));
                    fields.add(new Field(FieldName.COMPOUND_NAME.getName(), chebiCompound.getChebiCompoundName()));
                    refs.add(new Ref(chebiCompound.getChebiCompoundId(), CHEBI));
                    String chebiId = chebiCompound.getChebiCompoundId().replace(CHEBI_PREFIX, CHEBI.toLowerCase());
                    fields.add(new Field(FieldName.CHEBIID.getName(), chebiId));
                    break;
            }
        }

    }

    private void addChebiCompoundField(Protein chebiCompound, Set<Field> fields, Set<Ref> refs) {

        fields.add(new Field(FieldName.CHEBI_ID.getName(), chebiCompound.getChebiCompoundId()));

        refs.add(new Ref(chebiCompound.getChebiCompoundId(), CHEBI));
        String chebiId = chebiCompound.getChebiCompoundId().replace(CHEBI_PREFIX, CHEBI.toLowerCase());
        fields.add(new Field(FieldName.CHEBIID.getName(), chebiId));

    }

    @Override
    public void addCompoundFieldsAndXrefs(Protein compound, Set<Field> fields, Set<Ref> refs) {

        if (Objects.nonNull(compound.getCompoundSource()) && Objects.nonNull(compound.getCompoundId()) && Objects.nonNull(compound.getCompoundName())) {
            switch (compound.getCompoundRole()) {
                case COFACTOR:
                    addChebiField(compound, fields, refs);
                    addCofactorField(compound, compound.getAccession(), compound.getOrganismName(), compound.getEntryType(), fields);
                    break;
                case INHIBITOR:
                    addCompoundFieldAndXref(compound, FieldName.INHIBITOR.getName(), FieldName.INHIBITOR_NAME.getName(), fields, refs);
                    break;
                case ACTIVATOR:
                    addCompoundFieldAndXref(compound, FieldName.ACTIVATOR.getName(), FieldName.ACTIVATOR_NAME.getName(), fields, refs);
                    break;
                default:

                    fields.add(new Field(FieldName.COMPOUND_NAME.getName(), compound.getCompoundName()));
                    refs.add(new Ref(compound.getCompoundId(), compound.getCompoundSource().toUpperCase()));
                    break;
            }
        }

    }

    private void addChebiField(Protein compound, Set<Field> fields, Set<Ref> refs) {

        fields.add(new Field(FieldName.CHEBI_ID.getName(), compound.getCompoundId()));

        refs.add(new Ref(compound.getCompoundId(), compound.getCompoundSource().toUpperCase()));
        String chebiId = compound.getCompoundId().replace(CHEBI_PREFIX, CHEBI.toLowerCase());
        fields.add(new Field(FieldName.CHEBIID.getName(), chebiId));

    }

    private void addCompoundFieldAndXref(Protein compound, String fieldIdkey, String fieldNameKey, Set<Field> fields, Set<Ref> refs) {

        fields.add(new Field(fieldIdkey, compound.getCompoundId()));

        fields.add(new Field(fieldNameKey, compound.getCompoundName()));

        refs.add(new Ref(compound.getCompoundId(), compound.getCompoundSource().toUpperCase()));
    }

    private void addCofactorField(Protein compound, String accession, String commonName, int entryType, Set<Field> fields) {

        fields.add(new Field(FieldName.COFACTOR.getName(), compound.getCompoundId().replace(CHEBI_PREFIX, "")));

        fields.add(new Field(FieldName.COFACTOR_NAME.getName(), compound.getCompoundName()));

        fields.add(new Field(FieldName.WITH_COFACTOR.getName(), withResourceField(compound.getCompoundId().replace(CHEBI_PREFIX, ""), accession, commonName, entryType)));
        //fields.add(new Field(FieldName.HAS_COFACTOR.getName(), HAS_COFACTOR));

    }

}
