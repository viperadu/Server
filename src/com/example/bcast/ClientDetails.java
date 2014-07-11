package com.example.bcast;

import java.net.InetAddress;

import com.example.bcast.session.Session;
import com.example.bcast.video.MP4Config;
import com.example.bcast.video.VideoQuality;

public class ClientDetails {
	public Session mSession;
	public InetAddress mOrigin;
	public MP4Config mConfig;
	public VideoQuality mVideoQuality;
	public int SSRC;
}
