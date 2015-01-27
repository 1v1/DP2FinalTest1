
package it.polito.dp2.FDS.sol4.server.jaxws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "SeatAlreadyAssigned", targetNamespace = "http://pad.polito.it/FDS")
public class SeatAlreadyAssigned_Exception
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private SeatAlreadyAssigned faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public SeatAlreadyAssigned_Exception(String message, SeatAlreadyAssigned faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public SeatAlreadyAssigned_Exception(String message, SeatAlreadyAssigned faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: it.polito.dp2.FDS.sol4.server.jaxws.SeatAlreadyAssigned
     */
    public SeatAlreadyAssigned getFaultInfo() {
        return faultInfo;
    }

}
