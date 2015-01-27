package it.polito.dp2.FDS.lab4.tests;

import static org.junit.Assert.*;
import it.polito.dp2.FDS.FlightInstanceStatus;
import it.polito.dp2.FDS.FlightInstanceReader;
import it.polito.dp2.FDS.FlightMonitor;
import it.polito.dp2.FDS.FlightMonitorFactory;
import it.polito.dp2.FDS.lab4.CancellationImpossibleException;
import it.polito.dp2.FDS.lab4.FDSFinalTest1Client;
import it.polito.dp2.FDS.lab4.NoSuchFlightInstanceException;
import it.polito.dp2.FDS.lab4.OperationFailedException;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FDSTest1 {
	
	private FDSFinalTest1Client clientUnderTest;
	private static FlightMonitor monitor;
	private static FlightInstanceReader instanceToCancel;
	
	private String clientUnderTestClass = "it.polito.dp2.FDS.sol4.client.FDSFinalTest1ClientImpl";
	
    @BeforeClass
    public static void setUpBeforeClass() {
    	try{
	    	FlightMonitorFactory factory = FlightMonitorFactory.newInstance();
			monitor = factory.newFlightMonitor();
			
			List<FlightInstanceReader> fiList= monitor.getFlightInstances(null, null, null);
			
			for (FlightInstanceReader fInstance:fiList) {
	        	
	        	// try with another flight instance if this one cannot be cancelled
	        	FlightInstanceStatus fiStat = fInstance.getStatus();
	        	if(fiStat==FlightInstanceStatus.CANCELLED || fiStat==FlightInstanceStatus.DEPARTED || fiStat==FlightInstanceStatus.ARRIVED)
	        		continue;
				
	        	instanceToCancel = fInstance;
	        }
	        // fail if there is no cancellable flight instance
	        if (instanceToCancel == null)
	        	fail("No flight instance adequate for testing with current seed. Choose a different seed.");
    	}
    	catch(Exception ex)
    	{
    		fail("Cannot instantiate a valid FlightMonitor. Tests failed");
    	}
    }
    
    @Before
    public void setUp() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = FDSTest1.class.getClassLoader();
		}
		try{
			Class<?> t = null;
			t = (loader != null) ? loader.loadClass(clientUnderTestClass) : 
				 Class.forName(clientUnderTestClass);
	        clientUnderTest = (FDSFinalTest1Client) t.newInstance();
	        assertNotNull("The solution client cannot be instantiated", clientUnderTest);
		} 
		catch (ClassNotFoundException ex)
		{
			fail("The class "+clientUnderTestClass+" cannot be found! Check your solution and the name of your class!");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			fail("An exception has occurred when initializing the test. Signal this problem to the teacher");
		}
    }
    
    private void performCancel(FlightInstanceReader fInstance) throws NoSuchFlightInstanceException, CancellationImpossibleException, OperationFailedException {
        // get date and flight number of selected flight instance
        GregorianCalendar date = fInstance.getDate();
        String flightNo = fInstance.getFlight().getNumber();
				
		// call cancel operation
		clientUnderTest.cancel(flightNo,date);
    }
    
    private FlightInstanceStatus performGetStatus(FlightInstanceReader fInstance) throws NoSuchFlightInstanceException, OperationFailedException {
        // get date and flight number of selected flight instance
        GregorianCalendar date = fInstance.getDate();
        String flightNo = fInstance.getFlight().getNumber();
				
		// call cancel operation
		return clientUnderTest.getStatus(flightNo,date);
    }
    
    @Test
    public final void testGetStatus() throws Exception {    	
		// get the list of flight instances
        List<FlightInstanceReader> fiList= monitor.getFlightInstances(null, null, null);
        
        for (FlightInstanceReader fInstance:fiList) {
        	
        	// we skip the instance which has selected for being cancelled
        	if(fInstance.getFlight().getNumber().equals(instanceToCancel.getFlight().getNumber())
        			&& fInstance.getDate().equals(instanceToCancel.getDate()))
        		continue;
        	
	        FlightInstanceStatus fiStat = performGetStatus(fInstance);
			
			// check correctness of result
	        assertEquals("The status was not read correctly.", fInstance.getStatus(), fiStat);
        }
    }

    @Test
    public final void testValidCancel() throws Exception {
    	assertNotNull("No flight instance to cancel. Choose a different seed.", instanceToCancel);
    	
    	performCancel(instanceToCancel);
        FlightInstanceStatus newFiStat = performGetStatus(instanceToCancel);
		
		// check correctness of result
        assertEquals("The status was not set to the correct value or was not read correctly.", FlightInstanceStatus.CANCELLED, newFiStat);
    }
    
    @Test(expected=CancellationImpossibleException.class)
    public final void testCancelWithInvalidFlightInstance() throws Exception {
    	boolean testPerformed=false;
    	
		// get the list of flight instances
        List<FlightInstanceReader> fiList= monitor.getFlightInstances(null, null, null);
        
        for (FlightInstanceReader fInstance:fiList) {
        	// try with this flight instance if test not yet performed
        	if(testPerformed)
        		break;
        	
        	// try with another flight instance if this one can be cancelled
        	FlightInstanceStatus fiStat = fInstance.getStatus();
        	if(fiStat!=FlightInstanceStatus.CANCELLED && fiStat!=FlightInstanceStatus.DEPARTED && fiStat!=FlightInstanceStatus.ARRIVED)
        		continue;
	        
        	performCancel(fInstance);

	        testPerformed = true;
        }
        // fail if test could not be performed
        if (!testPerformed)
        	fail("No flight instance adequate for testing with current seed. Choose a different seed.");
    }
    
    @Test(expected=CancellationImpossibleException.class)
    public final void testCancelWithInvalidFlightInstanceDoesNotChangeStatus() throws Exception {
    	boolean testPerformed=false;
    	
		// get the list of flight instances
        List<FlightInstanceReader> fiList= monitor.getFlightInstances(null, null, null);
        
        for (FlightInstanceReader fInstance:fiList) {
        	// try with this flight instance if test not yet performed
        	if(testPerformed)
        		break;
        	
        	// try with another flight instance if this one can be cancelled
        	FlightInstanceStatus fiStat = fInstance.getStatus();
        	if(fiStat!=FlightInstanceStatus.CANCELLED && fiStat!=FlightInstanceStatus.DEPARTED && fiStat!=FlightInstanceStatus.ARRIVED)
        		continue;
	        
        	try {
        		performCancel(fInstance);
        	} catch (CancellationImpossibleException e) {
				// This exception is expected. Check status has not been changed
        		FlightInstanceStatus newFiStat = performGetStatus(fInstance);
		        assertEquals("The status was erroneously changed or not read correctly.",fiStat, newFiStat);
		        throw (e);
        	} catch (Exception ge) {
        		// other exceptions are not expected
        		fail("Unexpected exception "+ge.getMessage());
        	}

	        testPerformed = true;
        }
        // fail if test could not be performed
        if (!testPerformed)
        	fail("No flight instance adequate for testing with current seed. Choose a different seed.");
    }
    
    @Test(expected=NoSuchFlightInstanceException.class)
    public final void testCancelWithNonExistingFlightInstance() throws Exception {
    	boolean testPerformed=false;
    	
		// look for a flight instance that does not exist
    	GregorianCalendar date = new GregorianCalendar();
    	date.clear();
    	date.set(2014, 01, 01);
    	String flightNo = "AA000";
        FlightInstanceReader fInstance= monitor.getFlightInstance(flightNo, date);
        
        assertNull("Non existing flight number not found. Please try with another seed.",fInstance);
        
        clientUnderTest.cancel(flightNo,date);
        
        testPerformed = true;
        
        // fail if test could not be performed
        if (!testPerformed)
        	fail("No flight instance adequate for testing with current seed. Choose a different seed.");
    }
    
    @Test(expected=OperationFailedException.class)
    public final void testCancelWithNonExistingService() throws Exception {
    	boolean testPerformed=false;
    	
		// get the list of flight instances
        List<FlightInstanceReader> fiList= monitor.getFlightInstances(null, null, null);
        
        for (FlightInstanceReader fInstance:fiList) {
        	// try with this flight instance if test not yet performed
        	if(testPerformed)
        		break;
        	
        	// try with another flight instance if this one cannot be cancelled
        	FlightInstanceStatus fiStat = fInstance.getStatus();
        	if(fiStat==FlightInstanceStatus.CANCELLED || fiStat==FlightInstanceStatus.DEPARTED || fiStat==FlightInstanceStatus.ARRIVED)
        		continue;
			
        	clientUnderTest.setCancellationServiceURL(new URL("http://localhost:9000/noURL"));
	        performCancel(fInstance);   
	        
			testPerformed = true;
        }
        // fail if test could not be performed
        if (!testPerformed)
        	fail("No flight instance adequate for testing with current seed. Choose a different seed.");
    }
	
}
