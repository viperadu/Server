package com.example.bcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.example.bcast.utils.Utils;

public class RtcpServer extends Thread {
	public static String TAG = "RtcpServer: ";
	private boolean DEBUGGING = true;
	private boolean LOGGING = true;
	
	private DatagramSocket mSocket;
	private DatagramPacket mPacket;
	private int MTU = 1500;
	private byte[] mPayload;
	
	public RtcpServer(int port) throws SocketException {
		mSocket = new DatagramSocket(port);
		mPayload = new byte[MTU];
		mPacket = new DatagramPacket(mPayload, MTU);
		this.start();
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try {
				mSocket.receive(mPacket);
				// TODO: change with the port the client specifies
				mPacket.setPort(5007);
				// TODO: change withthe address the client has
				mPacket.setAddress(InetAddress.getByName("192.168.0.3"));
				mSocket.send(mPacket);
//				Utils.LOG(TAG + "Received RTCP packet of " + mPacket.getLength() + " bytes.", DEBUGGING, LOGGING);
//				for(Processer proc : PacketProcesser.getProcessers()) {
//					for(InetAddress addr : proc.getDestinations())
//					{
//						mPacket.setAddress(addr);
//						mPacket.setPort(5007);
//						mSocket.send(mPacket);
//					}
//				}
//				Thread.sleep(1000);
			} catch (IOException e) {
				Utils.LOG(TAG + "Error while receiving RTCP packets", DEBUGGING, LOGGING);
				e.printStackTrace();
//			} catch (InterruptedException e) {
//				Utils.LOG(TAG + "Interrupted exception", DEBUGGING, LOGGING);
			}
		}
	}
}
