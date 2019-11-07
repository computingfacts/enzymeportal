package uk.ac.ebi.ep.web.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.ac.ebi.ep.indexservice.helper.IndexFields;
import uk.ac.ebi.ep.indexservice.helper.QueryBuilder;
import uk.ac.ebi.ep.indexservice.model.enzyme.EnzymeEntry;
import uk.ac.ebi.ep.indexservice.model.enzyme.EnzymeSearchResult;
import uk.ac.ebi.ep.indexservice.model.protein.ProteinGroupSearchResult;
import uk.ac.ebi.ep.indexservice.service.EnzymeCentricService;
import uk.ac.ebi.ep.indexservice.service.IndexService;
import uk.ac.ebi.ep.indexservice.service.ProteinCentricService;

/**
 *
 * @author joseph
 */
@Slf4j
@Service
class SearchIndexServiceImpl implements SearchIndexService {

    private static final String RELEVANCE = "_relevance";
    private static final String ENTRY_TYPE_ASC = "entry_type:ascending";
    private static final String FORMAT = "json";

    private final EnzymeCentricService enzymeCentricService;
    private final ProteinCentricService proteinCentricService;
    private final IndexService indexService;

    @Autowired
    SearchIndexServiceImpl(IndexService indexService, EnzymeCentricService enzymeCentricService, ProteinCentricService proteinCentricService) {
        this.enzymeCentricService = enzymeCentricService;
        this.proteinCentricService = proteinCentricService;
        this.indexService = indexService;

    }

    @Override
    public EnzymeSearchResult findEnzyme(String query) {

        List<String> fieldList = Arrays.asList(IndexFields.id.name(), IndexFields.name.name(), IndexFields.alt_names.name(),
                IndexFields.description.name(), IndexFields.enzyme_family.name(), IndexFields.intenz_cofactors.name(), IndexFields.catalytic_activity.name());
        QueryBuilder queryBuilder = QueryBuilder
                .builder()
                .query(query)
                .start(0)
                .size(1)
                .fields(fieldList)
                .sort(RELEVANCE)
                .reverse(Boolean.TRUE)
                .format(FORMAT)
                .build();

        return enzymeCentricService.searchForEnzymes(queryBuilder);
    }

    @Override
    public EnzymeSearchResult findEnzyme(String query, int startPage, int pageSize, int facetCount, List<String> facetList) {

        String facets = facetList.stream().collect(Collectors.joining(","));

        List<String> fieldList = Arrays.asList(IndexFields.id.name(), IndexFields.name.name(), IndexFields.enzyme_family.name(), IndexFields.intenz_cofactors.name());
        QueryBuilder queryBuilder = QueryBuilder
                .builder()
                .query(query)
                .facetcount(facetCount)
                .facets(facets)
                .start(startPage * pageSize)
                .size(pageSize)
                .fields(fieldList)
                .sort(RELEVANCE)
                .reverse(Boolean.TRUE)
                .format(FORMAT)
                .build();

        return enzymeCentricService.searchForEnzymes(queryBuilder);

    }

    @Override
    public ProteinGroupSearchResult findAssociatedProtein(String query, int pageSize) {

        List<String> fieldList = Arrays.asList(IndexFields.id.name(), IndexFields.name.name(),
                IndexFields.primary_accession.name(), IndexFields.primary_organism.name(), IndexFields.related_species.name(),
                IndexFields.with_cofactor.name(), IndexFields.with_metabolite.name(), IndexFields.with_pathway.name(), IndexFields.with_disease.name(), IndexFields.with_protein_family.name(), IndexFields.with_taxonomy.name());
        QueryBuilder queryBuilder = QueryBuilder
                .builder()
                .query(query)
                .start(0)
                .size(pageSize)
                .fields(fieldList)
                .sort(ENTRY_TYPE_ASC)
                .reverse(Boolean.FALSE)
                .format(FORMAT)
                .build();

        return proteinCentricService.searchForProteins(queryBuilder);
    }

    @Override
    public ProteinGroupSearchResult findProteinResult(String query, int startPage, int pageSize, int facetCount, List<String> facetList) {
        String facets = facetList.stream().collect(Collectors.joining(","));

        List<String> fieldList = Arrays.asList(IndexFields.id.name(), IndexFields.name.name(),
                IndexFields.primary_accession.name(), IndexFields.primary_organism.name(),
                IndexFields.primary_image.name(), IndexFields.function.name(), IndexFields.disease_name.name(), IndexFields.catalytic_activity.name(),
                IndexFields.related_species.name(), IndexFields.alt_names.name(), IndexFields.gene_name.name(), IndexFields.ec.name(), IndexFields.entry_type.name(),
                IndexFields.with_cofactor.name(), IndexFields.with_metabolite.name(), IndexFields.with_disease.name(), IndexFields.with_protein_family.name(), IndexFields.with_taxonomy.name(), IndexFields.with_pathway.name());
        QueryBuilder queryBuilder = QueryBuilder
                .builder()
                .query(query)
                .facetcount(facetCount)
                .facets(facets)
                .start(startPage * pageSize)
                .size(pageSize)
                .fields(fieldList)
                .sort(ENTRY_TYPE_ASC)
                .reverse(Boolean.FALSE)
                .format(FORMAT)
                .build();

        return proteinCentricService.searchForProteins(queryBuilder);
    }

    @Override
    public Mono<EnzymeEntry> getEnzymePageEntry(String query) {
        List<String> fieldList = Arrays.asList(IndexFields.id.name(), IndexFields.name.name(), IndexFields.alt_names.name(),
                IndexFields.description.name(), IndexFields.enzyme_family.name(), IndexFields.intenz_cofactors.name(), IndexFields.catalytic_activity.name());
        QueryBuilder queryBuilder = QueryBuilder
                .builder()
                .query(query)
                .start(0)
                .size(1)
                .fields(fieldList)
                .sort(RELEVANCE)
                .reverse(Boolean.TRUE)
                .format(FORMAT)
                .build();

        return indexService.getEnzymePageEntry(queryBuilder);

    }

}