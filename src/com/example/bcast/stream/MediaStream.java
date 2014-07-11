package com.example.bcast.stream;

import java.io.IOException;
import java.net.InetAddress;

public abstract class MediaStream implements Stream {

	protected int mRtpPort = 0, mRtcpPort = 0;

	public void start() throws IllegalStateException, IOException {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

	public void setTimeToLive(int timeToLive) throws IOException {
		// TODO Auto-generated method stub

	}

	public void setDestinationAddress(InetAddress dest) {
		// TODO Auto-generated method stub

	}

	public void setDestinationPorts(int rtpPort, int rtcpPort) {
		// TODO Auto-generated method stub

	}

	public int[] getLocalPorts() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getDestinationPorts() {
//		return new int[] {5006, 5007};
		return new int[] {5004, 5006};
		a
	}

	public int getSSRC() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getBitrate() {
		// TODO Auto-generated method stub
		return 0;
	}

	public abstract String generateSessionDescription() throws IllegalStateException, IOException;

	public boolean isStreaming() {
		// TODO Auto-generated method stub
		return false;
	}

}
