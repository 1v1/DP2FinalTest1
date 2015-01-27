
package it.polito.dp2.FDS.sol4.client.gen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				This response contains the page number of the list,
 * 				the last page indicator and the list of boarded passengers.
 * 			
 * 
 * <p>Java class for getBoardedPassengersResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBoardedPassengersResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://pad.polito.it/FDS}passenger" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lastPage" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="pageNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBoardedPassengersResponse", propOrder = {
    "_return",
    "lastPage",
    "pageNumber"
})
public class GetBoardedPassengersResponse {

    @XmlElement(name = "return")
    protected List<Passenger> _return;
    protected boolean lastPage;
    protected int pageNumber;

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
     * {@link Passenger }
     * 
     * 
     */
    public List<Passenger> getReturn() {
        if (_return == null) {
            _return = new ArrayList<Passenger>();
        }
        return this._return;
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

}
