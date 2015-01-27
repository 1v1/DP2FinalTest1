package it.polito.dp2.FDS.sol4.server;

import it.polito.dp2.FDS.sol4.server.jaxws.AssignSeat;
import it.polito.dp2.FDS.sol4.server.jaxws.AssignSeatResponse;
import it.polito.dp2.FDS.sol4.server.jaxws.BoardingInfo;
import it.polito.dp2.FDS.sol4.server.jaxws.CancelFlightInstance;
import it.polito.dp2.FDS.sol4.server.jaxws.CancelFlightInstanceResponse;
import it.polito.dp2.FDS.sol4.server.jaxws.CancelledFlight;
import it.polito.dp2.FDS.sol4.server.jaxws.CancelledFlight_Exception;
import it.polito.dp2.FDS.sol4.server.jaxws.ChangeBoardingGate;
import it.polito.dp2.FDS.sol4.server.jaxws.ChangeBoardingGateResponse;
import it.polito.dp2.FDS.sol4.server.jaxws.ChangeDelay;
import it.polito.dp2.FDS.sol4.server.jaxws.ChangeDelayResponse;
import it.polito.dp2.FDS.sol4.server.jaxws.Control;
import it.polito.dp2.FDS.sol4.server.jaxws.FlightInstance;
import it.polito.dp2.FDS.sol4.server.jaxws.FlightInstanceStatus;
import it.polito.dp2.FDS.sol4.server.jaxws.FullyBookedFlight;
import it.polito.dp2.FDS.sol4.server.jaxws.FullyBookedFlight_Exception;
import it.polito.dp2.FDS.sol4.server.jaxws.GetBoardedPassengers;
import it.polito.dp2.FDS.sol4.server.jaxws.GetBoardedPassengersResponse;
import it.polito.dp2.FDS.sol4.server.jaxws.InvalidArgument;
import it.polito.dp2.FDS.sol4.server.jaxws.InvalidArgument_Exception;
import it.polito.dp2.FDS.sol4.server.jaxws.InvalidStatus;
import it.polito.dp2.FDS.sol4.server.jaxws.InvalidStatus_Exception;
import it.polito.dp2.FDS.sol4.server.jaxws.Monitor;
import it.polito.dp2.FDS.sol4.server.jaxws.Monitor_Exception;
import it.polito.dp2.FDS.sol4.server.jaxws.NotBoarding;
import it.polito.dp2.FDS.sol4.server.jaxws.NotBoarding_Exception;
import it.polito.dp2.FDS.sol4.server.jaxws.Passenger;
import it.polito.dp2.FDS.sol4.server.jaxws.PassengerAlreadyRegistered;
import it.polito.dp2.FDS.sol4.server.jaxws.PassengerAlreadyRegistered_Exception;
import it.polito.dp2.FDS.sol4.server.jaxws.RegisterPassenger;
import it.polito.dp2.FDS.sol4.server.jaxws.RegisterPassengerResponse;
import it.polito.dp2.FDS.sol4.server.jaxws.SeatAlreadyAssigned;
import it.polito.dp2.FDS.sol4.server.jaxws.SeatAlreadyAssigned_Exception;
import it.polito.dp2.FDS.sol4.server.jaxws.StartBoarding;
import it.polito.dp2.FDS.sol4.server.jaxws.StartBoardingResponse;
import it.polito.dp2.FDS.sol4.server.jaxws.UnknownFlightInstance;
import it.polito.dp2.FDS.sol4.server.jaxws.UnknownFlightInstance_Exception;

import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;


@WebService(serviceName="FDSControl",
portName="FDSControlImplPort",
targetNamespace="http://pad.polito.it/FDS",
endpointInterface="it.polito.dp2.FDS.sol4.server.jaxws.Control")
@HandlerChain(file = "META-INF/server/custom/handler-chain.xml")
public class FDSControlImpl implements Control {

	private static DataManager manager;

	private static final Integer MAX_ENTRIES_PER_PAGE = 10;

	//	private static Logger logger = Logger.getLogger(FDSControlImpl.class.getName());

	public FDSControlImpl()
	{
		//		logger.info("FDSControlImpl starting...");
		FDSControlImpl.manager = DataManager.getInstance();
	}



	@Override
	public StartBoardingResponse startBoarding(StartBoarding parameters)
			throws CancelledFlight_Exception, InvalidArgument_Exception,
			UnknownFlightInstance_Exception, InvalidStatus_Exception, Monitor_Exception {

		//		logger.info("startBoarding()");

		FlightInstanceKey key = new FlightInstanceKey(parameters.getFlightID(), parameters.getDepartureDate());

		try {
			if (manager.containsKeyFlightInstancesMap(key))
			{
				FlightInstance f;
				synchronized(manager.getFlightInstance(key))
				{
					f = manager.getFlightInstance(key);
					if (f.getStatus() == FlightInstanceStatus.CHECKINGIN)
					{
						//						logger.info("Flight instance="+f.getFlightID()+" "+f.getDate().toString()+" CHECKING --> BOARDING");
						f.setStatus(FlightInstanceStatus.BOARDING);

						// Update the flightInstancesMap
						manager.putflightInstancesMap(key, f);
						// SUCCESS!
						StartBoardingResponse res = new StartBoardingResponse();
						return res;
					}else if (f.getStatus() == FlightInstanceStatus.CANCELLED)
					{
						//						logger.warning("The requested flight instance has been cancelled");
						CancelledFlight unkFli = new CancelledFlight();
						unkFli.setMessage("The requested flight instance has been cancelled");
						throw new CancelledFlight_Exception("The requested flight instance has been cancelled", unkFli);
					}else
					{
						//						logger.warning("The flight instance status for this flight instance is not valid");
						InvalidStatus invSta = new InvalidStatus();
						invSta.setMessage("The flight instance status for this flight instance is not valid");
						throw new InvalidStatus_Exception("The flight instance status for this flight instance is not valid", invSta);
					}
				}
			}else
			{
				//				logger.warning("The requested flight number is not in our database");
				UnknownFlightInstance unkFli = new UnknownFlightInstance();
				unkFli.setMessage("The requested flight number is not in our database");
				throw new UnknownFlightInstance_Exception("The requested flight number is not in our database", unkFli);
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
			Monitor mon = new Monitor();
			mon.setMessage("Error during lazy initialization");
			throw new Monitor_Exception("Error during lazy initialization", mon);
		}
	}

	@Override
	public RegisterPassengerResponse registerPassenger(
			RegisterPassenger parameters) throws InvalidArgument_Exception,
			NotBoarding_Exception, PassengerAlreadyRegistered_Exception,
			UnknownFlightInstance_Exception, Monitor_Exception {

		//		logger.info("registering a new passenger:"+parameters.getPassengerName());

		FlightInstanceKey key = new FlightInstanceKey(parameters.getFlightID(), parameters.getDepartureDate());

		try {
			if (manager.containsKeyFlightInstancesMap(key))
			{
				FlightInstance flightInstance;

				synchronized(manager.getFlightInstance(key))
				{
					flightInstance = manager.getFlightInstance(key);
					if (flightInstance.getStatus() == FlightInstanceStatus.CHECKINGIN)
					{
						//						logger.info("Flight instance="+flightInstance.getFlightID()+" "+flightInstance.getDate().toString()+" CHECKING --> BOARDING");
						flightInstance.setStatus(FlightInstanceStatus.BOARDING);

						// Update the flightInstancesMap with the updated flightInstance
						manager.putflightInstancesMap(key, flightInstance);
					}

					if (flightInstance.getStatus() != FlightInstanceStatus.BOARDING)
					{
						//						logger.warning("The flight status is not BOARDING");
						NotBoarding notBoa = new NotBoarding();
						notBoa.setMessage("The flight status is not BOARDING");
						throw new NotBoarding_Exception("The flight status is not BOARDING", notBoa);
					}

					synchronized(manager.getPassengerList(key))
					{
						List<Passenger> passengerList = manager.getPassengerList(key);
						for (int i=0; i<passengerList.size(); i++)
						{
							Passenger passenger = passengerList.get(i);

							if (passenger.getName().equals(parameters.getPassengerName()))
							{
								if (passenger.isBoarded())
								{
									//									logger.warning("This passenger has already been registered on this flight instance");
									PassengerAlreadyRegistered passReg = new PassengerAlreadyRegistered();
									passReg.setMessage("This passenger has already been registered on this flight instance");
									throw new PassengerAlreadyRegistered_Exception("Passenger is already on this flight",passReg);
								}else
								{
									//									logger.info(passenger.getName()+" BOARDED!");
									passenger.setBoarded(true);

									// Update the passenger list for this flight instance
									passengerList.remove(i); // passengerList is a copy, so there's no interleave among threads
									passengerList.add(passenger);

									// Update fp_list Map
									manager.putFplist_map(key, manager.getPassengerList(key));

									// SUCCESS!
									RegisterPassengerResponse res = new RegisterPassengerResponse();
									return res;
								}
							}
						}

						//						logger.warning("The specified passenger's name is not valid");
						InvalidArgument invArg = new InvalidArgument();
						invArg.setMessage("The specified passenger's name is not valid");
						throw new InvalidArgument_Exception("The specified passenger's name is not valid", invArg);
					}
				}
			}else
			{
				//				logger.warning("The specified flight number does not exist");
				UnknownFlightInstance unkFli = new UnknownFlightInstance();
				unkFli.setMessage("The specified flight number does not exist");
				throw new UnknownFlightInstance_Exception("The specified flight number does not exist", unkFli);
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
			Monitor mon = new Monitor();
			mon.setMessage("Error during lazy initialization");
			throw new Monitor_Exception("Error during lazy initialization", mon);
		}
	}

	@Override
	public GetBoardedPassengersResponse getBoardedPassengers (GetBoardedPassengers parameters)
			throws InvalidArgument_Exception, UnknownFlightInstance_Exception, Monitor_Exception {

		FlightInstanceKey key = new FlightInstanceKey(parameters.getFlightID(), parameters.getDepartureDate());

		try {
			if (manager.containsKeyFplist_map(key))
			{
				GetBoardedPassengersResponse res = new GetBoardedPassengersResponse();

				// Paging response parameters
				int boardedPassengers = 0;
				int pageNumber = parameters.getPageNumber();
				int entriesPerPage = 0;
				int pageBeginning = pageNumber * FDSControlImpl.MAX_ENTRIES_PER_PAGE;
				int passengers = 0;
				synchronized(manager.getPassengerList(key))
				{
					for (Passenger p:manager.getPassengerList(key))
					{
						passengers++;
						if (p.isBoarded())
						{
							boardedPassengers++;
							if (boardedPassengers > pageBeginning)
							{
								// Boarded passengers not returned yet
								entriesPerPage++;
								res.getReturn().add(p);
								if (entriesPerPage == MAX_ENTRIES_PER_PAGE)
								{
									// Max entries per page limit reached
									res.setPageNumber(pageNumber);
									if (passengers == manager.getPassengerList(key).size())
									{
										res.setLastPage(true);
										//										logger.info("Boarded passengers last page returned!");
									}else
									{
										//										logger.info("Boarded passengers page number "+pageNumber+" returned!");
										res.setLastPage(false);
									}
									return res;
								}
							}
						}
					}
				}
				//				logger.info("Boarded passengers last page returned!");
				res.setLastPage(true);
				res.setPageNumber(pageNumber);
				return res;
			}else
			{
				//				logger.warning("There are no flight instances for the specified departure date");
				UnknownFlightInstance unkFlight = new UnknownFlightInstance();
				unkFlight.setMessage("There are no flight instances for the specified departure date");
				throw new UnknownFlightInstance_Exception("There are no flight instances for the specified departure date",unkFlight);
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
			Monitor mon = new Monitor();
			mon.setMessage("Error during lazy initialization");
			throw new Monitor_Exception("Error during lazy initialization", mon);
		}
	}

	@Override
	public CancelFlightInstanceResponse cancelFlightInstance(
			CancelFlightInstance parameters) throws InvalidArgument_Exception,
			UnknownFlightInstance_Exception, Monitor_Exception, InvalidStatus_Exception
	{
		FlightInstanceKey key = new FlightInstanceKey(parameters.getFlightID(), parameters.getDepartureDate());

		try {
			synchronized(manager.getflightInstancesMap())
			{
				if (manager.containsKeyFlightInstancesMap(key))
				{
					// logger.info("Flight instance="+f.getFlightID()+" "+f.getDate().toString()+" CHECKING --> CANCELLED");
					// Update the flightInstancesMap
					if ( (manager.getFlightInstance(key).getStatus() == FlightInstanceStatus.CANCELLED) ||
							(manager.getFlightInstance(key).getStatus() == FlightInstanceStatus.ARRIVED) ||
							(manager.getFlightInstance(key).getStatus() == FlightInstanceStatus.DEPARTED) )
					{
						InvalidStatus sta = new InvalidStatus();
						sta.setMessage("The status of the flight instance is not valid");
						throw new InvalidStatus_Exception("The selected flight instance can not be cancelled", sta);
					} else
					{
						manager.getFlightInstance(key).setStatus(FlightInstanceStatus.CANCELLED);
						// SUCCESS!
						CancelFlightInstanceResponse res = new CancelFlightInstanceResponse();
						return res;
					}
				}else
				{
					// logger.warning("The requested flight number is not in our database");
					UnknownFlightInstance unkFli = new UnknownFlightInstance();
					unkFli.setMessage("The requested flight number is not in our database");
					throw new UnknownFlightInstance_Exception("The requested flight number is not in our database", unkFli);
				}
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
			Monitor mon = new Monitor();
			mon.setMessage("Error during lazy initialization");
			throw new Monitor_Exception("Error during lazy initialization", mon);
		}
	}

	@Override
	public ChangeBoardingGateResponse changeBoardingGate(
			ChangeBoardingGate parameters) throws InvalidArgument_Exception,
			UnknownFlightInstance_Exception, Monitor_Exception
	{
		if ( (parameters.getGate().isEmpty()) || (parameters.getGate() == null) )
		{
			InvalidArgument invArg = new InvalidArgument();
			invArg.setMessage("The boarding gate is not valid");
			throw new InvalidArgument_Exception("Invalid boarding gate", invArg);
		}

		FlightInstanceKey key = new FlightInstanceKey(parameters.getFlightID(), parameters.getDepartureDate());

		try {
			if (manager.containsKeyFlightInstancesMap(key))
			{
				synchronized(manager.getFlightInstance(key))
				{
					// logger.info("Flight instance="+f.getFlightID()+" "+f.getDate().toString()+" GATE CHANGED");
					// Update the flightInstancesMap
					manager.getFlightInstance(key).setDepartureGate(parameters.getGate());

					// SUCCESS!
					ChangeBoardingGateResponse res = new ChangeBoardingGateResponse();
					return res;
				}
			}else
			{
				// logger.warning("The requested flight number is not in our database");
				UnknownFlightInstance unkFli = new UnknownFlightInstance();
				unkFli.setMessage("The requested flight number is not in our database");
				throw new UnknownFlightInstance_Exception("The requested flight number is not in our database", unkFli);
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
			Monitor mon = new Monitor();
			mon.setMessage("Error during lazy initialization");
			throw new Monitor_Exception("Error during lazy initialization", mon);
		}
	}

	@Override
	public ChangeDelayResponse changeDelay(ChangeDelay parameters)
			throws InvalidArgument_Exception, UnknownFlightInstance_Exception, Monitor_Exception
	{
		FlightInstanceKey key = new FlightInstanceKey(parameters.getFlightID(), parameters.getDepartureDate());

		try {
			if (manager.containsKeyFlightInstancesMap(key))
			{
				synchronized(manager.getFlightInstance(key))
				{
					// logger.info("Flight instance="+f.getFlightID()+" "+f.getDate().toString()+" GATE CHANGED");
					// Update the flightInstancesMap
					manager.getFlightInstance(key).setDelay(parameters.getDelay());
					// SUCCESS!
					ChangeDelayResponse res = new ChangeDelayResponse();
					return res;
				}
			}else
			{
				// logger.warning("The requested flight number is not in our database");
				UnknownFlightInstance unkFli = new UnknownFlightInstance();
				unkFli.setMessage("The requested flight number is not in our database");
				throw new UnknownFlightInstance_Exception("The requested flight number is not in our database", unkFli);
			}
		} catch (DataManagerException e) {
			e.printStackTrace();
			Monitor mon = new Monitor();
			mon.setMessage("Error during lazy initialization");
			throw new Monitor_Exception("Error during lazy initialization", mon);
		}
	}


	@Override
	public AssignSeatResponse assignSeat(AssignSeat parameters)
			throws FullyBookedFlight_Exception, Monitor_Exception,
			SeatAlreadyAssigned_Exception, UnknownFlightInstance_Exception, InvalidArgument_Exception
	{
		FlightInstanceKey key = new FlightInstanceKey(parameters.getFlightID(), parameters.getDepartureDate());
		try {
			if (manager.containsKeyFlightInstancesMap(key))
			{
				synchronized(manager.getFlightInstance(key))
				{
					if (manager.getFlightInstance(key).getStatus() != FlightInstanceStatus.CHECKINGIN)
					{
						InvalidArgument mon = new InvalidArgument();
						mon.setMessage("The selected flight instance is not checking-in now");
						throw new InvalidArgument_Exception("The selected flight instance is not checking-in now", mon);
					}
					synchronized(manager.getPassengerList(key))
					{
						List<Passenger> passengerList = manager.getPassengerList(key);
						for (Passenger p:passengerList)
						{
							if (p.getName().equals(parameters.getPassengerName()))
							{
								if(p.isBoarded())
								{
									SeatAlreadyAssigned saa = new SeatAlreadyAssigned();
									saa.setMessage("The specified passenger has already been boarded");
									throw new SeatAlreadyAssigned_Exception("The specified passenger has already been boarded", saa);
								}
								if (p.getSeat() == null)
								{
									Boolean equalSeatFound = false;
									Boolean seatAssigned = false;
									for (String seat:manager.getFlightInstance(key).getAircraft().getSeat())
									{
										equalSeatFound = false;
										for (Passenger p2:passengerList)
											if (p2.getSeat()!=null)
											{
												if (p2.getSeat().equals(seat))
												{
													equalSeatFound = true;
													break;
												}
											}

										if (!equalSeatFound)
										{
											p.setSeat(seat);
											seatAssigned = true;
											break;
										}
									}
									if (!seatAssigned)
									{
										FullyBookedFlight fbf = new FullyBookedFlight();
										fbf.setMessage("No more seats available for this flight instance");
										throw new FullyBookedFlight_Exception("No more seats available for this flight instance", fbf);
									}else
									{
										// SUCCESS
										AssignSeatResponse res = new AssignSeatResponse();
										BoardingInfo info = new BoardingInfo();
										FlightInstance f = manager.getFlightInstance(key);
										info.setDelay(f.getDelay());
										info.setDepartureDate(f.getDate());
										info.setFlightID(f.getFlightID());
										info.setGate(f.getDepartureGate());
										info.setPassengerName(p.getName());
										info.setSeat(p.getSeat());
										res.setReturn(info);
										return res;
									}
								}else
								{
									SeatAlreadyAssigned saa = new SeatAlreadyAssigned();
									saa.setMessage("The specified passenger has already been checked-in");
									throw new SeatAlreadyAssigned_Exception("The specified passenger has already been checked-in", saa);
								}
							}
						}
						// Passenger not found
						InvalidArgument mon = new InvalidArgument();
						mon.setMessage("The selected passenger is not in this flight instance");
						throw new InvalidArgument_Exception("The selected passenger is not in this flight instance", mon);
					}
				}
			}else
			{
				UnknownFlightInstance ufi = new UnknownFlightInstance();
				ufi.setMessage("The specified flight instance is not in the database");
				throw new UnknownFlightInstance_Exception("The specified flight instance is not in the database", ufi);
			}

		} catch (DataManagerException e) {
			e.printStackTrace();
			Monitor mon = new Monitor();
			mon.setMessage("Error during lazy initialization");
			throw new Monitor_Exception("Error during lazy initialization", mon);
		}
	}

}
