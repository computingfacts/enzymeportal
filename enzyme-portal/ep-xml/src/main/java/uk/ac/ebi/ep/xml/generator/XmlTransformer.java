package uk.ac.ebi.ep.xml.generator;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.util.StringUtils;
import uk.ac.ebi.ep.data.domain.UniprotEntry;
import uk.ac.ebi.ep.xml.config.XmlConfigParams;
import static uk.ac.ebi.ep.xml.generator.XmlTransformer.ENZYME_PORTAL;
import uk.ac.ebi.ep.xml.model.Database;
import uk.ac.ebi.ep.xml.model.Field;
import uk.ac.ebi.ep.xml.model.Ref;
import uk.ac.ebi.ep.xml.util.DatabaseName;
import uk.ac.ebi.ep.xml.util.FieldName;

/**
 *
 * @author Joseph <joseph@ebi.ac.uk>
 */
public class XmlTransformer {

    protected static final String REVIEWED = "reviewed";
    protected static final String UNREVIEWED = "unreviewed";

    public static final String ENZYME_PORTAL = "Enzyme Portal";
    public static final String ENZYME_PORTAL_DESCRIPTION = "The Enzyme Portal integrates publicly available information about enzymes, such as small-molecule chemistry, biochemical pathways and drug compounds.";

    private final XmlConfigParams xmlConfigParams;

    public XmlTransformer(XmlConfigParams xmlConfigParams) {
        this.xmlConfigParams = xmlConfigParams;
    }

    protected Database buildDatabaseInfo(int entryCount) {
        Database database = new Database();
        database.setName(ENZYME_PORTAL);
        database.setDescription(ENZYME_PORTAL_DESCRIPTION);
        database.setRelease(xmlConfigParams.getReleaseNumber());
        LocalDate date = LocalDate.now();
        database.setReleaseDate(date);
        database.setEntryCount(entryCount);
        return database;
    }

    protected void addUniprotIdFields(UniprotEntry uniprotEntry, Set<Field> fields) {
        if (!StringUtils.isEmpty(uniprotEntry.getUniprotid())) {
            Field field = new Field(FieldName.UNIPROT_NAME.getName(), uniprotEntry.getUniprotid());
            fields.add(field);
            
        }
    }

    protected void addStatus(UniprotEntry uniprotEntry, Set<Field> fields) {
        if (uniprotEntry.getEntryType() != null) {
            int entryType = uniprotEntry.getEntryType().intValue();

            if (entryType == 0) {
                Field field = new Field(FieldName.STATUS.getName(), REVIEWED);
                fields.add(field);
            }
            if (entryType == 1) {
                Field field = new Field(FieldName.STATUS.getName(), UNREVIEWED);
                fields.add(field);
            }

        }
    }

    protected void addProteinNameFields(UniprotEntry uniprotEntry, Set<Field> fields) {
        if (!StringUtils.isEmpty(uniprotEntry.getProteinName())) {
            Field field = new Field(FieldName.PROTEIN_NAME.getName(), uniprotEntry.getProteinName());
            fields.add(field);

        }
    }

    protected void addScientificNameFields(UniprotEntry uniprotEntry, Set<Field> fields) {
        if (!StringUtils.isEmpty(uniprotEntry.getScientificName())) {
            Field field = new Field(FieldName.SCIENTIFIC_NAME.getName(), uniprotEntry.getScientificName());
            fields.add(field);

        }
    }

    private void computeSynonymsAndBuildFields(Optional<String> synonymName, String proteinName, Set<Field> fields) {
        if (synonymName.isPresent()) {
            Stream.of(synonymName.get().split(";")).distinct()
                    .filter(otherName -> (!otherName.trim().equalsIgnoreCase(proteinName.trim())))
                    .map((syn) -> {
                        Field field = new Field(FieldName.SYNONYM.getName(), syn);
                        return field;
                    }).forEach((field) -> {
                        fields.add(field);
                    });
        }
    }

    protected void addSynonymFields(UniprotEntry uniprotEntry, Set<Field> fields) {
        if (uniprotEntry.getSynonymNames() != null && uniprotEntry.getProteinName() != null) {

            Optional<String> synonymName = Optional.ofNullable(uniprotEntry.getSynonymNames());
            computeSynonymsAndBuildFields(synonymName, uniprotEntry.getProteinName(), fields);

        }
    }

    protected void addAccessionXrefs(UniprotEntry uniprotEntry, Set<Ref> refs) {
        if (!StringUtils.isEmpty(uniprotEntry.getAccession())) {
            Ref xref = new Ref(uniprotEntry.getAccession(), DatabaseName.UNIPROTKB.getDbName());
            refs.add(xref);

        }
    }

    protected void addEcXrefs(UniprotEntry uniprotEntry, Set<Ref> refs) {

        if (!uniprotEntry.getEnzymePortalEcNumbersSet().isEmpty()) {
            uniprotEntry.getEnzymePortalEcNumbersSet()
                    .stream()
                    .map(ecNumbers -> new Ref(ecNumbers.getEcNumber(), DatabaseName.INTENZ.getDbName()))
                    .forEach(xref -> {
                        refs.add(xref);
                    });
        }
    }

    protected void addCompoundFieldsAndXrefs(UniprotEntry uniprotEntry, Set<Field> fields, Set<Ref> refs) {
        if (!uniprotEntry.getEnzymePortalCompoundSet().isEmpty()) {

            uniprotEntry.getEnzymePortalCompoundSet().stream().map(compound -> {
                Field field = new Field(FieldName.COMPOUND_NAME.getName(), compound.getCompoundName());
                fields.add(field);
                Ref xref = new Ref(compound.getCompoundId(), compound.getCompoundSource());
                return xref;
            }).forEach(xref -> {
                refs.add(xref);
            });
        }
    }

    protected void addDiseaseFields(UniprotEntry uniprotEntry, Set<Field> fields) {
        if (!uniprotEntry.getEnzymePortalDiseaseSet().isEmpty()) {
            uniprotEntry.getEnzymePortalDiseaseSet().stream().map(disease -> {
                Field field = new Field(FieldName.DISEASE_NAME.getName(), disease.getDiseaseName());
                return field;
            }).forEach(field -> {
                fields.add(field);
            });
        }
    }

}