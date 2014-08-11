package com.example.bcast.video;

import java.io.IOException;
import java.io.Serializable;

import com.example.bcast.Global;

public class H263 extends VideoStream implements Serializable {
	private static final long serialVersionUID = -8892153046515407578L;
	
	@Override
	public String generateSessionDescription() throws IllegalStateException,
			IOException {
		return "m=video " + /*String.valueOf(getDestinationPorts()[1])*/ Global.videoSendingPort + " RTP/AVP 96\r\n" +
				"a=rtpmap:96 H263-1998/90000\r\n";
	}
}
