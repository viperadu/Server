package com.example.bcast.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.bcast.Global;
import com.example.bcast.Uploader;
import com.example.bcast.Viewer;
import com.example.bcast.utils.Utils;

public class AudioRtcpHandler extends RtcpHandler {
	private static final String TAG = "AudioRtcpHandler: ";
//	private static final boolean DEBUGGING = true;
//	private static final boolean LOGGING = true;
	
//	private DatagramSocket audioRtcpSocket = null;
//	private DatagramPacket audioRtcpPacket = null;
//	private byte[] audioRtcpData;
//	private Uploader mUploader;
//	private int uploaderIndex = 0;
	

	public AudioRtcpHandler(DatagramSocket socket, Uploader uploader, int bufferLength) {
//		this.audioRtcpData = new byte[1500];
//		this.audioRtcpSocket = socket;
//		this.audioRtcpPacket = new DatagramPacket(audioRtcpData, audioRtcpData.length);
//		this.mUploader = uploader;
//		this.uploaderIndex = Global.getUploaderIndex(mUploader);
		super(socket, uploader, (int) Math.ceil(bufferLength / 5.0), BUFFER_TYPE_AUDIO);
	}

	/*@Override
	public void run() {
		try {
			int MBs = 0;
			int count = 0;
			while(!Thread.interrupted()) {
				mSocket.receive(mPacket);
				Utils.logToFile(Utils.getCurrentTimestamp() + " Audio control packet received of size " + mPacket.getLength(), "audio_rtcp_received.txt");
				if(mUploader != null) {
					uploaderIndex = Global.getUploaderIndex(mUploader);
					for(Viewer v : Global.mUploaders.get(uploaderIndex).mViewers) {
						if(v.play) {
							mPacket.setPort(Global.audioRtcpSendingPort);
							mPacket.setAddress(v.mDestination);
							mSocket.send(mPacket);
						}
					}
				}
				if (count > 10240) {
					MBs++;
					DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
					Date date = new Date();
					System.err.println(TAG + (MBs*10) + "Kb of audio data received from "
							+ mPacket.getAddress() + "  ("
							+ dateFormat.format(date) + ")");
					count -= 10240;
				}
				count += mPacket.getLength();
			}
		} catch (IOException e) {
			Utils.LOG(
					"AudioRtcpHandler: Error while receiving / sending audio RTCP packets",
					DEBUGGING, LOGGING);
		}
	}*/
}