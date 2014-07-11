package com.example.bcast.processer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.bcast.utils.Utils;

public class Processer implements Runnable {
	public static final String TAG = "Processer: ";
	public static final boolean DEBUGGING = true;
	public static final boolean LOGGING = true;

	private MulticastSocket socket = null;
	private String recvFrom = null;
	private Queue<DatagramPacket> queue = new ConcurrentLinkedQueue<DatagramPacket>();
	private static ArrayList<InetAddress> mDestinations = null;

	public Processer(DatagramPacket recvPacket, MulticastSocket socket) {
		mDestinations = new ArrayList<InetAddress>();
		recvFrom = recvPacket.getAddress().getHostAddress();
		this.socket = socket;
	}

	public boolean contains(InetAddress addr) {
		return mDestinations.contains(addr);
	}

	public void addDestination(InetAddress a) {
		mDestinations.add(a);
	}

	public void removeDestination(InetAddress a) {
		if (mDestinations.contains(a)) {
			mDestinations.remove(a);
		}
	}

	public String getSourceAddress() {
		return recvFrom;
	}

	public void addPacket(DatagramPacket recvPacket) {
		// socket.send(recvPacket);
		if (queue.size() < 3) {
			queue.add(recvPacket);
		} else {
			queue.poll();
			queue.add(recvPacket);
		}
	}

	public void run() {
		int count = 0, noOfMb = 0;;
		DatagramPacket dtgrm;
		while(!Thread.interrupted()) {
			//TODO: RTSP Server, offering packets from queue to users
			if(queue.size() > 0) {
				try {
					synchronized(this) {
						if(mDestinations.size() > 0) {
//							for(InetAddress addr : mDestinations) {
								dtgrm = queue.poll();
								dtgrm.setAddress(mDestinations.get(0));
								// TODO: parse this from the request
								dtgrm.setPort(5006);
								socket.send(dtgrm);
								count += dtgrm.getLength();
								if(count > 1048576) {
									count -= 1048576;
									noOfMb++;
									System.out.println("Sent " + noOfMb + "Mb.");
								}
//							}
						}
					}
				} catch (IOException e) {
					Utils.LOG(TAG + "Error while sending packets from processer ", DEBUGGING, LOGGING);
					e.printStackTrace();
				}
			}
			//TODO: HTTP Server, offering packets from queue to users
		}
	}

	public Queue<DatagramPacket> getQueue() {
		return queue;
	}

	public ArrayList<InetAddress> getDestinations() {
		return mDestinations;
	}
}
