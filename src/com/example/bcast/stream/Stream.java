package com.example.bcast.stream;

import java.io.IOException;
import java.net.InetAddress;

public interface Stream {
	public void start() throws IllegalStateException, IOException;
	public void stop();
	public void setTimeToLive(int timeToLive) throws IOException;
	public void setDestinationAddress(InetAddress dest);
	public void setDestinationPorts(int rtpPort, int rtcpPort);
	public int[] getLocalPorts();
	public int[] getDestinationPorts();
	public int getSSRC();
	public long getBitrate();
	public String generateSessionDescription() throws IllegalStateException, IOException;
	public boolean isStreaming();
}
