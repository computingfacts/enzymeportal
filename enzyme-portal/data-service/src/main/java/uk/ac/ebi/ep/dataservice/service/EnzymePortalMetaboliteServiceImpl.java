package uk.ac.ebi.ep.dataservice.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.ep.dataservice.dto.Metabolite;
import uk.ac.ebi.ep.dataservice.dto.MetaboliteView;
import uk.ac.ebi.ep.dataservice.repositories.EnzymePortalMetaboliteRepository;

/**
 *
 * @author joseph
 */
@Transactional(readOnly = true)
@Service
public class EnzymePortalMetaboliteServiceImpl implements EnzymePortalMetaboliteService {

    private final EnzymePortalMetaboliteRepository metaboliteRepository;

    @Autowired
    public EnzymePortalMetaboliteServiceImpl(EnzymePortalMetaboliteRepository metaboliteRepository) {
        this.metaboliteRepository = metaboliteRepository;
    }

    @Override
    public List<MetaboliteView> findMetabolites() {
        return metaboliteRepository.findMetabolites();
    }

    @Override
    public List<Metabolite> findMetaboliteNameLike(String name) {
        return metaboliteRepository.findMetaboliteNameLike(name);
    }

}