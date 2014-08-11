package com.example.bcast.video;

import com.example.bcast.stream.MediaStream;

public abstract class VideoStream extends MediaStream {
	public VideoQuality mQuality;// = VideoQuality.DEFAULT_VIDEO_QUALITY.clone();
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

	// TODO: change this with the proper bitrate
	@Override
	public long getBitrate() {
		return mQuality.resX * mQuality.resY * mQuality.framerate;
	}
}
