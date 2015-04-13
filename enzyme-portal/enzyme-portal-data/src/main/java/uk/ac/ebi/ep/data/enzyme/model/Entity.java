package uk.ac.ebi.ep.data.enzyme.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



/**
 * <p>Java class for Entity complex type.
 * 

 * 
 * 
 */

public class Entity
    implements Serializable
{

  
    protected String id;
  
    protected String name;
 
    protected String description;
  
 
    protected Object url;

    protected List<Object> xrefs;
    protected List<String> evidence;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setUrl(Object value) {
        this.url = value;
    }

    /**
     * Gets the value of the xrefs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xrefs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXrefs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getXrefs() {
        if (xrefs == null) {
            xrefs = new ArrayList<Object>();
        }
        return this.xrefs;
    }

    /**
     * Gets the value of the evidence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the evidence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEvidence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getEvidence() {
        if (evidence == null) {
            evidence = new ArrayList<String>();
        }
        return this.evidence;
    }

    

    public Entity withId(String value) {
        setId(value);
        return this;
    }

    public Entity withName(String value) {
        setName(value);
        return this;
    }

    public Entity withDescription(String value) {
        setDescription(value);
        return this;
    }

    public Entity withUrl(Object value) {
        setUrl(value);
        return this;
    }

    public Entity withXrefs(Object... values) {
        if (values!= null) {
            for (Object value: values) {
                getXrefs().add(value);
            }
        }
        return this;
    }

    public Entity withXrefs(Collection<Object> values) {
        if (values!= null) {
            getXrefs().addAll(values);
        }
        return this;
    }

    public Entity withEvidence(String... values) {
        if (values!= null) {
            for (String value: values) {
                getEvidence().add(value);
            }
        }
        return this;
    }

    public Entity withEvidence(Collection<String> values) {
        if (values!= null) {
            getEvidence().addAll(values);
        }
        return this;
    }

    /**
     * Sets the value of the xrefs property.
     * 
     * @param xrefs
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setXrefs(List<Object> xrefs) {
        this.xrefs = xrefs;
    }

    /**
     * Sets the value of the evidence property.
     * 
     * @param evidence
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }
    
}
