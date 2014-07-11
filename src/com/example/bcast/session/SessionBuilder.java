package com.example.bcast.session;

import java.net.InetAddress;

@SuppressWarnings("unused")
public class SessionBuilder {
	public static final String TAG = "SessionBuilder: ";
	public static final int VIDEO_NONE = 0;
	public static final int VIDEO_H264 = 1;
	public static final int VIDEO_H263 = 2;
	public static final int AUDIO_NONE = 0;
	public static final int AUDIO_AMRNB = 3;
	public static final int AUDIO_AAC = 5;

	private int mVideoEncoder = VIDEO_H263; 
	private int mAudioEncoder = AUDIO_AMRNB;
	private int mTimeToLive = 64;
	private InetAddress mOrigin = null;
	private InetAddress mDestination = null;
}
