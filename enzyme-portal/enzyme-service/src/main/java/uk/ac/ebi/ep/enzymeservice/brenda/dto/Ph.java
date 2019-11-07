package uk.ac.ebi.ep.enzymeservice.brenda.dto;

import java.util.Objects;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author joseph
 */
@Data
@Builder
@ToString
public class Ph {

    private String ecNumber;
    private String phRange;
    private String phRangeMaximum;
    private String organism;
    private String comment;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.organism);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ph other = (Ph) obj;
        return Objects.equals(this.organism, other.organism);
    }
}