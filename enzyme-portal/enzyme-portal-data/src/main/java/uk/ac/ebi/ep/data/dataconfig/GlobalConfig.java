package uk.ac.ebi.ep.data.dataconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import uk.ac.ebi.ep.data.service.BioPortalService;
import uk.ac.ebi.ep.data.service.DiseaseService;
import uk.ac.ebi.ep.data.service.EnzymePortalService;
import uk.ac.ebi.ep.data.service.RelatedProteinsService;
import uk.ac.ebi.ep.data.service.UniprotEntryService;

/**
 *
 * @author joseph
 */
@Configuration
public class GlobalConfig {
   
    @Bean
    public DiseaseService diseaseService() {
        return new DiseaseService();
    }

    @Bean
    public BioPortalService bioPortalService() {
        return new BioPortalService();
    }

    @Bean
    public UniprotEntryService uniprotEntryService() {
        return new UniprotEntryService();
    }

    @Bean
    public EnzymePortalService enzymePortalService() {
        return new EnzymePortalService();
    }

    @Bean
    public RelatedProteinsService relatedProteinsService() {
        return new RelatedProteinsService();
    }

    @Bean
    public SpelAwareProxyProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }

    
}
