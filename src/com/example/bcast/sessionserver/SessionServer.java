package com.example.bcast.sessionserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedList;

import com.example.bcast.Global;
import com.example.bcast.Server;
import com.example.bcast.session.Session;
import com.example.bcast.utils.Pair;
import com.example.bcast.utils.Utils;
import com.example.bcast.video.MP4Config;
import com.example.bcast.video.VideoQuality;

public class SessionServer extends Thread {
	public final static String TAG = "SessionServer: ";
	public final static boolean DEBUGGING = true;
	public final static boolean LOGGING = true;

	@SuppressWarnings("unused")
	private int mPort;
	private ServerSocket mSocket = null;
	private LinkedList<NewSessionHandler> mNewSessionHandlers = null;
	private Pair<Session, InetAddress, MP4Config, VideoQuality, Integer> mPair = null;
	
	public SessionServer(int port) throws IOException {
		mPort = port;
		mSocket = new ServerSocket(port, Integer.MAX_VALUE, InetAddress.getByName(Global.localIPAddress));
		
		Utils.LOG(TAG + mSocket.getLocalSocketAddress().toString(), DEBUGGING, LOGGING);
		
		mNewSessionHandlers = new LinkedList<NewSessionHandler>();
//		mPairs = list;//new ArrayList<Pair<Session, InetAddress>>();
		this.start();
	}

	@Override
	public void run() {
		NewSessionHandler handler = null;
//		Utils.printNetworkInterfaces(TAG);
		Utils.LOG(TAG + "Waiting for client...", DEBUGGING, LOGGING);
		
		while(!Thread.interrupted()) {
			try {
				handler = new NewSessionHandler(mSocket.accept());
			} catch (IOException e) {
				Utils.LOG(TAG + "", DEBUGGING, LOGGING);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				Utils.LOG(TAG + "", DEBUGGING, LOGGING);
				e.printStackTrace();
			}
			mNewSessionHandlers.add(handler);
//			while(handler.getSession() != null && handler.getRemoteAddress() != null) {
//				try {
//					Thread.sleep(200);
//				} catch (InterruptedException e) {
//					Utils.LOG(TAG + "Interrupted during thread sleep", DEBUGGING, LOGGING);
//					e.printStackTrace();
//				}
//			}
//			mPair = new Pair(handler.getSession(), handler.getRemoteAddress(), handler.getMP4Config(), handler.getVideoQuality(), handler.getSSRC());
//			Global.mPairs.add(mPair);
		}
	}
	
	
	
	public ArrayList<Pair<Session, InetAddress, MP4Config, VideoQuality, Integer>> getPairs() {
		return Global.mPairs;
	}
	
	public Pair<Session, InetAddress, MP4Config, VideoQuality, Integer> getPair() {
		return mPair;
	}
	
	public Pair<Session, InetAddress, MP4Config, VideoQuality, Integer> getPair(int n) {
		return Global.mPairs.get(n);
	}
}
