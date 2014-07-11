package com.example.bcast.session;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;

import com.example.bcast.Global;
import com.example.bcast.audio.AudioStream;
import com.example.bcast.stream.Stream;
import com.example.bcast.video.MP4Config;
import com.example.bcast.video.VideoStream;

public class Session implements Serializable {
	private static final long serialVersionUID = 1L;
//	private static final int DEFAULT_TCP_PORT = 25122;
	public final static String TAG = "Session";
	private InetAddress mOrigin;
	private InetAddress mDestination;
	private long mTimestamp;
	
	private static Object sLock = new Object();
	
	private int mTimeToLive = 64;
	private AudioStream mAudioStream = null;
	private VideoStream mVideoStream = null;
	
	public Session() {
		this(null, null);
		try {
			mOrigin = InetAddress.getLocalHost();
		} catch(Exception ignore) {
			mOrigin = null;
		}
	}
	
	public Session(InetAddress origin, InetAddress destination) {
		mOrigin = origin;
		mDestination = destination;
		long uptime = System.currentTimeMillis();
		mTimestamp = (uptime/1000)<<32 & (((uptime-((uptime/1000)*1000))>>32)/1000);
	}
	
	public void addAudioStream(AudioStream audioStream) {
		mAudioStream = audioStream;
	}
	
	public void addVideoStream(VideoStream videoStream) {
		mVideoStream = videoStream;
	}
	
	public void removeAudioTrack() {
		mAudioStream = null;
	}
	
	public void removeVideoTrack() {
		mVideoStream = null;
	}

	public AudioStream getAudioTrack() {
		return mAudioStream;
	}
	
	public VideoStream getVideoTrack() {
		return mVideoStream;
	}
	
	public void setOrigin(InetAddress origin) {
		mOrigin = origin;
	}
	
	public void setDestination(InetAddress destination) throws IllegalStateException {
		mDestination =  destination;
	}
	
	public String getSessionDescription() throws IllegalStateException, IOException {
		if(mDestination == null) {
			throw new IllegalStateException("Destination was not set!");
		}
		synchronized (sLock) {
			StringBuilder sessionDescription = new StringBuilder();
			sessionDescription.append("v=0\r\n");
			// TODO: Add IPV6 support
			sessionDescription.append("o=- "+mTimestamp+" "+mTimestamp+" IN IP4 "+(mOrigin==null?"127.0.0.1":mOrigin.getHostAddress())+"\r\n");
			sessionDescription.append("s=Unnamed\r\n");
			sessionDescription.append("i=N/A\r\n");
			sessionDescription.append("c=IN IP4 "+mDestination.getHostAddress()+"\r\n");
			// t=0 0 means the session is permanent (we don't know when it will stop)
			sessionDescription.append("t=0 0\r\n");
			sessionDescription.append("a=recvonly\r\n");
			// Prevents two different sessions from using the same peripheral at the same time
			if (mAudioStream != null) {
				sessionDescription.append(mAudioStream.generateSessionDescription());
				sessionDescription.append("a=control:trackID="+0+"\r\n");
			}
			if(mVideoStream != null) {
				
				// TODO: change this dynamically
//				MP4Config config = Global.mUploaders.get(0).mConfig;
				
				// TODO: change the "5006" dynamically
//				String conf =  "m=video " + "5006" + " RTP/AVP 96\r\n" +
//				"a=rtpmap:96 H264/90000\r\n" +
//				"a=fmtp:96 packetization-mode=1;profile-level-id="+config.getProfileLevel()+ "sprop-parameter-sets=Z0KAFOkCg/I=,aM4G4g==;\r\n"/*";sprop-parameter-sets="+config.getB64SPS()+","+config.getB64PPS()+";\r\n"*/;
				
				sessionDescription.append(mVideoStream.generateSessionDescription());
				sessionDescription.append("a=control:trackID="+1+"\r\n");
			}			
			return sessionDescription.toString();
		}
	}
	
	public InetAddress getDestination() {
		return mDestination;
	}

	public boolean trackExists(int id) {
		if (id==0) 
			return mAudioStream != null;
		else
			return mVideoStream != null;
	}
	
	public Stream getTrack(int id) {
		if (id==0)
			return mAudioStream;
		else
			return mVideoStream;
	}
	
	public long getBitrate() {
		long sum = 0;
		if(mAudioStream != null) {
			sum += mAudioStream.getBitrate();
		}
		if(mVideoStream != null) {
			sum += mVideoStream.getBitrate();
		}
		return sum;
	}
	
	public boolean isStreaming() {
		if((mAudioStream != null && mAudioStream.isStreaming()) || (mVideoStream != null && mVideoStream.isStreaming())) {
			return true;
		} else {
			return false;
		}
	}
	
	public void start(int id) throws IllegalStateException, IOException {
		Stream stream = null;
		synchronized(sLock) {
			if(id == 0) {
				stream = mAudioStream;
			} else {
				stream = mVideoStream;
			}
			if(stream != null && !stream.isStreaming()) {
				stream.setTimeToLive(mTimeToLive);
				stream.setDestinationAddress(mDestination);
				stream.start();
			}
		}
	}
	
	public void start() throws IllegalStateException,IOException {
		synchronized(sLock) {
			if(mDestination.isMulticastAddress()) {
				//TODO: something
			}
		}
		start(0);
		start(1);
	}
	
	public void stop(int id) throws IllegalStateException,IOException {
		Stream stream = null;
		if(id == 0) {
			stream = mAudioStream;
		} else {
			stream = mVideoStream;
		}
		if(stream != null) {
			stream.stop();
		}
	}
	
	public void stop() throws IllegalStateException, IOException {
		stop(0);
		stop(1);
	}
	
	public void flush() {
		synchronized(sLock) {
			if(mVideoStream != null) {
				mVideoStream.stop();
				mVideoStream = null;
			}
			if(mAudioStream != null) {
				mAudioStream.stop();
				mAudioStream = null;
			}
		}
	}
	
	public boolean equals(Session session) {
//		if(mVideoStream.equals(session.getVideoTrack()) &&
//				mAudioStream.equals(session.getAudioTrack())) {
		if(mVideoStream == session.getVideoTrack() &&
				mAudioStream == session.getAudioTrack()) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
