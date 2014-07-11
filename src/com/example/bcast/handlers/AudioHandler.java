package com.example.bcast.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.bcast.Uploader;
import com.example.bcast.Viewer;
import com.example.bcast.utils.Utils;

public class AudioHandler extends Thread {
	private static final String TAG = "AudioHandler: ";
	private static final boolean DEBUGGING = true;
	private static final boolean LOGGING = true;
	
	private DatagramSocket audioSocket;
	private DatagramPacket audioPacket;
	private byte[] audioData;
	private Uploader mUploader;

	public AudioHandler(DatagramSocket socket, Uploader uploader) {
		this.audioData = new byte[1500];
		this.audioSocket = socket;
		this.audioPacket = new DatagramPacket(audioData, audioData.length);
		this.mUploader = uploader;
	}

	@Override
	public void run() {
		try {
			boolean first = true;
			int MBs = 0;
			int count = 0;
			while(!Thread.interrupted()) {
				audioSocket.receive(audioPacket);
				if(first) {
					System.out.println("Received a first audio packet!");
					first = false;
				}
				if(mUploader != null) {
					for(Viewer v : mUploader.mViewers) {
						
	//					audioPacket.setPort(5004);
						audioPacket.setPort(v.mDestinationPorts[0]);
	//					audioPacket.setAddress(InetAddress.getByName(Global.localIPAddress));
						audioPacket.setAddress(v.mDestination);
						
						audioSocket.send(audioPacket);
					}
				}
				
				if (count > 102400) {
					MBs++;
					DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
					Date date = new Date();
//					Utils.LOG(
//							TAG + (MBs*100) + "Kb of audio data received from "
//									+ audioPacket.getAddress() + "  ("
//									+ dateFormat.format(date) + ")", DEBUGGING,
//							LOGGING);
					System.err.println(TAG + (MBs*100) + "Kb of audio data received from "
							+ audioPacket.getAddress() + "  ("
							+ dateFormat.format(date) + ")");
					count -= 102400;
				}
				count += audioPacket.getLength();
			}
		} catch (IOException e) {
			Utils.LOG(
					"AudioHandler: Error while receiving / sending audio packets",
					DEBUGGING, LOGGING);
		}
	}
}