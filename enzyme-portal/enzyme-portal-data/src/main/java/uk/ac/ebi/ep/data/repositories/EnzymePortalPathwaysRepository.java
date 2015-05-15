/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.data.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.ep.data.domain.EnzymePortalPathways;

/**
 *
 * @author joseph
 */
public interface EnzymePortalPathwaysRepository extends JpaRepository<EnzymePortalPathways, Long>, QueryDslPredicateExecutor<EnzymePortalPathways>, EnzymePortalPathwaysRepositoryCustom {

    EnzymePortalPathways findByPathwayId(Long pathwayId);

    @Query(value = "SELECT * FROM ENZYME_PORTAL_PATHWAYS  WHERE UNIPROT_ACCESSION = :UNIPROT_ACCESSION", nativeQuery = true)
    List<EnzymePortalPathways> findPathwaysByAccession(@Param("UNIPROT_ACCESSION") String accession);

    @Modifying
    @Transactional
    @Query(value = "Insert /*+ IGNORE_ROW_ON_DUPKEY_INDEX(ENZYME_PORTAL_PATHWAYS,IX_ENZYME_PATHWAY_DUPS) */ INTO ENZYME_PORTAL_PATHWAYS "
            + "(UNIPROT_ACCESSION,PATHWAY_ID,PATHWAY_URL,PATHWAY_NAME,STATUS,SPECIES) VALUES (?1,?2,?3,?4,?5,?6)", nativeQuery = true)
    void createPathwayIgnoreDup(String accession, String pathwayId, String pathwayUrl, String pathwayName, String status, String species);

     //Insert into ENZYME_PORTAL_PATHWAYS (UNIPROT_ACCESSION,PATHWAY_ID,PATHWAY_URL,PATHWAY_NAME,STATUS,SPECIES) values ('O43462','REACT_147797','http://www.reactome.org/PathwayBrowser/#REACT_147797','Regulation of cholesterol biosynthesis by SREBP (SREBF)','TAS','Homo sapiens');
}
