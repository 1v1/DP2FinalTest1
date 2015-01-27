
package it.polito.dp2.FDS.sol4.server.jaxws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				This response contains the page number of the list
 * 				returned, a parameter
 * 				which tells if the returned page is the last
 * 				one and the list of the
 * 				aircrafts.
 * 			
 * 
 * <p>Java class for getAircraftsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getAircraftsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pageNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="lastPage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="return" type="{http://pad.polito.it/FDS}aircraftType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAircraftsResponse", propOrder = {
    "pageNumber",
    "lastPage",
    "_return"
})
public class GetAircraftsResponse {

    protected int pageNumber;
    protected boolean lastPage;
    @XmlElement(name = "return")
    protected List<AircraftType> _return;

    /**
     * Gets the value of the pageNumber property.
     * 
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the value of the pageNumber property.
     * 
     */
    public void setPageNumber(int value) {
        this.pageNumber = value;
    }

    /**
     * Gets the value of the lastPage property.
     * 
     */
    public boolean isLastPage() {
        return lastPage;
    }

    /**
     * Sets the value of the lastPage property.
     * 
     */
    public void setLastPage(boolean value) {
        this.lastPage = value;
    }

    /**
     * Gets the value of the return property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the return property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReturn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AircraftType }
     * 
     * 
     */
    public List<AircraftType> getReturn() {
        if (_return == null) {
            _return = new ArrayList<AircraftType>();
        }
        return this._return;
    }

}
