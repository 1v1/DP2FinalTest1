package it.polito.dp2.FDS.sol4.server;

import it.polito.dp2.FDS.Aircraft;
import it.polito.dp2.FDS.FlightInstanceReader;
import it.polito.dp2.FDS.FlightMonitor;
import it.polito.dp2.FDS.FlightMonitorException;
import it.polito.dp2.FDS.FlightMonitorFactory;
import it.polito.dp2.FDS.FlightReader;
import it.polito.dp2.FDS.MalformedArgumentException;
import it.polito.dp2.FDS.PassengerReader;
import it.polito.dp2.FDS.sol4.server.jaxws.AircraftType;
import it.polito.dp2.FDS.sol4.server.jaxws.Flight;
import it.polito.dp2.FDS.sol4.server.jaxws.FlightInstance;
import it.polito.dp2.FDS.sol4.server.jaxws.FlightInstanceStatus;
import it.polito.dp2.FDS.sol4.server.jaxws.Passenger;
import it.polito.dp2.FDS.sol4.server.jaxws.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DataManager {

	// Lazy initialization and Singleton pattern
	private static FlightMonitor monitor;
	private static Set<AircraftType> aircraftsSet = null;
	//			new HashSet<AircraftType>(); // read-only
	private static ConcurrentHashMap<FlightInstanceKey, FlightInstance> flightInstancesMap = null;
	//			new ConcurrentHashMap<FlightInstanceKey, FlightInstance>();
	private static ConcurrentHashMap<String,Flight> flightsMap = null;
	//			new ConcurrentHashMap<String, Flight>();
	private static ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>> fplist_map = null;
	//			new ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>>();

//	private static Logger logger = Logger.getLogger(DataManager.class.getName());

	private static class DataManagerHolder {
		private final static DataManager INSTANCE = new DataManager();
	}

	public static DataManager getInstance() {
		return DataManagerHolder.INSTANCE;
	}

	// Protected constructor is sufficient to suppress unauthorized calls to the
	// constructor
	private DataManager(){
		FlightMonitorFactory factory = FlightMonitorFactory.newInstance();
		try {
			monitor = factory.newFlightMonitor();
		} catch (FlightMonitorException e) {
			e.printStackTrace();
		}
//		logger.fine("DataManager created.");
	}

	private Set<AircraftType> createAircraftsSet()
	{
		// Lazy initialization method for the aircrafts set
		Set<Aircraft> a_set = DataManager.monitor.getAircrafts();
		Set<AircraftType> res = new HashSet<AircraftType>();
		for (Aircraft a:a_set)
		{
			AircraftType air = new AircraftType();
			air.setModel(a.model);
			air.getSeat().addAll(a.seats);
			res.add(air);
		}
		return res;
	}

	private ConcurrentHashMap<FlightInstanceKey, FlightInstance> createFlightInstancesMap()
			throws DatatypeConfigurationException, MalformedArgumentException
			{
		List<FlightInstanceReader> FlightInstances_list = monitor.getFlightInstances(null, null,null);
		ConcurrentHashMap<FlightInstanceKey, FlightInstance> res =
				new ConcurrentHashMap<FlightInstanceKey, FlightInstance>();

		for (FlightInstanceReader fi:FlightInstances_list)
		{
			FlightInstance flightInstance = new FlightInstance();
			flightInstance.setDelay(fi.getDelay());
			flightInstance.setDepartureGate(fi.getDepartureGate());
			flightInstance.setFlightID(fi.getFlight().getNumber());
			flightInstance.setStatus(FlightInstanceStatus.valueOf(fi.getStatus().toString()));

			GregorianCalendar cal = fi.getDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddXXX");
			sdf.setTimeZone(cal.getTimeZone());
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			String date = sdf.format(cal.getTime());

			XMLGregorianCalendar departureDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);

			flightInstance.setDate(departureDate);


			AircraftType a_fi = new AircraftType();
			a_fi.setModel(fi.getAircraft().model);
			a_fi.getSeat().addAll(fi.getAircraft().seats);
			flightInstance.setAircraft(a_fi);

			FlightInstanceKey key = new FlightInstanceKey(flightInstance.getFlightID(), departureDate);

			res.put(key, flightInstance);
		}
		return res;
			}

	private ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>> createFplist_map()
			throws DatatypeConfigurationException, MalformedArgumentException
			{
		List<FlightInstanceReader> FlightInstances_list = monitor.getFlightInstances(null, null,null);
		ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>> res =
				new ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>>();

		for (FlightInstanceReader fi:FlightInstances_list)
		{
			FlightInstance flightInstance = new FlightInstance();
			flightInstance.setDelay(fi.getDelay());
			flightInstance.setDepartureGate(fi.getDepartureGate());
			flightInstance.setFlightID(fi.getFlight().getNumber());
			flightInstance.setStatus(FlightInstanceStatus.valueOf(fi.getStatus().toString()));

			GregorianCalendar cal = fi.getDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddXXX");
			sdf.setTimeZone(cal.getTimeZone());
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			String date = sdf.format(cal.getTime());

			XMLGregorianCalendar departureDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);

			flightInstance.setDate(departureDate);


			AircraftType a_fi = new AircraftType();
			a_fi.setModel(fi.getAircraft().model);
			a_fi.getSeat().addAll(fi.getAircraft().seats);
			flightInstance.setAircraft(a_fi);

			FlightInstanceKey key = new FlightInstanceKey(flightInstance.getFlightID(), departureDate);

			CopyOnWriteArrayList<Passenger> passList = new CopyOnWriteArrayList<Passenger>();

			// Convert PassengerReader into Web Service's Passenger type 
			for (PassengerReader p:fi.getPassengerReaders(null))
			{
				Passenger passenger = new Passenger();
				passenger.setBoarded(p.boarded());
				passenger.setDepartureDate(departureDate);
				passenger.setFlightID(flightInstance.getFlightID());
				passenger.setSeat(p.getSeat());
				passenger.setName(p.getName());
				passList.add(passenger);
				if ( (passenger.getSeat()==null) && (passenger.getFlightID().equals("HC890")) &&
						(fi.getStatus().toString().equals(FlightInstanceStatus.CHECKINGIN.toString())) )
				{
					System.out.println("DATE="+fi.getDate().get(Calendar.DAY_OF_MONTH)+"/"+
				fi.getDate().get(Calendar.MONTH)+"/"+fi.getDate().get(Calendar.YEAR)+" NAME="+passenger.getName());
				}
			}

			res.put(key, passList);
		}
		return res;
			}

	private ConcurrentHashMap<String, Flight> createFlightsMap() throws MalformedArgumentException
	{
		ConcurrentHashMap<String, Flight> res =
				new ConcurrentHashMap<String, Flight>();
		List<FlightReader> Flights_list = monitor.getFlights(null, null, null);

		for (FlightReader f:Flights_list)
		{
			Flight flight = new Flight();
			flight.setDepartureAirport(f.getDepartureAirport());

			Time departureTime = new Time();
			departureTime.setHour(f.getDepartureTime().getHour());
			departureTime.setMinute(f.getDepartureTime().getMinute());
			flight.setDepartureTime(departureTime);

			flight.setNumber(f.getNumber());
			flight.setDestinationAirport(f.getDestinationAirport());

			// Add this Flight element to FlightsMap
			res.put(flight.getNumber(), flight);
		}
		return res;
	}


	public Set<AircraftType> getAircrafts()
	{
		Set<AircraftType> res = aircraftsSet;

		if (res == null) // First check (no locking)
		{
			synchronized (this) {
				res = aircraftsSet;
				if (res == null) // Second check (with locking)
					aircraftsSet = res = createAircraftsSet();
			}
		}
		return res;
	}

	public ConcurrentHashMap<FlightInstanceKey, FlightInstance> getflightInstancesMap() throws DataManagerException
	{
		ConcurrentHashMap<FlightInstanceKey, FlightInstance> res = flightInstancesMap;
		if(res == null)
		{
			synchronized(this)
			{
				res = flightInstancesMap;
				if (res == null)
					try {
						flightInstancesMap = res = createFlightInstancesMap();
					} catch (DatatypeConfigurationException
							| MalformedArgumentException e) {
						throw new DataManagerException();
					}
			}
		}
		return res;
	}

	public ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>> getFplist_map() throws DataManagerException
	{
		ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>> res = fplist_map;

		if (res == null)
		{
			synchronized(this)
			{
				res = fplist_map;
				if (res == null)
				{
					try {
						res = fplist_map = createFplist_map();
					} catch (DatatypeConfigurationException
							| MalformedArgumentException e) {
						throw new DataManagerException();
					}
				}
			}
		}
		return res;
	}

	public ConcurrentHashMap<String, Flight> getFlightsMap() throws DataManagerException
	{
		ConcurrentHashMap<String, Flight> res = DataManager.flightsMap;

		if (res == null)
		{
			synchronized(this)
			{
				res = DataManager.flightsMap;
				if ( res == null)
				{
					try {
						res = flightsMap = createFlightsMap();
					} catch (MalformedArgumentException e) {
						throw new DataManagerException();
					}
				}
			}
		}
		return res;
	}

	public void putflightInstancesMap(FlightInstanceKey key, FlightInstance value) throws DataManagerException
	{
		if (key == null)
		{
//			logger.info("Invalid key");
			throw new DataManagerException("Invalid key");
		}

		ConcurrentHashMap<FlightInstanceKey, FlightInstance> res = flightInstancesMap;
		if(res == null)
		{
			synchronized(this)
			{
				res = flightInstancesMap;
				if (res == null)
					try {
						flightInstancesMap = res = createFlightInstancesMap();
						flightInstancesMap.put(key, value);
//						logger.info("KEY="+key+" added to FlightInstancesMap");
					} catch (DatatypeConfigurationException
							| MalformedArgumentException e) {
//						logger.warning("Impossible adding "+key+" to FlightInstancesMap");
						throw new DataManagerException();
					}
			}
		}
	}

	public void putFplist_map(FlightInstanceKey key, CopyOnWriteArrayList<Passenger> value) throws DataManagerException
	{
		if (key == null)
		{
//			logger.info("Invalid key");
			throw new DataManagerException("Invalid key");
		}

		ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>> res = fplist_map;
		if (res == null)
		{
			synchronized(this)
			{
				res = fplist_map;
				if (res == null)
				{
					try {
						res = fplist_map = createFplist_map();
						fplist_map.put(key, value);
//						logger.info("KEY="+key+" added to FPListMap");
					} catch (DatatypeConfigurationException
							| MalformedArgumentException e) {
//						logger.warning("Impossible adding "+key+" to FPListMap");
						throw new DataManagerException();
					}
				}
			}
		}
	}

	public boolean containsKeyFlightInstancesMap(FlightInstanceKey key) throws DataManagerException
	{
		if (key == null)
		{
//			logger.info("Invalid key");
			throw new DataManagerException("Invalid key");
		}

		ConcurrentHashMap<FlightInstanceKey, FlightInstance> res = flightInstancesMap;
		if(res == null)
		{
			synchronized(this)
			{
				res = flightInstancesMap;
				if (res == null)
					try {
						flightInstancesMap = res = createFlightInstancesMap();
					} catch (DatatypeConfigurationException
							| MalformedArgumentException e) {
//						logger.warning("Error while looking for "+key+" in FlightInstancesMap");
						throw new DataManagerException();
					}
			}
		}
		return res.containsKey(key);
	}

	public boolean containsKeyFplist_map(FlightInstanceKey key) throws DataManagerException
	{
		if (key == null)
		{
//			logger.info("Invalid key");
			throw new DataManagerException("Invalid key");
		}

		ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>> res = fplist_map;
		if (res == null)
		{
			synchronized(this)
			{
				res = fplist_map;
				if (res == null)
				{
					try {
						res = fplist_map = createFplist_map();
					} catch (DatatypeConfigurationException
							| MalformedArgumentException e) {
//						logger.warning("Error while looking for "+key+" in FPListMap");
						throw new DataManagerException();
					}
				}
			}
		}
		return res.containsKey(key);
	}

	public boolean containsKeyFlightsMap(String key) throws DataManagerException
	{
		if (key == null)
		{
//			logger.info("Invalid key");
			throw new DataManagerException("Invalid key");
		}

		ConcurrentHashMap<String, Flight> res = DataManager.flightsMap;

		if (res == null)
		{
			synchronized(this)
			{
				res = DataManager.flightsMap;
				if ( res == null)
				{
					try {
						res = flightsMap = createFlightsMap();
					} catch (MalformedArgumentException e) {
//						logger.warning("Error while looking for "+key+" in Flights Map");
						throw new DataManagerException();
					}
				}
			}
		}
		return res.containsKey(key);
	}

	public FlightInstance getFlightInstance(FlightInstanceKey key) throws DataManagerException
	{
		if (key == null)
		{
//			logger.info("Invalid key");
			throw new DataManagerException("Invalid key");
		}

		ConcurrentHashMap<FlightInstanceKey, FlightInstance> res = flightInstancesMap;
		if(res == null)
		{
			synchronized(this)
			{
				res = flightInstancesMap;
				if (res == null)
					try {
						flightInstancesMap = res = createFlightInstancesMap();
					} catch (DatatypeConfigurationException
							| MalformedArgumentException e) {
//						logger.warning("Error while looking for "+key+" in FlightInstancesMap");
						throw new DataManagerException();
					}
			}
		}
		return res.get(key);
	}

	public CopyOnWriteArrayList<Passenger> getPassengerList (FlightInstanceKey key) throws DataManagerException
	{
		if (key == null)
		{
//			logger.info("Invalid key");
			throw new DataManagerException("Invalid key");
		}

		ConcurrentHashMap<FlightInstanceKey, CopyOnWriteArrayList<Passenger>> res = fplist_map;
		if(res == null)
		{
			synchronized(this)
			{
				res = fplist_map;
				if (res == null)
					try {
						fplist_map = res = createFplist_map();
					} catch (DatatypeConfigurationException
							| MalformedArgumentException e) {
//						logger.warning("Error while looking for "+key+" in FlightInstancesMap");
						throw new DataManagerException();
					}
			}
		}
		return res.get(key);
	}

	public Flight getFlight(String key) throws DataManagerException
	{
		if (key == null)
		{
//			logger.info("Invalid key");
			throw new DataManagerException("Invalid key");
		}

		ConcurrentHashMap<String, Flight> res = flightsMap;

		if (res == null)
			synchronized(this)
			{
				res = flightsMap;
				if(res == null)
				{
					try {
						res = flightsMap = createFlightsMap();
					} catch (MalformedArgumentException e) {
//						logger.warning("Impossible looking for "+key+" in Flights Map");
						throw new DataManagerException();
					}
				}
			}
		return res.get(key);
	}
}
