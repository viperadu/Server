package com.example.bcast.video;

import java.io.Serializable;

public class MP4Config implements Serializable {
	private String mProfileLevel, mPPS, mSPS;
	
	public MP4Config(String profileLevel, String pps, String sps) {
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

