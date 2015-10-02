/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.data.repositories;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;
import uk.ac.ebi.ep.data.domain.EnzymePortalDisease;
import uk.ac.ebi.ep.data.search.model.Disease;

/**
 * @author joseph
 */
@NoRepositoryBean
public interface DiseaseRepositoryCustom {


    List<EnzymePortalDisease> findDiseasesByAccessions(List<String> accessions);

    List<EnzymePortalDisease> findDiseasesByNamePrefixes(List<String> namePrefixes);

    List<String> findAccessionsByOmimId(String meshId);

    List<EnzymePortalDisease> findDiseases();

    List<Disease> findDiseasesNameLike(String name);
    
    List<Disease> findDiseasesByTaxId(Long taxId);
    
     List<Disease> findDiseasesByAccession(String accession);
     
     List<Disease> findDiseasesByEcNumber(String ecNumber);

}
