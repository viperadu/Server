package com.example.bcast.video;

import java.io.IOException;
import java.io.Serializable;

import com.example.bcast.Global;

public class H264 extends VideoStream implements Serializable {
	private static final long serialVersionUID = 75172938245372591L;
	private MP4Config config;
	
	public H264(MP4Config config) {
		this.config = config;
	}
	
	public void setMP4Config(MP4Config config) {
		this.config = config;
	}
	
	@Override
	public String generateSessionDescription() throws IllegalStateException, IOException{
		return  "m=video " + Global.videoSendingPort + " RTP/AVP 96 \r\n" +
				"a=rtpmap:96 H264/90000\r\n" + 
				"a=fmtp:96 packetization-mode=1;profile-level-id=" + config.getProfileLevel() + 
				";sprop-parameter-sets=" + config.getB64SPS() + "," + config.getB64PPS() + ";\r\n";
	}
}
