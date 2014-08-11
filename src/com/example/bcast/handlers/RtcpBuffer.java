package com.example.bcast.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.bcast.Global;
import com.example.bcast.Viewer;

public class RtcpBuffer extends AbstractBuffer {

	/**
	 * The queue which holds the RTCP messages.
	 */
	protected Queue<DatagramPacket> mRtcpMessagesQueue;
	/**
	 * The port to which the RTCP packets will be sent.
	 */
	protected int port;
	
	public RtcpBuffer(DatagramSocket socket, int bufferLength, int bufferType) {
		super(socket, bufferLength);
		mRtcpMessagesQueue = new ConcurrentLinkedQueue<DatagramPacket>();
		switch(bufferType) {
		case 1:
			port = Global.audioRtcpSendingPort;
			break;
		case 2:
			port = Global.videoRtcpSendingPort;
			break;
		}
	}
	

	@Override
	public void addPacket(DatagramPacket mPacket) {
		if(mRtcpMessagesQueue.size() < mBufferLength) {
			mRtcpMessagesQueue.offer(new DatagramPacket(mPacket.getData(), mPacket.getLength()));
		} else {
			mRtcpMessagesQueue.poll();
			mRtcpMessagesQueue.offer(new DatagramPacket(mPacket.getData(), mPacket.getLength()));
		}
	}

	@Override
	public void sendPacket(ArrayList<Viewer> mViewers) {
		if(mRtcpMessagesQueue.size() > 0 && mViewers.size() > 0) {
			DatagramPacket p = mRtcpMessagesQueue.poll(); 
			for(Viewer v : mViewers) {
				p.setPort(port);
				p.setAddress(v.mDestination);
				try {
					mSocket.send(p);
				} catch (IOException e) {
					System.err.println("Something went wrong when trying to send RTCP messages to " + v.mDestination + ".");
				}
			}
		}
	}

}
