package com.example.bcast.handlers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.example.bcast.Viewer;

public abstract class AbstractBuffer {

	/**
	 * The socket used to send the frames to the destination.
	 */
	protected DatagramSocket mSocket;
	/**
	 * The buffer length, in seconds.
	 */
	protected int mBufferLength;
	/**
	 * Helps with logging time.
	 */
	protected SimpleDateFormat dateFormat;
	
	public AbstractBuffer(DatagramSocket socket, int bufferLength) {
		this.mSocket = socket;
		this.mBufferLength = bufferLength;
		this.dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	}
	
	public abstract void addPacket(DatagramPacket mPacket);

	public abstract void sendPacket(ArrayList<Viewer> mViewers);
	
	public long getTimestamp(DatagramPacket mPacket) {
		return mPacket.getData()[4] << 24 | 
				(mPacket.getData()[5] & 0xFF) << 16 | 
				(mPacket.getData()[6] & 0xFF) << 8 | 
				(mPacket.getData()[7] & 0xFF);
	}
}
