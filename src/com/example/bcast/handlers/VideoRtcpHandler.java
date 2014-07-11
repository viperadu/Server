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

public class VideoRtcpHandler extends Thread {
	private static final String TAG = "VideoRtcpHandler: ";
	private static final boolean DEBUGGING = true;
	private static final boolean LOGGING = true;
	
	private DatagramSocket videoRtcpSocket;
	private DatagramPacket videoRtcpPacket;
	private byte[] videoRtcpData;
	private Uploader mUploader;

	public VideoRtcpHandler(DatagramSocket socket, Uploader uploader) {
		videoRtcpData = new byte[1500];
		this.videoRtcpSocket = socket;
		this.videoRtcpPacket = new DatagramPacket(videoRtcpData, videoRtcpData.length);
		this.mUploader = uploader;
	}

	@Override
	public void run() {
		try {
			int count = 0;
			int MBs = 0;
			while(!Thread.interrupted()) {
				videoRtcpSocket.receive(videoRtcpPacket);

				if(mUploader != null) {
					for(Viewer v : mUploader.mViewers) {
	//					videoRtcpPacket.setPort(5007);
						videoRtcpPacket.setPort(v.mDestinationPorts[3]);
	//					videoRtcpPacket.setAddress(InetAddress.getByName(Global.localIPAddress));
						videoRtcpPacket.setAddress(v.mDestination);
						
						videoRtcpSocket.send(videoRtcpPacket);
					}
				}
				
				if (count > 1048576) {
					MBs++;
					DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
					Date date = new Date();
//					Utils.LOG(
//							TAG + MBs + "Mb of video RTCP data received from "
//									+ videoRtcpPacket.getAddress() + "  ("
//									+ dateFormat.format(date) + ")", DEBUGGING,
//							LOGGING);
					System.err.println(TAG + MBs + "Mb of video RTCP data received from "
							+ videoRtcpPacket.getAddress() + "  ("
							+ dateFormat.format(date) + ")");
					count -= 1048576;
				}
				count += videoRtcpPacket.getLength();
			}
		} catch (IOException e) {
			Utils.LOG(
					"VideoRtcpHandler: Error while receiving / sending video RTCP packets",
					DEBUGGING, LOGGING);
		}
	}
}