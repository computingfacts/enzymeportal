//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.21 at 10:28:29 AM CET 
//


package uk.ac.ebi.ep.domain.mesh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "descriptorUI",
    "descriptorName"
})
@XmlRootElement(name = "DescriptorReferredTo")
public class DescriptorReferredTo {

    @XmlElement(name = "DescriptorUI", required = true)
    protected String descriptorUI;
    @XmlElement(name = "DescriptorName", required = true)
    protected DescriptorName descriptorName;

    /**
     * Gets the value of the descriptorUI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescriptorUI() {
        return descriptorUI;
    }

    /**
     * Sets the value of the descriptorUI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescriptorUI(String value) {
        this.descriptorUI = value;
    }

    /**
     * Gets the value of the descriptorName property.
     * 
     * @return
     *     possible object is
     *     {@link DescriptorName }
     *     
     */
    public DescriptorName getDescriptorName() {
        return descriptorName;
    }

    /**
     * Sets the value of the descriptorName property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescriptorName }
     *     
     */
    public void setDescriptorName(DescriptorName value) {
        this.descriptorName = value;
    }

}
