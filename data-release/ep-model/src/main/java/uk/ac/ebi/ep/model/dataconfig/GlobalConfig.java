package uk.ac.ebi.ep.model.dataconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.ep.model.service.AnalysisService;
import uk.ac.ebi.ep.model.service.EnzymePortalParserService;
import uk.ac.ebi.ep.model.service.SitemapService;
import uk.ac.ebi.ep.model.service.WebStatService;
import uk.ac.ebi.ep.model.service.WebStatService;

/**
 *
 * @author joseph
 */
@Configuration
public class GlobalConfig {

    @Bean
    public AnalysisService analysisService() {
        return new AnalysisService();
    }

    @Bean
    public SitemapService sitemapService() {
        return new SitemapService();
    }

    @Bean
    public EnzymePortalParserService enzymePortalParserService() {
        return new EnzymePortalParserService();
    }

    @Bean
    public WebStatService webStatService() {
        return new WebStatService();
    }
}
