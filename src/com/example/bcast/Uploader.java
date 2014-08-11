package com.example.bcast;

import java.net.InetAddress;
import java.util.ArrayList;

import com.example.bcast.audio.AudioQuality;
import com.example.bcast.session.Session;
import com.example.bcast.video.MP4Config;
import com.example.bcast.video.VideoQuality;

public class Uploader {
	public int sessionId;
	public String mName;
	public Session mSession;
	public Server mServer;
	public InetAddress mOrigin;
	public int mSSRC;
	public MP4Config mConfig;
	public boolean isStreaming;
	
	public ArrayList<Viewer> mViewers;
	
	public Uploader(String name, Session session, Server server, InetAddress origin, int ssrc, MP4Config config) {
		sessionId = 0;
		mName = name;
		mSession = session;
		mServer = server;
		mOrigin = origin;
		mSSRC = ssrc;
		mConfig = config;
		isStreaming = true;
		mViewers = new ArrayList<Viewer>();
	}
	
	public void addViewer(Viewer viewer) {
		mViewers.add(viewer);
	}
	
	public void removeViewer(Viewer viewer) {
		mViewers.remove(viewer);
	}

}
