package it.polito.dp2.FDS.sol4.client;

import it.polito.dp2.FDS.FlightInstanceStatus;
import it.polito.dp2.FDS.lab4.CancellationImpossibleException;
import it.polito.dp2.FDS.lab4.FDSFinalTest1Client;
import it.polito.dp2.FDS.lab4.NoSuchFlightInstanceException;
import it.polito.dp2.FDS.lab4.OperationFailedException;
import it.polito.dp2.FDS.sol4.client.gen.CancelFlightInstance;
import it.polito.dp2.FDS.sol4.client.gen.CancelFlightInstanceResponse;
import it.polito.dp2.FDS.sol4.client.gen.Control;
import it.polito.dp2.FDS.sol4.client.gen.FDSControl;
import it.polito.dp2.FDS.sol4.client.gen.FDSInfo;
import it.polito.dp2.FDS.sol4.client.gen.GetFlightInstance;
import it.polito.dp2.FDS.sol4.client.gen.GetFlightInstanceResponse;
import it.polito.dp2.FDS.sol4.client.gen.Info;
import it.polito.dp2.FDS.sol4.client.gen.InvalidArgument_Exception;
import it.polito.dp2.FDS.sol4.client.gen.InvalidStatus_Exception;
import it.polito.dp2.FDS.sol4.client.gen.Monitor_Exception;
import it.polito.dp2.FDS.sol4.client.gen.UnknownFlightInstance_Exception;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPFaultException;


public class FDSFinalTest1ClientImpl implements FDSFinalTest1Client {

	private Control controlProxy;
	private Info infoProxy;
	private URL url;
	private boolean infoWebServiceIsRunning;
	private boolean controlWebServiceIsRunning;

	public FDSFinalTest1ClientImpl()
	{
		infoWebServiceIsRunning = false;
		controlWebServiceIsRunning = false;
	}

	@Override
	public void setCancellationServiceURL(URL url) {
		// TODO Auto-generated method stub
		this.url = url;
	}

	@Override
	public void cancel(String flightNumber, GregorianCalendar departureDate)
			throws NoSuchFlightInstanceException,
			CancellationImpossibleException, OperationFailedException {
		// TODO Auto-generated method stub

		try {
			prepareControlEndpoint();
			CancelFlightInstance req = new CancelFlightInstance();
			XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(departureDate);
			req.setDepartureDate(date);
			req.setFlightID(flightNumber);
			CancelFlightInstanceResponse res = controlProxy.cancelFlightInstance(req);

		} catch(MalformedURLException e) {
			e.printStackTrace();
			throw new OperationFailedException();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			throw new OperationFailedException();
		} catch (InvalidArgument_Exception e) {
			e.printStackTrace();
			throw new OperationFailedException();
		} catch (Monitor_Exception e) {
			e.printStackTrace();
			throw new OperationFailedException();
		} catch (UnknownFlightInstance_Exception e) {
			e.printStackTrace();
			throw new NoSuchFlightInstanceException();
		} catch (SOAPFaultException e) {
			e.printStackTrace();
			throw new CancellationImpossibleException();
		} catch (InvalidStatus_Exception e) {
			e.printStackTrace();
			throw new CancellationImpossibleException();
		}

	}

	@Override
	public FlightInstanceStatus getStatus(String flightNumber,
			GregorianCalendar departureDate)
					throws NoSuchFlightInstanceException, OperationFailedException {

		try {
			prepareInfoEndpoint();
			GetFlightInstance req = new GetFlightInstance();
			XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(departureDate);
			req.setDepartureDate(date);
			req.setFlightID(flightNumber);
			GetFlightInstanceResponse res = infoProxy.getFlightInstance(req);
			if (res == null)
				throw new OperationFailedException();
			FlightInstanceStatus ret = FlightInstanceStatus.valueOf(res.getReturn().getStatus().toString());
			return ret;
		} catch(MalformedURLException e) {
			e.printStackTrace();
			throw new OperationFailedException();
		} catch (InvalidArgument_Exception e) {
			e.printStackTrace();
			throw new OperationFailedException();
		} catch (Monitor_Exception e) {
			e.printStackTrace();
			throw new OperationFailedException();
		} catch (UnknownFlightInstance_Exception e) {
			e.printStackTrace();
			throw new NoSuchFlightInstanceException();
		} catch (SOAPFaultException e) {
			e.printStackTrace();
			throw new NoSuchFlightInstanceException();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			throw new OperationFailedException();
		}
	}

	private void prepareInfoEndpoint() throws MalformedURLException, OperationFailedException
	{

		URL endpointUrl = new URL("http://localhost:8181/fdsinfo");

		try {
			FDSInfo service = new FDSInfo( endpointUrl,
					new QName("http://pad.polito.it/FDS", "FDSInfo"));
			infoProxy = service.getFDSInfoImplPort();

			BindingProvider bindprov = (BindingProvider) infoProxy;
			bindprov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl.toString()+"?wsdl");
		} catch (Exception e) {
			e.printStackTrace();
			throw new OperationFailedException();
		}
	}

	private void prepareControlEndpoint() throws MalformedURLException, OperationFailedException
	{
		if (url==null)
			url=new URL("http://localhost:8182/fdscontrol");


		try {
			URL endpointUrl = url;

			FDSControl service;
			//		try {
			service = new FDSControl( endpointUrl,
					new QName("http://pad.polito.it/FDS", "FDSControl"));
			controlProxy = service.getFDSControlImplPort();

			BindingProvider bindprov = (BindingProvider) controlProxy;
			bindprov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString()+"?wsdl");
		} catch (Exception e) {
			e.printStackTrace();
			throw new OperationFailedException();
		}
	}
}
