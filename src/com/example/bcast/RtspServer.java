package com.example.bcast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.WeakHashMap;

import com.example.bcast.rtsp.RequestListener;
import com.example.bcast.session.Session;
import com.example.bcast.session.SessionBuilder;
import com.example.bcast.utils.Pair;
import com.example.bcast.video.MP4Config;
import com.example.bcast.video.VideoQuality;

/**
 * @author Radu
 * This class offers the available streams to viewers.
 */
public class RtspServer {
	public static final String TAG = "RtspServer: ";
	public static final boolean DEBUGGING = true;
	public static final boolean LOGGING = true;
	
	public static String SERVER_NAME = "Radu RTSP Server";
	
	public static final int HTTP_PORT = 8080;
	public static final int DEFAULT_RTSP_PORT = 8086;
	
	public final static int ERROR_BIND_FAILED = 0x00;
	public final static int ERROR_START_FAILED = 0x01;
	public final static int MESSAGE_STREAMING_STARTED = 0X00;
	public final static int MESSAGE_STREAMING_STOPPED = 0X01;
	
	public final static String KEY_ENABLED = "rtsp_enabled";
	public final static String KEY_PORT = "rtsp_port";
	
	public boolean mStarted = false;
	public static SessionBuilder mSessionBuilder;
	protected boolean mEnabled = true;
	protected int mPort = DEFAULT_RTSP_PORT;
	public static WeakHashMap<Session, Object> mSessions = new WeakHashMap<Session, Object>(2);
	
	private RequestListener mListenerThread;
	private boolean mRestart = false;
	private final LinkedList<CallbackListener> mListeners = new LinkedList<CallbackListener>();

	public RtspServer() {
		start();
	}
	
	public interface CallbackListener {
		void onError(RtspServer server, Exception e, int error);

		void onMessage(RtspServer server, int message);
	}
	
	public void addCallbackListener(CallbackListener listener) {
		synchronized(mListeners) {
			if(mListeners.size() > 0) {
				for(CallbackListener cl : mListeners) {
					if(cl == listener) return;
				}
			}
			mListeners.add(listener);
		}
	}
	
	public void removeCallbackListener(CallbackListener listener) {
		synchronized(mListeners) {
			mListeners.remove(listener);
		}
	}
	
	public int getPort() {
		return mPort;
	}
	
	public void setPort(int port) {
		mPort = port;
//		Editor editor = mSharedPreferences.edit();
//		editor.putString(KEY_PORT, String.valueOf(port));
//		editor.commit();
	}
	
	public void start() {
		if(!mEnabled || mRestart) {
			stop();
		}
		if(mEnabled && mListenerThread == null) {
			try {
				mListenerThread = new RequestListener(mPort);
				mStarted = true;
			} catch(Exception e) {
				mListenerThread = null;
			}
		}
		mRestart = false;
	}
	
	public void stop() {
		if(mListenerThread != null) {
			try {
				mListenerThread.kill();
				for(Session session : mSessions.keySet()) {
					if(session != null) {
						if(session.isStreaming()) {
							session.stop();
							mStarted = false;
						}
					}
				}
			} catch(Exception e) {
			} finally {
				mListenerThread = null;
				mStarted = false;
			}
		}
	}
	
	public boolean isStreaming() {
		for(Session session : mSessions.keySet()) {
			if(session != null) {
				if(session.isStreaming()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isEnabled() {
		return mEnabled;
	}
	
	public long getBitrate() {
		long bitrate = 0;
		for(Session session : mSessions.keySet()) {
			if(session != null) {
				if(session.isStreaming()) {
					bitrate += session.getBitrate();
				}
			}
		}
		return bitrate;
	}
	
	public void onCreate() {
		mPort = DEFAULT_RTSP_PORT;
		mEnabled = true;
	}
	
	public void onDestroy() {
		stop();
	}
}
