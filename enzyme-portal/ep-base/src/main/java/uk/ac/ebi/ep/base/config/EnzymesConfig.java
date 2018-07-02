/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import uk.ac.ebi.ep.base.search.EnzymeFinder;
import uk.ac.ebi.ep.base.search.EnzymeFinderService;
import uk.ac.ebi.ep.data.service.EnzymePortalService;
import uk.ac.ebi.ep.ebeye.EbeyeRestService;
import uk.ac.ebi.ep.ebeye.ProteinGroupService;
import uk.ac.ebi.ep.enzymeservices.chebi.ChebiConfig;
import uk.ac.ebi.ep.enzymeservices.intenz.IntenzAdapter;
import uk.ac.ebi.ep.enzymeservices.intenz.IntenzConfig;
import uk.ac.ebi.ep.enzymeservices.rhea.IRheaAdapter;
import uk.ac.ebi.ep.enzymeservices.rhea.RheaWsAdapter;

/**
 *
 * @author joseph
 */
@Configuration
@ComponentScan(basePackages = {"uk.ac.ebi.ep.ebeye.config", "uk.ac.ebi.ep.data.service"})
public class EnzymesConfig {

    @Autowired
    private Environment env;
    @Autowired
    private ProteinGroupService proteinGroupService;
    @Autowired
    private EnzymePortalService enzymePortalService;
    @Autowired
    private EbeyeRestService ebeyeRestService;

    @Bean
    public ChebiConfig chebiConfig() {
        ChebiConfig chebiConfig = new ChebiConfig();
        chebiConfig.setTimeout(Integer.parseInt(env.getProperty("chebi.ws.timeout")));
        chebiConfig.setMaxThreads(Integer.parseInt(env.getProperty("chebi.threads.max")));
        chebiConfig.setSearchStars(env.getProperty("chebi.search.stars"));
        chebiConfig.setMaxRetrievedMolecules(Integer.parseInt(env.getProperty("chebi.results.max")));
        chebiConfig.setCompoundBaseUrl(env.getProperty("chebi.compound.base.url"));
        chebiConfig.setCompoundImgBaseUrl(env.getProperty("chebi.compound.img.base.url"));

        return chebiConfig;
    }

    @Bean
    public EnzymeFinderService enzymeFinderService() {

        return new EnzymeFinder(enzymePortalService, ebeyeRestService, proteinGroupService);
    }
    
        @Bean
    public IntenzConfig intenzConfig() {
        IntenzConfig config = new IntenzConfig();

        config.setTimeout(Integer.parseInt(env.getProperty("intenz.ws.timeout")));
        config.setIntenzXmlUrl(env.getProperty("intenz.xml.url"));
        config.setEcBaseUrl("https://www.ebi.ac.uk/intenz/query?cmd=Search&q=");
        return config;
    }

    @Bean
    public IntenzAdapter intenzAdapter() {
        IntenzAdapter intenz = new IntenzAdapter();
        intenz.setConfig(intenzConfig());
        return intenz;
    }
    
    @Bean
    public IRheaAdapter rheaAdapter(){
        IRheaAdapter rheaAdapter = new RheaWsAdapter();
        
        return rheaAdapter;
    }

}
