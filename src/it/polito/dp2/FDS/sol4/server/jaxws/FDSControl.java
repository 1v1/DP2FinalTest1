
package it.polito.dp2.FDS.sol4.server.jaxws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "FDSControl", targetNamespace = "http://pad.polito.it/FDS", wsdlLocation = "file:/Users/emdotzed/Documents/workspace/DP2FinalTest1/build/META-INF/server/wsdl/FDSControl.wsdl")
public class FDSControl
    extends Service
{

    private final static URL FDSCONTROL_WSDL_LOCATION;
    private final static WebServiceException FDSCONTROL_EXCEPTION;
    private final static QName FDSCONTROL_QNAME = new QName("http://pad.polito.it/FDS", "FDSControl");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/Users/emdotzed/Documents/workspace/DP2FinalTest1/build/META-INF/server/wsdl/FDSControl.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        FDSCONTROL_WSDL_LOCATION = url;
        FDSCONTROL_EXCEPTION = e;
    }

    public FDSControl() {
        super(__getWsdlLocation(), FDSCONTROL_QNAME);
    }

    public FDSControl(WebServiceFeature... features) {
        super(__getWsdlLocation(), FDSCONTROL_QNAME, features);
    }

    public FDSControl(URL wsdlLocation) {
        super(wsdlLocation, FDSCONTROL_QNAME);
    }

    public FDSControl(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, FDSCONTROL_QNAME, features);
    }

    public FDSControl(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public FDSControl(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns Control
     */
    @WebEndpoint(name = "FDSControlImplPort")
    public Control getFDSControlImplPort() {
        return super.getPort(new QName("http://pad.polito.it/FDS", "FDSControlImplPort"), Control.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns Control
     */
    @WebEndpoint(name = "FDSControlImplPort")
    public Control getFDSControlImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://pad.polito.it/FDS", "FDSControlImplPort"), Control.class, features);
    }

    private static URL __getWsdlLocation() {
        if (FDSCONTROL_EXCEPTION!= null) {
            throw FDSCONTROL_EXCEPTION;
        }
        return FDSCONTROL_WSDL_LOCATION;
    }

}
