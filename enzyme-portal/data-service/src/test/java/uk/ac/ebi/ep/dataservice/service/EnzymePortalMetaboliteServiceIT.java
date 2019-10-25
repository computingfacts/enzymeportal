package uk.ac.ebi.ep.dataservice.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.ep.dataservice.dto.Metabolite;
import uk.ac.ebi.ep.dataservice.dto.MetaboliteView;

/**
 *
 * @author joseph
 */
@Slf4j
public class EnzymePortalMetaboliteServiceIT extends DataServiceBaseIT {

    @Autowired
    private EnzymePortalMetaboliteService enzymePortalMetaboliteService;

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(enzymePortalMetaboliteService).isNotNull();
    }

    /**
     * Test of findMetabolites method, of class EnzymePortalMetaboliteService.
     */
    @Test
    public void testFindMetabolites() {
        log.info("testFindMetabolites");

        List<MetaboliteView> result = enzymePortalMetaboliteService.findMetabolites();
        assertNotNull(result);
        log.info("Total NUmber of Metabolites found : " + result.size());
        assertThat(result, hasSize(greaterThanOrEqualTo(1)));
    }

    /**
     * Test of findMetaboliteNameLike method, of class
     * EnzymePortalMetaboliteService.
     */
    @Test
    public void testFindMetaboliteNameLike() {
        log.info("testFindMetaboliteNameLike");
        String metaboliteName = "orot";
        String metabolite = "orotate";
        List<Metabolite> result = enzymePortalMetaboliteService.findMetaboliteNameLike(metaboliteName);

        assertNotNull(result);
        assertThat(result, hasSize(greaterThanOrEqualTo(1)));
   
        assertThat(result.stream()
                .filter(x->x.getMetaboliteName().equalsIgnoreCase(metabolite)))
                .containsAnyOf(new Metabolite("CHEBI:30839", metabolite));
    }

}
