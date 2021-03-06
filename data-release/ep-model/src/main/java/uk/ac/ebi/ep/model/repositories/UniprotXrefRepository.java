package uk.ac.ebi.ep.model.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.ep.model.UniprotXref;

/**
 *
 * @author joseph
 */
public interface UniprotXrefRepository extends JpaRepository<UniprotXref, Long> {

    @Query(value = "SELECT * FROM UNIPROT_XREF WHERE SOURCE = :SOURCE", nativeQuery = true)
    List<UniprotXref> findUniprotXrefBySource(@Param("SOURCE") String source);

    @Query(value = "SELECT COUNT (DISTINCT source_id) FROM uniprot_xref WHERE source='PDB'", nativeQuery = true)
    long countUniquePdbIds();

    @Query(value = "SELECT COUNT (distinct accession) FROM uniprot_xref WHERE source='PDB'", nativeQuery = true)
    long countEntriesWithProteinStructure();

    @Query(value = "SELECT count(DISTINCT source_id) FROM uniprot_xref WHERE source='Reactome'", nativeQuery = true)
    long countUniqueReactomeIds();
}
