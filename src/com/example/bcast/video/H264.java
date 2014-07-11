package com.example.bcast.video;

import java.io.IOException;
import java.io.Serializable;

public class H264 extends VideoStream implements Serializable {
	private static final long serialVersionUID = 75172938245372591L;
	private MP4Config config;
	
	public H264(MP4Config config) {
		this.config = config;
	}
	
	@Override
	public String generateSessionDescription() throws IllegalStateException, IOException{
		return this.config != null ? "m=video " + String.valueOf(getDestinationPorts()[1]) + " RTP/AVP 96 \r\n" +
				"a=rtpmap:96 H264/90000\r\n" + 
				"a=fmtp:96 packetization-mode=1;profile-level-id=" + config.getProfileLevel() + 
				";sprop-parameter-sets=" + config.getB64SPS() + "," + config.getB64PPS() + ";\r\n"
				:
				"m=video " + String.valueOf(getDestinationPorts()[1]) + "RTP/AVP 96 \r\n" +
				"a=rtpmap:96 H264/90000\r\n" + 
				"a=fmtp:96 packetization-mode=1;profile-level-id=42800c;sprop-parameter-sets=" + 
				"Z0KADOkCg/I=,aM4G4g==;\r\n"
				;
	}
}
