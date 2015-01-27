//package it.polito.dp2.FDS.sol4.client;
//
//import it.polito.dp2.FDS.lab4.BoardInfo;
//import it.polito.dp2.FDS.lab4.CheckinFailedException;
//import it.polito.dp2.FDS.lab4.FDSCheckinClient;
//import it.polito.dp2.FDS.lab4.MissingDataException;
//import it.polito.dp2.FDS.sol4.client.gen.AssignSeat;
//import it.polito.dp2.FDS.sol4.client.gen.AssignSeatResponse;
//import it.polito.dp2.FDS.sol4.client.gen.BoardingInfo;
//import it.polito.dp2.FDS.sol4.client.gen.Control;
//import it.polito.dp2.FDS.sol4.client.gen.FDSControl;
//import it.polito.dp2.FDS.sol4.client.gen.FullyBookedFlightException;
//import it.polito.dp2.FDS.sol4.client.gen.InvalidArgumentException;
//import it.polito.dp2.FDS.sol4.client.gen.MonitorException;
//import it.polito.dp2.FDS.sol4.client.gen.SeatAlreadyAssignedException;
//import it.polito.dp2.FDS.sol4.client.gen.UnknownFlightInstanceException;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//
//import javax.xml.datatype.DatatypeConfigurationException;
//import javax.xml.datatype.DatatypeFactory;
//import javax.xml.datatype.XMLGregorianCalendar;
//import javax.xml.namespace.QName;
//import javax.xml.ws.BindingProvider;
//import javax.xml.ws.soap.SOAPFaultException;
//
//public class FDSCheckinClientImpl implements FDSCheckinClient {
//
//	private static Control controlProxy;
//	private AssignSeat req = new AssignSeat();
//	private String flightNumber;
//	private GregorianCalendar departureDate;
//	private static URL url;
//
//	public FDSCheckinClientImpl()
//	{
//
//	}
//
//	public static void main (String args[])
//	{
//		try {
//			FDSCheckinClientImpl cl = new FDSCheckinClientImpl();
//			GregorianCalendar gc = new GregorianCalendar();
//			gc.clear();
//			gc.set(Calendar.YEAR, 2010);
//			gc.set(Calendar.MONTH, 1);
//			gc.set(Calendar.DAY_OF_MONTH, 15);
//			cl.setDepartureDate(gc);
//			cl.setFlightNumber("HC890");
//			URL url = new URL("http://localhost:7070/fdscontrol");
//			cl.setServiceURL(url);
//			cl.checkin("Caio Baldo");
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MissingDataException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CheckinFailedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void setServiceURL(URL url) {
//
//		FDSCheckinClientImpl.url = url;
//	}
//
//
//	@Override
//	public void setFlightNumber(String number) {
//
//		flightNumber = number;
//
//	}
//
//	@Override
//	public void setDepartureDate(GregorianCalendar gdate) {
//
//		departureDate = gdate;
//	}
//
//	@Override
//	public BoardInfo checkin(String passengerName) throws MissingDataException,
//	CheckinFailedException {
//
//		prepareControlEndpoint();
//		
//		try {
//			XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(departureDate);
//			req.setFlightID(flightNumber);
//			req.setDepartureDate(date);
//			req.setPassengerName(passengerName);
//
//			AssignSeatResponse res = controlProxy.assignSeat(req);
//
//			BoardingInfo info = res.getReturn();
//
//			BoardInfo boardInfo = new BoardInfo();
//			boardInfo.setDelay(info.getDelay());
//			boardInfo.setGate(info.getGate());
//			boardInfo.setPassengerName(info.getPassengerName());
//			boardInfo.setSeat(info.getSeat());
//
//			return boardInfo;
//		} catch (DatatypeConfigurationException e) {
//			e.printStackTrace();
//			throw new CheckinFailedException();
//		} catch (FullyBookedFlightException e) {
//			e.printStackTrace();
//			throw new CheckinFailedException();
//		} catch (InvalidArgumentException e) {
//			e.printStackTrace();
//			throw new CheckinFailedException();
//		} catch (MonitorException e) {
//			e.printStackTrace();
//			throw new CheckinFailedException();
//		} catch (SeatAlreadyAssignedException e) {
//			e.printStackTrace();
//			throw new CheckinFailedException();
//		} catch (UnknownFlightInstanceException e) {
//			e.printStackTrace();
//			throw new CheckinFailedException();
//		} catch (SOAPFaultException e) {
//			e.printStackTrace();
//			throw new MissingDataException();
//		}
//	}
//
//	private static void prepareControlEndpoint()
//	{
//		try {
//			if (url==null)
//				url=new URL("http://localhost:7070/fdscontrol");
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		
//		URL endpointUrl = url;
//
//		FDSControl service = new FDSControl( endpointUrl,
//				new QName("http://pad.polito.it/FDSControl", "FDSControl"));
//		controlProxy = service.getFDSControlImplPort();
//
//		BindingProvider bindprov = (BindingProvider) controlProxy;
//		bindprov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url+"?wsdl");
//	}
//
//}
