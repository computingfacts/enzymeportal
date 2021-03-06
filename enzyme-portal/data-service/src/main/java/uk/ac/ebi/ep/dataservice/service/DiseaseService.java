package uk.ac.ebi.ep.dataservice.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.ep.dataservice.dto.Disease;
import uk.ac.ebi.ep.dataservice.dto.DiseaseView;

/**
 *
 * @author joseph
 */
public interface DiseaseService {

    List<Disease> findDiseasesNameLike(String name);

    List<DiseaseView> findDiseases();

    Page<DiseaseView> getPageableDiseases(Pageable pageable);
}
