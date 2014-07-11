package com.example.bcast.sessionserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.example.bcast.Global;
import com.example.bcast.Server;
import com.example.bcast.Uploader;
import com.example.bcast.audio.AudioQuality;
import com.example.bcast.session.Session;
import com.example.bcast.utils.Pair;
import com.example.bcast.utils.Utils;
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
	VideoQuality mVideoQuality = null;
	AudioQuality mAudioQuality = null;
	int SSRC = 0;

	public NewSessionHandler(Socket newClient) throws ClassNotFoundException,
			IOException {
		mClient = newClient;
		mAddr = mClient.getInetAddress();
		int[] ports = null;
		String title = "";
		Server server = null;
		
		try {
			ois = new ObjectInputStream(mClient.getInputStream());
			oos = new ObjectOutputStream(mClient.getOutputStream());
		} catch (IOException e) {
			Utils.LOG(TAG + e.getMessage(), DEBUGGING, LOGGING);
		}
		try {
			title = (String) ois.readObject();
		} catch (ClassNotFoundException e) {
			System.err.println("String - Class not found");
			title = "";
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
			mConfig = (MP4Config) ois.readObject();
		} catch (ClassNotFoundException e) {
			System.err.println("MP4Config - Class not found");
			mConfig = null;
		}
		
		try {
			SSRC = (Integer) ois.readObject();
		} catch (ClassNotFoundException e) {
			System.err.println("Integer - Class not found");
			SSRC = 0;
		}
		if (!title.equals("")) {
			Utils.LOG(TAG + "Successfully received String object from "
					+ mAddr, DEBUGGING, LOGGING);
		}
		if (mSession != null) {
			Utils.LOG(TAG + "Successfully received Session object from "
					+ mAddr, DEBUGGING, LOGGING);
			//We replace the session's origin with the server address
			mSession.setOrigin(mAddr);
			// We start the server that receives Audio / Video packets
			Uploader u = new Uploader(title, mSession, null, mAddr, SSRC, mConfig);
			Global.mUploaders.add(u);
			server = new Server(mSession);
			int index = Global.mUploaders.indexOf(u);
			u.mServer = server;
			Global.mUploaders.set(index, u);
			ports = server.getPorts();
			oos.writeObject(ports);
		}
		if (mConfig != null) {
			Utils.LOG(TAG + "Successfully received MP4Config object from "
					+ mAddr, DEBUGGING, LOGGING);
		}
		if (SSRC != 0) {
			Utils.LOG(TAG + "Successfully received Integer object from "
					+ mAddr, DEBUGGING, LOGGING);
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
