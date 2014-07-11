package com.example.bcast.video;

import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;

public class MP4Config implements Serializable {
	private static final long serialVersionUID = 1L;
	private MP4Parser mp4Parser = null;
	private String mProfileLevel, mPPS, mSPS;
	
	public MP4Config(String profileLevel, String pps, String sps) {
		mp4Parser = null;
		mProfileLevel = profileLevel;
		mPPS = pps;
		mSPS = sps;
	}
	
	public String getProfileLevel() {
		return mProfileLevel;
	}
	
	public String getB64SPS() {
		return mSPS;
	}
	
	public String getB64PPS() {
		return mPPS;
	}
}

class MP4Parser implements Serializable {
	public static final String TAG = "MP4Parser";
	public static final boolean DEBUGGING = true;
	private HashMap<String, Long> boxes = new HashMap<String, Long>();
	private long pos = 0;
	private final RandomAccessFile file = null;
}
