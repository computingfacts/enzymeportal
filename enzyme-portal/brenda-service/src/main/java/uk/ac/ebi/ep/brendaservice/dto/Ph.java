package uk.ac.ebi.ep.brendaservice.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author joseph
 */
@Data
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ph implements Serializable {

    private String ecNumber;
    private String phRange;
    @EqualsAndHashCode.Include
    private String organism;
    private String comment;
    private String accession;

}
