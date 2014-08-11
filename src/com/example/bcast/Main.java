package com.example.bcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

import com.example.bcast.handlers.XugglerHandler;
import com.example.bcast.session.Session;
import com.example.bcast.sessionserver.SessionServer;
import com.example.bcast.utils.Pair;
import com.example.bcast.utils.Utils;
import com.example.bcast.video.MP4Config;
import com.example.bcast.video.VideoQuality;

public class Main {
	public static final String TAG = "Main: ";
	public static final boolean DEBUGGING = true;
	public static final boolean LOGGING = true;
	
	public static final int VIDEO_PORT = 25123;
	public static final int AUDIO_PORT = 25121;
	public static final int TCP_PORT = 25120;
	public static final int AVAILABLE_VIDEOS_PORT = 25118;
	
	public static MulticastSocket mSocket = null;
	
	public static RtspServer rtspServer = null;
	public static Server server = null;
	public static SessionServer sessionServer = null;
	public static RtcpServer rtcpServer = null;
	public static JsonServer jsonServer = null;
	
	public static void main(String[] args) {
		Utils.printNetworkInterfaces(TAG);
		Global.mPairs = new ArrayList<Pair<Session, InetAddress, MP4Config, VideoQuality, Integer>>();
		Global.mQueues = new ArrayList<Queue<DatagramPacket>>();
		Global.mUploaders = new ArrayList<Uploader>();
		
		Global.conn = new DatabaseConnection();
		Global.conn.init();
		Global.conn.close();
		try {
			mSocket = new MulticastSocket();
		} catch (IOException e1) {
			Utils.LOG(TAG + "IOException while opening the multicast socket", DEBUGGING, LOGGING);
			e1.printStackTrace();
		}
		
		try {
			sessionServer = new SessionServer(TCP_PORT);
		} catch (IOException e) {
			Utils.LOG(TAG + e.getMessage(), DEBUGGING, LOGGING);
			return;
		}
		
		try {
			jsonServer = new JsonServer(AVAILABLE_VIDEOS_PORT);
		} catch (IOException e) {
			Utils.LOG(TAG + e.getMessage(), DEBUGGING, LOGGING);
			return;
		}
//		Handler h = new Handler("music.flv", null, "first");
//		ConvertVideo cv = new ConvertVideo("music.flv", "rtmp://194.102.231.189:1935/Server/first.mp4");
//		Thread t = new Thread(cv);
//		t.start();
		
//		try {
//			rtcpServer = new RtcpServer(RTCP_PORT);
//		} catch (IOException e) {
//			Utils.LOG(TAG + e.getMessage(), DEBUGGING, LOGGING);
//			return;
//		}
		
//		server = new Server(VIDEO_PORT, AUDIO_PORT, mSocket);
		
		rtspServer = new RtspServer();
		
		
		Utils.LOG(TAG + "Successfully initialised the SessionServer, the Server and the RTSPServer", DEBUGGING, LOGGING);
	}
}
