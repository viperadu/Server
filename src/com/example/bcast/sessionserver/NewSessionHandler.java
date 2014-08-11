package com.example.bcast.sessionserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.example.bcast.Global;
import com.example.bcast.Server;
import com.example.bcast.Uploader;
import com.example.bcast.audio.AACStream;
import com.example.bcast.audio.AudioQuality;
import com.example.bcast.session.Session;
import com.example.bcast.utils.Pair;
import com.example.bcast.utils.Utils;
import com.example.bcast.video.H264;
import com.example.bcast.video.MP4Config;
import com.example.bcast.video.VideoQuality;

public class NewSessionHandler extends Thread {
	public static final String TAG = "NewSessionHandler: ";
	public static final boolean DEBUGGING = true;
	public static final boolean LOGGING = true;

	Socket mClient = null;
	InetAddress mAddr = null;
	ObjectInputStream ois = null;
	ObjectOutputStream oos = null;
	Session mSession = null;
	MP4Config mConfig = null;
	int audioConfig = 0;
	VideoQuality mVideoQuality = null;
	AudioQuality mAudioQuality = null;
	int SSRC = 0;

	public NewSessionHandler(Socket newClient) throws ClassNotFoundException,
			IOException {
		mClient = newClient;
		mAddr = mClient.getInetAddress();
		int[] ports = null;
		String title = "";
		int bufferLength = 0;
		Server server = null;
		
		try {
			ois = new ObjectInputStream(mClient.getInputStream());
			oos = new ObjectOutputStream(mClient.getOutputStream());
		} catch (IOException e) {
			Utils.LOG(TAG + e.getMessage() != null ? e.getMessage() : "Unknown error", DEBUGGING, LOGGING);
		}
		try {
			title = (String) ois.readObject();
			Utils.LOG(TAG + "Successfully received String object from "
					+ mAddr, DEBUGGING, LOGGING);
		} catch (ClassNotFoundException e) {
			System.err.println("String - Class not found");
			title = "";
		}
		// Verifying the uniqueness of the title
		if(title != "" && Global.checkIfTitleExists(title)) {
			ports = new int[]{-1, -1, -1, -1};
			Utils.LOG(TAG + "Stream title is already in use!", DEBUGGING, LOGGING);
		}
		
		try {
			bufferLength = (Integer) ois.readObject();
			Utils.LOG(TAG + "Successfully received Integer object from "
					+ mAddr, DEBUGGING, LOGGING);
		} catch (ClassNotFoundException e) {
			System.err.println("Integer - Class not found");
			bufferLength = 0;
		}
		try {
			mSession = (Session) ois.readObject();
			if(mSession.getAudioTrack() != null) {
				System.err.println("AudioTrack = " + mSession.getAudioTrack().getClass().getName());
			} else {
				System.err.println("AudioTrack is null");
			}
			if(mSession.getVideoTrack() != null) {
				System.err.println("VideoTrack = " + mSession.getVideoTrack().getClass().getName());
			} else {
				System.err.println("VideoTrack is null");
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Session - Class not found");
			mSession = null;
		}
		
		try {
			SSRC = (Integer) ois.readObject();
		} catch (ClassNotFoundException e) {
			System.err.println("Integer - Class not found");
			SSRC = 0;
		}
		if (SSRC != 0) {
			Utils.LOG(TAG + "Successfully received Integer object from "
					+ mAddr, DEBUGGING, LOGGING);
		}
		
		if(mSession.getAudioTrack() != null) {
			try {
				mAudioQuality = (AudioQuality) ois.readObject();
				mSession.getAudioTrack().setAudioQuality(mAudioQuality);
				Utils.LOG(TAG + "Successfully received AudioQuality object from "
						+ mAddr, DEBUGGING, LOGGING);
System.out.println("AudioQuality: " + mAudioQuality.bitRate + " - " + mAudioQuality.samplingRate);
				if(mSession.getAudioTrack() instanceof AACStream) {
					audioConfig = (Integer) ois.readObject();
					((AACStream)mSession.getAudioTrack()).setAudioConfig(audioConfig);
				}
			} catch (ClassNotFoundException e) {
				System.err.println("AudioQuality - Class not found");
				mAudioQuality = null;
			}
		}
		if(mSession.getVideoTrack() != null) {
			try {
				mVideoQuality = (VideoQuality) ois.readObject();
				Utils.LOG(TAG + "Successfully received VideoQuality object from "
						+ mAddr, DEBUGGING, LOGGING);
System.out.println("VideoQuality: " + mVideoQuality.resX + " x " + mVideoQuality.resY + " - " + mVideoQuality.framerate + " - " + mVideoQuality.bitrate);
			} catch (ClassNotFoundException e) {
				System.err.println("VideoQuality - Class not found");
				mVideoQuality = null;
			}
			if(mSession.getVideoTrack() instanceof H264) {
				try {
//					mConfig = (MP4Config) ois.readObject();
					String profileLevel = (String) ois.readObject();
					String pps = (String) ois.readObject();
					String sps = (String) ois.readObject();
					mConfig = new MP4Config(profileLevel, pps, sps);
				} catch (ClassNotFoundException e) {
					System.err.println("MP4Config - Class not found");
					mConfig = null;
				}
			}
		}

		if (mSession != null) {
			Utils.LOG(TAG + "Successfully received Session object from "
					+ mAddr, DEBUGGING, LOGGING);
			//We replace the session's origin with the server address
			mSession.setOrigin(mClient.getLocalAddress());
			if(mSession.getVideoTrack() instanceof H264) {
				if(mConfig != null) {
//					mSession.addVideoStream(new H264(mConfig));
					mSession.setMP4Config(mConfig);
					mSession.getVideoTrack().setVideoQuality(mVideoQuality);
					Utils.LOG(TAG + "Successfully received MP4Config object from "
							+ mAddr, DEBUGGING, LOGGING);
				}
			}
			if(ports == null) {
				// We start the server that receives Audio / Video packets
				Uploader u = new Uploader(title, mSession, null, mAddr, SSRC, mConfig);
//				Global.mUploaders.add(u);
				Global.addUploader(u);
				server = new Server(mSession, mAddr, bufferLength);
				u.mServer = server;
				Global.mUploaders.set(Global.getUploaderIndex(u), u);
				ports = server.getPorts();
				if(ports != null && (ports[0] != -1 || ports[2] != -1)) {
					String insertQuery = "INSERT INTO sessions(name,ports,origin,ssrc,bufferLength,";
					if(mSession.getAudioTrack() != null) {
						insertQuery += "audioQuality,audioEncoder,";
					}
					if(mSession.getVideoTrack() != null) {
						insertQuery += "videoQuality,videoEncoder,";
					}
					if(mSession.getVideoTrack() instanceof H264) {
						insertQuery += "mp4config";
					}
					// TODO: check if user is logged in / registered
					// if(user registered) {
					// 		insertQuery += ",userId";
					// }
					insertQuery += ") VALUES(";
					insertQuery += "\"" + title + "\",\"" + ports[0] + "," + ports[1] + "," + ports[2] + "," + ports[3] + "\",\""
							+ mAddr.toString().substring(1) + "\",\"" + SSRC + "\"," + bufferLength;
					if(mSession.getAudioTrack() != null) {
						insertQuery += ",\"" + mAudioQuality.toString() + "\",\"" + mSession.getAudioTrack().getClass().getName().substring(mSession.getAudioTrack().getClass().getName().lastIndexOf(".") + 1) + "\"";
					}
					if(mSession.getVideoTrack() != null) {
						insertQuery += ",\"" + mVideoQuality.toString() + "\",\"" + mSession.getVideoTrack().getClass().getName().substring(mSession.getVideoTrack().getClass().getName().lastIndexOf(".") + 1) + "\"";
						if(mSession.getVideoTrack() instanceof H264) {
							insertQuery += ",\"" + mConfig.getProfileLevel() + "-" + mConfig.getB64PPS() + "-" + mConfig.getB64SPS() + "\"";
						} else {
							insertQuery += ",null";
						}
					}
					insertQuery += ")";
					System.out.println(insertQuery);
					Global.conn.executeInsert(insertQuery);
					Global.mUploaders.get(Global.getUploaderIndex(u)).sessionId = Global.conn.getId("SELECT sessionId FROM sessions WHERE name=\'" + title + "\'");
				}
			}
			oos.writeObject(ports);
		}
		
		oos.close();
		ois.close();
		mClient.close();
	}

	public Session getSession() {
		return mSession;
	}

	public MP4Config getMP4Config() {
		return mConfig;
	}

	public InetAddress getRemoteAddress() {
		return mAddr;
	}

	public VideoQuality getVideoQuality() {
		return mVideoQuality;
	}

	public int getSSRC() {
		return SSRC;
	}
}
