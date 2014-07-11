package com.example.bcast.video;

import java.io.Serializable;

import com.example.bcast.stream.MediaStream;

public abstract class VideoStream extends MediaStream {
	public VideoQuality mQuality = VideoQuality.DEFAULT_VIDEO_QUALITY.clone();
	protected int mVideoEncoder, mCameraId = 0;
	protected boolean mCameraOpenedManually = true;
//	protected boolean mFlashState = false;
	protected boolean mSurfaceReady = false;
	protected boolean mUnlocked = false;
	protected boolean mPreviewStarted = false;
	
	public VideoStream() {
		
	}
	
	public void setVideoQuality(VideoQuality quality) {
		mQuality = quality;
	}
	
	public VideoQuality getVideoQuality() {
		return mQuality;
	}
	
//	public String generateSessionDescription() throws IllegalStateException, IOException {
//		MP4Config config = new MP4Config("42800c", "Z0KADOkCg/I=", "pps=aM4G4g==");
//		return "m=video "+String.valueOf(getDestinationPorts()[0])+" RTP/AVP 96\r\n" +
//		"a=rtpmap:96 H264/90000\r\n" +
//		"a=fmtp:96 packetization-mode=1;profile-level-id="+config.getProfileLevel()+";sprop-parameter-sets="+config.getB64SPS()+","+config.getB64PPS()+";\r\n";
//	}
	
	
//	public boolean equals(VideoStream vStream) {
//		if(mVideoEncoder == vStream.mVideoEncoder &&
//				mCameraId == vStream.mCameraId &&
//				mQuality.equals(vStream.mQuality))
//	}
}
