package com.example.bcast;

import java.net.InetAddress;

public class Viewer {
	public int id;
	public InetAddress mDestination;
	public boolean play;
	public int audioBytesSent;
	public int audioMBs;
	public int videoBytesSent;
	public int videoMBs;
	
	public Viewer(InetAddress destination) {
		id = Global.assignNewId();
//System.out.println("Viewer: " + "Assigned new id: " + id);
		mDestination = destination;
		play = true;
		audioBytesSent = 0;
		audioMBs = 0;
		videoBytesSent = 0;
		videoMBs = 0;
		
	}
	/*public Viewer(InetAddress destination, int[] destinationPorts) {
		mDestination = destination;
//		mDestinationPorts = destinationPorts;
		audioBytesSent = 0;
		audioMBs = 0;
		videoBytesSent = 0;
		videoMBs = 0;
	}*/
}
