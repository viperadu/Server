package com.example.bcast;

import java.net.InetAddress;

public class Viewer {
	public InetAddress mDestination;
	public int[] mDestinationPorts;
	
	public Viewer(InetAddress destination) {
		mDestination = destination;
		mDestinationPorts = new int[]{0, 0, 0, 0};
	}
	public Viewer(InetAddress destination, int[] destinationPorts) {
		mDestination = destination;
		mDestinationPorts = destinationPorts;
	}
}
