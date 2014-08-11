package com.example.bcast.audio;

import com.example.bcast.stream.MediaStream;

public abstract class AudioStream extends MediaStream {
	protected int mAudioSource;
	protected int mOutputFormat;
	protected int mAudioEncoder;
	public AudioQuality mQuality;// = AudioQuality.DEFAULT_AUDIO_QUALITY.clone();
	
	public void setAudioQuality(AudioQuality quality) {
		mQuality = quality;
	}
	
	public AudioQuality getAudioQuality() {
		return mQuality;
	}
	
	//TODO: change this with the proper bitrate
	@Override
	public long getBitrate() {
		return mQuality.bitRate;
	}
	
	
}
