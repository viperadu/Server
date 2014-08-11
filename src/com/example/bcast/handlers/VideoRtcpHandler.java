package com.example.bcast.handlers;

import java.io.IOException;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.bcast.Global;
import com.example.bcast.Uploader;
import com.example.bcast.Viewer;
import com.example.bcast.utils.Utils;

public class VideoRtcpHandler extends RtcpHandler {
	private static final String TAG = "VideoRtcpHandler: ";
//	private static final boolean DEBUGGING = true;
//	private static final boolean LOGGING = true;
	
//	private DatagramSocket videoRtcpSocket;
//	private DatagramPacket videoRtcpPacket;
//	private byte[] videoRtcpData;
//	private Uploader mUploader;
//	private int uploaderIndex = 0;
	
//	private int bufferLength;
//	private SimpleDateFormat dateFormat;
	

	public VideoRtcpHandler(DatagramSocket socket, Uploader uploader, int bufferLength) {
//		videoRtcpData = new byte[1500];
//		this.videoRtcpSocket = socket;
//		this.videoRtcpPacket = new DatagramPacket(videoRtcpData, videoRtcpData.length);
//		this.mUploader = uploader;
//		this.uploaderIndex = Global.getUploaderIndex(mUploader);
//		this.dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		
		// We set the FIFO queue size equal to the length of the buffer divided by 5, 
		// which is the interval between RTCP packets. So for a 10 second buffer, we would
		// only need a queue of size 2, because we would only get 2 RTCP packets.
		super(socket, uploader, (int) Math.ceil(bufferLength / 5.0), BUFFER_TYPE_VIDEO);
	}

	/*@Override
	public void run() {
		
	}*/
}