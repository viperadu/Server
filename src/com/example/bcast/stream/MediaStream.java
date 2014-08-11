package com.example.bcast.stream;

import java.io.IOException;
import java.net.InetAddress;

import com.example.bcast.Global;

public abstract class MediaStream implements Stream {

	protected int mRtpPort = 0, mRtcpPort = 0;
	public static final int mMediaRecorderMode = 0;
	public static final int mMediaCodecMode = 1;
	protected int mEncodingMode = mMediaCodecMode;

	public void start() throws IllegalStateException, IOException {

	}

	public void stop() {

	}

	public void setTimeToLive(int timeToLive) throws IOException {

	}

	public void setDestinationAddress(InetAddress dest) {

	}

	public void setDestinationPorts(int rtpPort, int rtcpPort) {

	}

	public int[] getLocalPorts() {
		return null;
	}

	public int[] getDestinationPorts() {
		return new int[] {Global.audioSendingPort, Global.videoSendingPort};
	}

	public int getSSRC() {
		return 0;
	}

	public abstract long getBitrate();

	public abstract String generateSessionDescription() throws IllegalStateException, IOException;

	public boolean isStreaming() {
		return false;
	}

}
