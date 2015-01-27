package it.polito.dp2.FDS.sol4.server;

import javax.xml.datatype.XMLGregorianCalendar;


public class FlightInstanceKey {
	
	private final String flightID;
	private final XMLGregorianCalendar departureDate;
	
	public FlightInstanceKey (String flightID, XMLGregorianCalendar departureDate)
	{
		this.departureDate = departureDate;
		departureDate.setHour(0);
		departureDate.setMinute(0);
		departureDate.setSecond(0);
		departureDate.setMillisecond(0);
		this.flightID = flightID;
	}
	
	@Override
	public int hashCode()
	{
		final int prime1 = 31;
		final int prime2 = 2659;
		int hashcode = (departureDate.getDay() + departureDate.getMonth() +
				departureDate.getYear()) 
				* prime2;
		int c1 = (int) flightID.charAt(0);
		int c2 = (int) flightID.charAt(1);
		String number = flightID.substring(2);
		int n = Integer.valueOf(number);
		
		hashcode=(hashcode+c1+c2+n)*prime1;
		
		return hashcode;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this==obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
            return false;
		
		FlightInstanceKey other = (FlightInstanceKey) obj;
		if  (!flightID.equals(other.flightID))
			return false;

		other.departureDate.setHour(0);
		other.departureDate.setMinute(0);
		other.departureDate.setSecond(0);
		other.departureDate.setMillisecond(0);
		
		return (other.departureDate.equals(departureDate));
	}

}
