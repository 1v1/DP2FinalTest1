package it.polito.dp2.FDS.sol4.server;

import java.util.concurrent.Executors;

import javax.xml.ws.Endpoint;


public class FDSControlServer {

	private static final Integer NUMBER_OF_THREADS = 10;

	public static void main(String[] args)
	{
		System.out.println("Publishing http://localhost:8182/fdscontrol");
		Endpoint endpoint = Endpoint.create(new FDSControlImpl());
		endpoint.setExecutor(Executors.newFixedThreadPool(NUMBER_OF_THREADS));
		endpoint.publish("http://localhost:8182/fdscontrol");

		System.out.println("Publishing http://localhost:8181/fdsinfo");
		Endpoint endpoint2 = Endpoint.create(new FDSInfoImpl());
		endpoint2.setExecutor(Executors.newFixedThreadPool(NUMBER_OF_THREADS));
		endpoint2.publish("http://localhost:8181/fdsinfo");
	}

}
