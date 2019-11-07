package uk.ac.ebi.ep.dataservice.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.ep.dataservice.dto.PathwayView;
import uk.ac.ebi.ep.dataservice.entities.EnzymePortalPathways;

/**
 *
 * @author joseph
 */
@Repository
public interface EnzymePortalPathwaysRepository extends JpaRepository<EnzymePortalPathways, Long>, QuerydslPredicateExecutor<EnzymePortalPathways>, EnzymePortalPathwaysRepositoryCustom {

    @Query(value = "select p.pathwayGroupId as pathwayGroupId,p.pathwayName as pathwayName from EnzymePortalPathways p group by p.pathwayGroupId, p.pathwayName")
    List<PathwayView> findPathways();

    @Query(value = "SELECT PATHWAY_ID FROM ENZYME_PORTAL_PATHWAYS  WHERE UNIPROT_ACCESSION = :UNIPROT_ACCESSION", nativeQuery = true)
    List<String> findPathwayGroupIdsByAccession(@Param("UNIPROT_ACCESSION") String accession);

}