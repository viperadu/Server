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

public class VideoHandler extends Thread {
	private static final String TAG = "VideoHandler: ";
	private static final boolean DEBUGGING = true;
	private static final boolean LOGGING = true;
	
	private DatagramSocket videoSocket;
	private DatagramPacket videoPacket;
	private byte[] videoData;
	private Uploader mUploader;

	public VideoHandler(DatagramSocket socket, Uploader uploader) {
		this.videoData = new byte[1500];
		this.videoSocket = socket;
		this.videoPacket = new DatagramPacket(videoData, videoData.length);
		this.mUploader = uploader;
	}

	@Override
	public void run() {
		try {
			boolean first = true;
			int count = 0;
			int MBs = 0;
			while(!Thread.interrupted()) {
				videoSocket.receive(videoPacket);

				if(first) {
					System.out.println("Received a first video packet!");
					first = false;
				}
				if(mUploader != null) {
					for(Viewer v : mUploader.mViewers) {
	//					videoPacket.setPort(5006);
						videoPacket.setPort(v.mDestinationPorts[2]);
	//					videoPacket.setAddress(InetAddress.getByName(Global.localIPAddress));
						videoPacket.setAddress(v.mDestination);
						
						videoSocket.send(videoPacket);
					}
				}
				
				if (count > 1048576) {
					MBs++;
					DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
					Date date = new Date();
//					Utils.LOG(
//							TAG + MBs + "Mb of video data received from "
//									+ videoPacket.getAddress() + "  ("
//									+ dateFormat.format(date) + ")", DEBUGGING,
//							LOGGING);
					System.err.println(TAG + MBs + "Mb of video data received from "
							+ videoPacket.getAddress() + "  ("
							+ dateFormat.format(date) + ")");
					count -= 1048576;
				}
				count += videoPacket.getLength();
			}
		} catch (IOException e) {
			Utils.LOG(
					"VideoHandler: Error while receiving / sending video packets",
					DEBUGGING, LOGGING);
		}
	}
}