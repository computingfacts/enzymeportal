package uk.ac.ebi.ep.xml.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author joseph
 */
@Entity
@Table(name = "INTENZ_ALT_NAMES")
@XmlRootElement
@NamedQuery(name = "IntenzAltNames.findAll", query = "SELECT i FROM IntenzAltNames i")
@NamedQuery(name = "IntenzAltNames.findByInternalId", query = "SELECT i FROM IntenzAltNames i WHERE i.internalId = :internalId")
@NamedQuery(name = "IntenzAltNames.findByAltName", query = "SELECT i FROM IntenzAltNames i WHERE i.altName = :altName")
public class IntenzAltNames implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "INTERNAL_ID")
    private BigDecimal internalId;
    @Column(name = "ALT_NAME")
    private String altName;
    @JoinColumn(name = "EC_NUMBER", referencedColumnName = "EC_NUMBER")
    @ManyToOne
    private EnzymePortalUniqueEc ecNumber;

    public IntenzAltNames() {
}

    public IntenzAltNames(BigDecimal internalId) {
        this.internalId = internalId;
    }

    public BigDecimal getInternalId() {
        return internalId;
    }

    public void setInternalId(BigDecimal internalId) {
        this.internalId = internalId;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public EnzymePortalUniqueEc getEcNumber() {
        return ecNumber;
    }

    public void setEcNumber(EnzymePortalUniqueEc ecNumber) {
        this.ecNumber = ecNumber;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (internalId != null ? internalId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof IntenzAltNames)) {
            return false;
        }
        IntenzAltNames other = (IntenzAltNames) object;
        return !((this.internalId == null && other.internalId != null) || (this.internalId != null && !this.internalId.equals(other.internalId)));
    }


}
