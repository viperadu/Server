package com.example.bcast.handlers;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.example.bcast.Uploader;

public class VideoHandler extends RtpHandler {
//	private static final String TAG = "VideoHandler: ";
//	private static final boolean DEBUGGING = true;
//	private static final boolean LOGGING = true;
//	 
//	private DatagramSocket videoSocket;
//	private DatagramPacket videoPacket;
//	private byte[] videoData;
//	private Uploader mUploader;
//	private int uploaderIndex = 0;
//	
//	private Timer timer;
//	private Timer bufferTimer;
//	private int bufferCount;
//	private volatile boolean stillStreaming;
//	private boolean firstTimeOnly = true;
//	private VideoBuffer mBuffer;
//	private boolean bufferingStarted = false;
//	private int mBufferLength = 10;
//	
//	DateFormat dateFormat;
	
//	private ActionListener 	
//	private ActionListener 

	public VideoHandler(DatagramSocket socket, Uploader uploader, int bufferLength) {
		/*this.videoData = new byte[1500];
		this.videoSocket = socket;
		this.videoPacket = new DatagramPacket(videoData, videoData.length);
		this.mUploader = uploader;
		this.uploaderIndex = Global.getUploaderIndex(mUploader);
		this.mBuffer = new VideoBuffer(this.videoSocket, mUploader.mSession.getVideoTrack().mQuality.framerate, mBufferLength);
		bufferTimer = new Timer(1000, mBufferListener);
System.out.println("Timer will execute every " + 1000 + " milliseconds.");
		dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		bufferCount = 0;*/
		super(socket, uploader, bufferLength, BUFFER_TYPE_VIDEO);
	}
	

/*	@Override
	public void run() {
		try {
			long timestamp = 0;
			int count = 0;
			int MBs = 0;
			while(!Thread.interrupted()) {
				mSocket.receive(mPacket);
//				videoPacket.setPort(Global.videoSendingPort);
//				videoPacket = updateTimestamp(videoPacket);
				
//				System.out.println("\t\t\t[" + dateFormat.format(new Date()) + "] " + videoPacket.getLength() + " bytes received");
				
				
//				decodePacket(videoPacket);
				
				Utils.logToFile(Utils.getCurrentTimestamp() + " Video packet received of size " + mPacket.getLength(), "video_received.txt");
				if(firstTimeOnly) {
					timer = new Timer(5000, mActionListener);
					timer.start();
					firstTimeOnly = false;
					Utils.LOG(TAG + "Received the first video packet! (" + mPacket.getLength() + " bytes)", DEBUGGING, LOGGING);
					timestamp = System.currentTimeMillis();
				}
				streamingCounter = 10;
				stillStreaming = true;
				if(mBufferLength > 0) {
					if(!bufferingStarted) {
						if(System.currentTimeMillis() - timestamp >= mBufferLength * 1000) {
							bufferTimer.start();
							bufferingStarted = true;
						}
					}
					mBuffer.addPacket(mPacket);
				} else {
					if(mUploader != null) {
						// TODO: make this less processing-power-consuming
						// Not too cost effective searching every time for number of viewers
						uploaderIndex = Global.getUploaderIndex(mUploader);
						if(Global.mUploaders.get(uploaderIndex).mViewers.size() > 0) {
							for(Viewer v : Global.mUploaders.get(uploaderIndex).mViewers) {
								if(v.play) {
									mPacket.setPort(Global.videoSendingPort);
									mPacket.setAddress(v.mDestination);
									mSocket.send(mPacket);
									v.videoBytesSent += mPacket.getLength();
									if(v.videoBytesSent > 1048576) {
										Utils.LOG(TAG + Global.mUploaders.get(uploaderIndex).mViewers.size() + " viewers connected to " + mUploader.mName + " stream.", DEBUGGING, LOGGING);
										v.videoMBs++;
										Utils.LOG(TAG + "Sent " + v.videoMBs + "Mb to " + v.mDestination + ":" + Global.videoSendingPort, DEBUGGING, LOGGING);
										v.videoBytesSent -= 1048576;
									}
								}
							}
						}
					}
				}
				if (count > 1048576) {
					MBs++;
					System.err.println(TAG + MBs + "Mb of video data received from "
							+ mPacket.getAddress() + "  ("
							+ dateFormat.format(new Date()) + ")");
					count -= 1048576;
				}
				count += mPacket.getLength();
			}
		} catch (IOException e) {
			Utils.LOG(
					"VideoHandler: Error while receiving / sending video packets",
					DEBUGGING, LOGGING);
		}
	}*/
	
	private DatagramPacket updateTimestamp(DatagramPacket packet) {
		int timestamp = (int) System.currentTimeMillis();
		byte[] data = packet.getData();
		for ( int i = 0; i < 4; i++ ) {
		    data[7-i] = new Integer( timestamp >> (8*i) ).byteValue();
		}
		packet.setData(data);
		return packet;
	}
	
	private void decodePacket(DatagramPacket videoPacket) {
		byte[] packet = videoPacket.getData();
//		if( packet[0] == 0b01011100 && packet[1] == 0b10000001 ) {
//			System.out.println("");
//		} else {
		String line = "";
		for(int i=0; i<13; i++) {
			line += "[" + i + "]=" + String.format("%8s",Integer.toBinaryString((packet[i]+256)%256)).replace(" ", "0") + " | ";
		}
		line = line.substring(0, line.length()-3);
		line += "\n";
		for(int i=13; i<15; i++) {
			line += "[" + i + "]=" + String.format("%8s",Integer.toBinaryString((packet[i]+256)%256)).replace(" ", "0") + " | ";
		}
		line = line.substring(0, line.length()-3);
		int sequenceNumber = (packet[2] & 0xFF) << 8 | (packet[3] & 0xFF);
		line += " Sequence number = " + sequenceNumber;
//		long timestamp = packet[7] & 0xFF | (packet[6] & 0xFF) << 8 | (packet[5] & 0xFF) << 16 | (packet[4] & 0xFF) << 24;
//		line += " Timestamp = " + new Date(new Timestamp(timestamp).getTime());
		if(packet[13] == 0x7C) {
			line += " This is an iFrame!";
		}
		System.out.println(line);
	}

	/*public void kill() {
		if(!Thread.interrupted()) {
			stillStreaming = false;
			this.interrupt();
		}
	}*/
}