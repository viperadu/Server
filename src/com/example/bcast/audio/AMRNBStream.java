package com.example.bcast.audio;

import java.io.Serializable;

import com.example.bcast.Global;

public class AMRNBStream extends AudioStream implements Serializable {
	private static final long serialVersionUID = -3434013960770199674L;
	
	@Override
	public String generateSessionDescription() {
		return "m=audio " + /*String.valueOf(getDestinationPorts()[0])*/ Global.audioSendingPort + " RTP/AVP 96\r\n" +
				"a=rtpmap:96 AMR/8000\r\n" +
				"a=fmtp:96 octet-align=1;\r\n";
	}
}
