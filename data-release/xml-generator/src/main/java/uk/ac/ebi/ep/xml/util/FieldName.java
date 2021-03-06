package uk.ac.ebi.ep.xml.util;

/**
 *
 * @author Joseph <joseph@ebi.ac.uk>
 */
public enum FieldName {
    WITH_TAXONOMY("with_taxonomy"), WITH_PROTEIN_FAMILY("with_protein_family"), WITH_DISEASE("with_disease"), WITH_COFACTOR("with_cofactor"), WITH_PATHWAY("with_pathway"),WITH_METABOLITE("with_metabolite"),
//    HAS_TAXONOMY("has_taxonomy"), HAS_PROTEIN_FAMILY("has_protein_family"), HAS_DISEASE("has_disease"), HAS_COFACTOR("has_cofactor"), HAS_PATHWAY("has_pathway"),HAS_METABOLITE("has_metabolite"),
    GROUP_ACCESSION("group_accession"),
    CHEBI_ID("chebi_id"),CHEBIID("chebiid"), PROTEIN_FAMILY_NAME("protein_family_name"), PROTEIN_FAMILY_ID("protein_family_id"), REACTANT("reactant"), RHEA_ID("rhea_id"),RHEAID("rheaid"), UNIPROT_NAME("uniprot_name"), ENTRY_TYPE("entry_type"), FUNCTION("function"), PROTEIN_NAME("protein_name"), GENE_NAME("gene_name"),
    SCIENTIFIC_NAME("scientific_name"), COMMON_NAME("common_name"), SYNONYM("synonym"), STATUS("status"), SOURCE("source"),
    COFACTOR("cofactor"), INHIBITOR("inhibitor"), ACTIVATOR("activator"), COFACTOR_NAME("cofactor_name"), INHIBITOR_NAME("inhibitor_name"), ACTIVATOR_NAME("activator_name"),
    PRIMARY_IMAGE("primary_image"), PRIMARY_IMAGE_SPECIE("primary_image_specie"),PRIMARY_EC("primary_ec"), EC("ec"), CATALYTIC_ACTIVITY("catalytic_activity"),
    COMPOUND_NAME("compound_name"), COMPOUND_TYPE("compound_type"), DISEASE_NAME("disease_name"), ENZYME_FAMILY("enzyme_family"),
    TRANSFER_FLAG("transfer_flag"), INTENZ_COFACTORS("intenz_cofactors"), INTENZ_ALT_NAMES("alt_names"),
    RELATED_SPECIES("related_species"), PRIMARY_ACCESSION("primary_accession"), PRIMARY_ORGANISM("primary_organism"),
    CHEBI_SYNONYMS("chebi_synonyms"),METABOLITE("metabolite"),METABOLITE_NAME("metabolite_name"),PATHWAY_NAME("pathway_name");

    FieldName(String name) {
        this.name = name;
    }
    private final String name;

    public String getName() {
        return name;
    }

}
