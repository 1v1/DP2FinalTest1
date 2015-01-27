
package it.polito.dp2.FDS.sol4.server.jaxws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for flightInstanceStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="flightInstanceStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ARRIVED"/>
 *     &lt;enumeration value="BOARDING"/>
 *     &lt;enumeration value="BOOKING"/>
 *     &lt;enumeration value="CANCELLED"/>
 *     &lt;enumeration value="CHECKINGIN"/>
 *     &lt;enumeration value="DEPARTED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "flightInstanceStatus")
@XmlEnum
public enum FlightInstanceStatus {

    ARRIVED,
    BOARDING,
    BOOKING,
    CANCELLED,
    CHECKINGIN,
    DEPARTED;

    public String value() {
        return name();
    }

    public static FlightInstanceStatus fromValue(String v) {
        return valueOf(v);
    }

}
