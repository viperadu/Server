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

public class AudioRtcpHandler extends Thread {
	private static final String TAG = "AudioRtcpHandler: ";
	private static final boolean DEBUGGING = true;
	private static final boolean LOGGING = true;
	
	private DatagramSocket audioRtcpSocket = null;
	private DatagramPacket audioRtcpPacket = null;
	private byte[] audioRtcpData;
	private Uploader mUploader;

	public AudioRtcpHandler(DatagramSocket socket, Uploader uploader) {
		this.audioRtcpData = new byte[1500];
		this.audioRtcpSocket = socket;
		this.audioRtcpPacket = new DatagramPacket(audioRtcpData, audioRtcpData.length);
		this.mUploader = uploader;
	}

	@Override
	public void run() {
		try {
			int MBs = 0;
			int count = 0;
			while(!Thread.interrupted()) {
				audioRtcpSocket.receive(audioRtcpPacket);

				if(mUploader != null) {
					for(Viewer v : mUploader.mViewers) {
		//					audioRtcpPacket.setPort(5004);
						audioRtcpPacket.setPort(v.mDestinationPorts[1]);
		//					audioRtcpPacket.setAddress(InetAddress.getByName(Global.localIPAddress));
						audioRtcpPacket.setAddress(v.mDestination);
						
						audioRtcpSocket.send(audioRtcpPacket);
					}
				}
				if (count > 102400) {
					MBs++;
					DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
					Date date = new Date();
//					Utils.LOG(
//							TAG + (MBs*100) + "Kb of audio data received from "
//									+ audioRtcpPacket.getAddress() + "  ("
//									+ dateFormat.format(date) + ")", DEBUGGING,
//							LOGGING);
					System.err.println(TAG + (MBs*100) + "Kb of audio data received from "
							+ audioRtcpPacket.getAddress() + "  ("
							+ dateFormat.format(date) + ")");
					count -= 102400;
				}
				count += audioRtcpPacket.getLength();
			}
		} catch (IOException e) {
			Utils.LOG(
					"AudioRtcpHandler: Error while receiving / sending audio RTCP packets",
					DEBUGGING, LOGGING);
		}
	}
}