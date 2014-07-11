package com.example.bcast;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

import com.example.bcast.handlers.AudioHandler;
import com.example.bcast.handlers.AudioRtcpHandler;
import com.example.bcast.handlers.VideoHandler;
import com.example.bcast.handlers.VideoRtcpHandler;
import com.example.bcast.session.Session;
import com.example.bcast.utils.Utils;

/**
 * @author Radu This class retrieves the video streams from uploaders.
 */
public class Server extends Thread {

	public static final String TAG = "Server: ";
	public static final boolean DEBUGGING = true;
	public static final boolean LOGGING = true;

	private DatagramSocket videoSocket = null;
	private DatagramSocket videoRtcpSocket = null;

	private DatagramSocket audioSocket = null;
	private DatagramSocket audioRtcpSocket = null;

	private Session mSession = null;
	
	private Uploader mUploader;
	
	
	public Server(Session session) {
		this.mSession = session;
		
		if(session.getAudioTrack() != null) {
			boolean error = false;
			do {
				try {
					int port;
					do {
						port = new Random().nextInt(65535);
					} while(port % 2 != 0);
					audioSocket = new DatagramSocket(port);
					audioRtcpSocket = new DatagramSocket(audioSocket.getLocalPort() + 1);
					Utils.LOG(TAG + "Audio Port = " + port + "     Audio RTCP Port = " + (audioSocket.getLocalPort() + 1), DEBUGGING, LOGGING);
				} catch (SocketException e) {
					Utils.LOG(TAG + "Error while trying to open Audio socket on port " + audioSocket.getLocalPort() + " or Audio RTCP socket on port " + audioSocket.getLocalPort() + 1, DEBUGGING, LOGGING);
					error = true;
				}
			} while(error);
		}
		
		if(session.getVideoTrack() != null) {
			boolean error = false;
			do {
				try {
					int port;
					do {
						port = new Random().nextInt(65535);
					} while(port % 2 != 0);
					videoSocket = new DatagramSocket(port);
					videoRtcpSocket = new DatagramSocket(videoSocket.getLocalPort() + 1);
					Utils.LOG(TAG + "Video Port = " + port + "     Video RTCP Port = " + (videoSocket.getLocalPort() + 1), DEBUGGING, LOGGING);
				} catch (SocketException e) {
					Utils.LOG(TAG + "Error while trying to open Video socket on port " + videoSocket.getLocalPort() + " or Video RTCP socket on port " + videoSocket.getLocalPort() + 1, DEBUGGING, LOGGING);
					error = true;
				}
			} while(error);
		}
		
		this.start();
		// TODO: delete this
		int i=0;
		System.err.println("No of uploaders: " + Global.mUploaders.size());
		
		for(Uploader u : Global.mUploaders) {
			// TODO: delete this
			System.err.println("Uploader " + i + ": " + u.mName);
			i++;
			if(mSession == u.mSession) {
				mUploader = u;
				// TODO: delete this
				System.err.println("Uploader match found!");
				break;
			}
		}
	}
	
	public int[] getPorts() {
//		if(mSession.getAudioTrack() == null && mSession.getVideoTrack() == null) {
//			return new int[] {0, 0, 0, 0};
//		} else if(mSession.getAudioTrack() != null && mSession.getVideoTrack() == null) {
//			return new int[] {audioSocket.getLocalPort(), audioRtcpSocket.getLocalPort(), 0, 0};
//		} else if(mSession.getAudioTrack() == null && mSession.getVideoTrack() != null) {
//			return new int[] {0, 0, videoSocket.getLocalPort(), videoRtcpSocket.getLocalPort()};
//		} else {
//			return new int[] {videoSocket.getLocalPort(), videoRtcpSocket.getLocalPort(),
//					audioSocket.getLocalPort(), audioRtcpSocket.getLocalPort()};
//		}
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

	/*public Server(int videoPort, int audioPort, MulticastSocket multicastSocket) {
		mVideoPort = videoPort;
		mVideoRtcpPort = videoPort + 1;
		mAudioPort = audioPort;
		mAudioRtcpPort = audioPort + 1;
		try {
			videoSocket = new DatagramSocket(videoPort, InetAddress.getByName(Global.localIPAddress));
			videoRtcpSocket = new DatagramSocket(mVideoRtcpPort, InetAddress.getByName(Global.localIPAddress));
			
			audioSocket = new DatagramSocket(audioPort, InetAddress.getByName(Global.localIPAddress));
			audioRtcpSocket = new DatagramSocket(mAudioRtcpPort, InetAddress.getByName(Global.localIPAddress));
			
			Global.videoSendingPort = videoSocket.getLocalPort();
			Global.audioSendingPort = audioSocket.getLocalPort();
			
		} catch (SocketException e) {
			Utils.LOG(TAG + "Port is busy", DEBUGGING, LOGGING);
			return;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.start();
	}*/

	@Override
	public void run() {
		if(mSession.getAudioTrack() != null) {	
			AudioHandler aHandler = new AudioHandler(audioSocket, mUploader);
			AudioRtcpHandler aRtcpHandler = new AudioRtcpHandler(audioRtcpSocket, mUploader);
			aHandler.start();
			aRtcpHandler.start();
		}	
		if(mSession.getVideoTrack() != null) {
			VideoHandler vHandler = new VideoHandler(videoSocket, mUploader);
			VideoRtcpHandler vRtcpHandler = new VideoRtcpHandler(videoRtcpSocket, mUploader);
			vHandler.start();
			vRtcpHandler.start();
		}
			// synchronized(this) {
			// processer.addPacket(packet);
			// }

	}
}
