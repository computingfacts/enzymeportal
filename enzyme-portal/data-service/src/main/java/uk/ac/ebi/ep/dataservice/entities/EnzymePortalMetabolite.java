package uk.ac.ebi.ep.dataservice.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author joseph
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "ENZYME_PORTAL_METABOLITE")
@XmlRootElement
@NamedQuery(name = "EnzymePortalMetabolite.findAll", query = "SELECT e FROM EnzymePortalMetabolite e")
@NamedQuery(name = "EnzymePortalMetabolite.findByMetaboliteInternalId", query = "SELECT e FROM EnzymePortalMetabolite e WHERE e.metaboliteInternalId = :metaboliteInternalId")
@NamedQuery(name = "EnzymePortalMetabolite.findByMetaboliteId", query = "SELECT e FROM EnzymePortalMetabolite e WHERE e.metaboliteId = :metaboliteId")
@NamedQuery(name = "EnzymePortalMetabolite.findByMetaboliteName", query = "SELECT e FROM EnzymePortalMetabolite e WHERE e.metaboliteName = :metaboliteName")
@NamedQuery(name = "EnzymePortalMetabolite.findByMetaboliteUrl", query = "SELECT e FROM EnzymePortalMetabolite e WHERE e.metaboliteUrl = :metaboliteUrl")
public class EnzymePortalMetabolite implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "METABOLITE_INTERNAL_ID")
    private long metaboliteInternalId;
    @Size(max = 50)
    @Column(name = "METABOLITE_ID")
    private String metaboliteId;
    @Size(max = 2000)
    @Column(name = "METABOLITE_NAME")
    private String metaboliteName;
    @Size(max = 250)
    @Column(name = "METABOLITE_URL")
    private String metaboliteUrl;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.metaboliteId);
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
        final EnzymePortalMetabolite other = (EnzymePortalMetabolite) obj;
        return Objects.equals(this.metaboliteId, other.metaboliteId);
    }

}
