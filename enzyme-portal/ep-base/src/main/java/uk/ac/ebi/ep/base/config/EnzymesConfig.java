/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.ep.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.ep.adapter.literature.LiteratureConfig;
import uk.ac.ebi.ep.ebeye.EbeyeRestService;
import uk.ac.ebi.ep.enzymeservices.chebi.ChebiConfig;
import uk.ac.ebi.ep.uniprotservice.blast.UniprotBlastService;
import uk.ac.ebi.ep.uniprotservice.blast.UniprotProdBlastService;

/**
 *
 * @author joseph
 */
@Configuration
public class EnzymesConfig {

    @Autowired
    private Environment env;

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
    public LiteratureConfig literatureConfig() {
        LiteratureConfig lc = new LiteratureConfig();
        lc.setMaxThreads(Integer.parseInt(env.getProperty("literature.threads.max")));
        lc.setUseCitexploreWs(Boolean.parseBoolean(env.getProperty("literature.citexplore.ws")));
        lc.setMaxCitations(Integer.parseInt(env.getProperty("literature.results.max")));
        lc.setCitexploreClientPoolSize(Integer.parseInt(env.getProperty("literature.citexplore.client.pool.size")));
        lc.setCitexploreConnectTimeout(Integer.parseInt(env.getProperty("literature.citexplore.ws.timeout.connect")));
        lc.setCitexploreReadTimeout(Integer.parseInt(env.getProperty("literature.citexplore.ws.timeout.read")));
        return lc;
    }
    
    @Bean
    public EbeyeRestService ebeyeRestService() {
        return new EbeyeRestService();
    }



    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        return new AsyncRestTemplate();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory();
        
    }
    
    @Bean
    public UniprotBlastService blastService(){
        return new UniprotBlastService();
    }
    
    @Bean
    public UniprotProdBlastService prodBlastService(){
        return new UniprotProdBlastService();
    }

}
