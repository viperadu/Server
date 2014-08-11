package com.example.bcast;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import javax.swing.Timer;

import com.example.bcast.handlers.AudioHandler;
import com.example.bcast.handlers.AudioRtcpHandler;
import com.example.bcast.handlers.RtcpHandler;
import com.example.bcast.handlers.RtpHandler;
import com.example.bcast.handlers.VideoHandler;
import com.example.bcast.handlers.VideoRtcpHandler;
import com.example.bcast.session.Session;
import com.example.bcast.utils.Utils;

/**
 * @author Radu This class retrieves the video streams from uploaders.
 */
public class Server extends Thread {
	public static final int RTP_PORT = 58086;
	public static final String TAG = "Server: ";
	public static final boolean DEBUGGING = true;
	public static final boolean LOGGING = true;

	private DatagramSocket videoSocket = null;
	private DatagramSocket videoRtcpSocket = null;

	private DatagramSocket audioSocket = null;
	private DatagramSocket audioRtcpSocket = null;

	private Session mSession = null;
	
	private Uploader mUploader;
	private InetAddress mOrigin;
//	private int uploaderIndex = 0;
	
//	private AudioHandler aHandler = null;
//	private AudioRtcpHandler aRtcpHandler = null;
//	private VideoHandler vHandler = null;
//	private VideoRtcpHandler vRtcpHandler = null;
	private RtpHandler aHandler = null, vHandler = null;
	private RtcpHandler aRtcpHandler = null, vRtcpHandler = null;
	
	private volatile boolean streamRunning;
	private int mBufferLength;
	
	public Server(Session session, InetAddress origin, int bufferLength) {
		this.mSession = session;
		this.mOrigin = origin;
		this.mBufferLength = bufferLength;
		if(session.getAudioTrack() != null) {
			boolean error = false;
			do {
				error = false;
				try {
					int port;
					do {
						port = new Random().nextInt(65535);
					} while(port % 2 != 0 && port > 1024);
					audioSocket = new DatagramSocket(port, Global.localAddress);
					audioRtcpSocket = new DatagramSocket(audioSocket.getLocalPort() + 1, Global.localAddress);
					Utils.LOG(TAG + "Audio Port = " + audioSocket.getLocalAddress() + ":" + port + 
							"     Audio RTCP Port = " + audioRtcpSocket.getLocalAddress() + ":" + 
							(audioSocket.getLocalPort() + 1), DEBUGGING, LOGGING);
				} catch (SocketException e) {
					Utils.LOG(TAG + "Error while trying to open Audio socket or Audio RTCP socket", DEBUGGING, LOGGING);
					error = true;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			} while(error);
		}
		
		if(session.getVideoTrack() != null) {
			boolean error = false;
			do {
				error = false;
				try {
					int port;
					do {
						port = new Random().nextInt(65535);
					} while(port % 2 != 0 && port > 1024);
					videoSocket = new DatagramSocket(port, Global.localAddress);
					videoRtcpSocket = new DatagramSocket(videoSocket.getLocalPort() + 1, Global.localAddress);
					Utils.LOG(TAG + "Video Port = " + videoSocket.getLocalAddress() + ":" + port + 
							"     Video RTCP Port = " + videoRtcpSocket.getLocalAddress() + ":" + 
							(videoSocket.getLocalPort() + 1), DEBUGGING, LOGGING);
				} catch (SocketException e) {
					Utils.LOG(TAG + "Error while trying to open Video socket or Video RTCP socket", DEBUGGING, LOGGING);
					error = true;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			} while(error);
		}
		
		this.start();
		// TODO: delete this
		System.out.println("No of uploaders: " + Global.mUploaders.size());
		
		mUploader = Global.getUploaderBySession(mSession);
	}
	
	public int[] getPorts() {
		int[] ports = new int[4];
		for(int i=0; i<4; i++) {
			ports[i] = 0;
		}
		if(mSession.getAudioTrack() != null) {
			ports[0] = audioSocket.getLocalPort();
			ports[1] = audioRtcpSocket.getLocalPort();
		}
		if(mSession.getVideoTrack() != null) {
			ports[2] = videoSocket.getLocalPort();
			ports[3] = videoRtcpSocket.getLocalPort();
		}
		return ports;
	}

	@Override
	public void run() {
		String incoming = "";
		try {
			incoming = "Incoming connection from " + mUploader.mOrigin + " with the name \"" + mUploader.mName + "\" and it contains:";
		} catch (NullPointerException e) {
			Global.mUploaders.remove(mUploader);
			this.interrupt();
		}
		if(mSession.getAudioTrack() != null) {	
			aHandler = new AudioHandler(audioSocket, mUploader, mBufferLength);
			aRtcpHandler = new AudioRtcpHandler(audioRtcpSocket, mUploader, mBufferLength);
			aHandler.start();
			aRtcpHandler.start();
			incoming += "\nAudio track: BitRate = " + mSession.getAudioTrack().getAudioQuality().bitRate + ", Sampling Rate = " + mSession.getAudioTrack().getAudioQuality().samplingRate;
		}
		if(mSession.getVideoTrack() != null) {
			vHandler = new VideoHandler(videoSocket, mUploader, mBufferLength);
			vRtcpHandler = new VideoRtcpHandler(audioRtcpSocket, mUploader, mBufferLength);
			vHandler.start();
			vRtcpHandler.start();
			incoming += "\nVideo track: Resolution: " + mSession.getVideoTrack().getVideoQuality().resX + "x" + mSession.getVideoTrack().getVideoQuality().resY + ", " + mSession.getVideoTrack().getVideoQuality().framerate + " fps and " + mSession.getVideoTrack().getVideoQuality().bitrate + " bitrate";
		}
		Utils.LOG(TAG + incoming, DEBUGGING, LOGGING);
		
		/*Timer t = new Timer(2000, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				boolean audioStillStreaming = false, videoStillStreaming = false;
				if(aHandler != null) {
					audioStillStreaming = aHandler.isStillStreaming();
System.out.println("Audio still streaming? " + aHandler.isStillStreaming());
				}
				if(vHandler != null) {
					videoStillStreaming = vHandler.isStillStreaming();
System.out.println("Video still streaming? " + vHandler.isStillStreaming());
				}
				if((audioStillStreaming && videoStillStreaming) == false) {
					Utils.LOG(TAG + "Streaming from " + mUploader.mOrigin + "/" + mUploader.mName + " stopped.", DEBUGGING, LOGGING);
					Global.mUploaders.remove(mUploader);
					kill();
				}
			}
			
		});*/
		
	}
	
	public void kill() {
		if(!Thread.interrupted()) {
			this.interrupt();
			// TODO: is this any good?
			Global.conn.executeDelete("DELETE FROM sessions WHERE sessionId=" + mUploader.sessionId);
		}
	}
}
