package com.example.bcast.processer;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Queue;

import com.example.bcast.Global;

public class PacketProcesser {

	private static ArrayList<Processer> mProcessers = null;
//	private ArrayList<Queue<DatagramPacket>> mQueues = null;
	private MulticastSocket mSocket = null;
	
	public PacketProcesser(/*ArrayList<Queue<DatagramPacket>> queues, */MulticastSocket socket) {
		mProcessers = new ArrayList<Processer>();
//		mQueues = queues;
		mSocket = socket;
	}
	
	public static ArrayList<Processer> getProcessers() {
		return mProcessers;
	}
	
	public void addPacket(DatagramPacket packet) {
		if(mProcessers.size() > 0) {
			for(Processer p : mProcessers) {
				if(p.getSourceAddress().equals(packet.getAddress().getHostAddress())){
					p.addPacket(packet);
					return;
				}
			}
		} else {
			Processer p = new Processer(packet, mSocket);
			mProcessers.add(p);
			new Thread(p).start();
			Global.mQueues.add(p.getQueue());
		}
	}
	
	public ArrayList<Queue<DatagramPacket>> getQueues() {
		return Global.mQueues;
	}
	
	public Queue<DatagramPacket> getQueue(int n) {
		return Global.mQueues.get(n);
	}
}
