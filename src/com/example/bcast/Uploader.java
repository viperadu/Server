package com.example.bcast;

import java.net.InetAddress;
import java.util.ArrayList;

import com.example.bcast.session.Session;
import com.example.bcast.video.MP4Config;

public class Uploader {
	public String mName;
	public Session mSession;
	public Server mServer;
	public InetAddress mOrigin;
	public int mSSRC;
	public MP4Config mConfig;
	
	public ArrayList<Viewer> mViewers;
	
	public Uploader(String name, Session session, Server server, InetAddress origin, int ssrc, MP4Config config) {
		mName = name;
		mSession = session;
		mServer = server;
		mOrigin = origin;
		mSSRC = ssrc;
		mConfig = config;
		mViewers = new ArrayList<Viewer>();
	}
	
	public void addViewer(Viewer viewer) {
		mViewers.add(viewer);
	}
	
	public void removeViewer(Viewer viewer) {
		mViewers.remove(viewer);
	}

}
