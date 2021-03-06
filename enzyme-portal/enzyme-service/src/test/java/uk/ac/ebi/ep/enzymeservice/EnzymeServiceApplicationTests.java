package uk.ac.ebi.ep.enzymeservice;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ebi.ep.restclient.service.RestConfigService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EnzymeServiceApplicationTests {

    @Autowired
    private RestConfigService restConfigService;
    @Autowired
    private WebClient webClient;

    @Test
    public void contextLoads() {
        assertThat(restConfigService).isNotNull();
        assertThat(webClient).isNotNull();
    }

}
