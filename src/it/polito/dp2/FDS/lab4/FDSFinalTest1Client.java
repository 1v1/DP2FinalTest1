/**
 * 
 */
package it.polito.dp2.FDS.lab4;

import it.polito.dp2.FDS.FlightInstanceStatus;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Set;

/**
 * This interface allows performing flight instance cancellations and obtain the current status of a flight instance.
 */
public interface FDSFinalTest1Client {
	
	/**
	 * Sets the URL of the web service to be used for the cancellation operations
	 * The default URL is the one specified in the WSDL submitted for Assignment 4.
	 * Note that this is only a local setting operation.
	 * 
	 * @param url The URL of the actual service port to be used for cancellation operations.
	 */
	public void setCancellationServiceURL(URL url);
	
	/**
	 * Performs the cancellation of the flight instance specified by the flightNumber and departureDate arguments.
	 * This operation is completed by contacting the appropriate remote web service.
	 * 
	 * @param flightNumber The flight number of the flight instance to be cancelled
	 * @param departureDate A GregorianCalendar whose year, month and day represent the departure date (in the departure airport time zone) of the flight instance to be cancelled; hours,minutes,seconds are meaningless.
	 * @throws NoSuchFlightInstanceException if the cancellation  operation could not be executed because a flight instance with the given flight number and date does not exist.
	 * @throws CancellationImpossibleException if the cancellation  operation could not be executed because the specified flight instance is either already cancelled or is in the DEPARTED or ARRIVED state.
	 * @throws OperationFailedException if the requested operation was not possible because of any other reason (e.g. because the server could not be contacted).
	 */
	public void cancel(String flightNumber, GregorianCalendar departureDate) throws NoSuchFlightInstanceException, CancellationImpossibleException, OperationFailedException;

	/**
	 * Returns the current status of the flight instance specified by the flightNumber and departureDate arguments.
	 * 
	 * @param flightNumber The flight number of the flight instance whose status has to be returned
	 * @param departureDate A GregorianCalendar whose year, month and day represent the departure date (in the departure airport time zone) of the flight instance whose status has to be returned; hours,minutes,seconds are meaningless.
	 * @return the current status of the flight instance specified by the arguments
	 * @throws NoSuchFlightInstanceException if a flight instance with the given flight number and date does not exist.
	 * @throws OperationFailedException if the current status could not be obtained because of any reason (e.g. because the server could not be contacted).
	 */
	public FlightInstanceStatus getStatus(String flightNumber, GregorianCalendar departureDate) throws NoSuchFlightInstanceException, OperationFailedException;
}
