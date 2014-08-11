package com.example.bcast.rtsp;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.SocketException;

import com.example.bcast.utils.Utils;

public class RequestListener extends Thread implements Runnable {
	public final static String TAG = "RequestListener: ";
	public final static boolean DEBUGGING = true;
	public final static boolean LOGGING = true;
	
	private ServerSocket mServer = null;
//	private ArrayList<Pair<Session, InetAddress, MP4Config, VideoQuality, Integer>> mPairs = null;
	
//	public RequestListener(int port/*, ArrayList<Pair<Session, InetAddress, MP4Config, VideoQuality, Integer>> pairs*/) throws IOException {
//		Utils.LOG(TAG + "Creating the RTSP Server listener", DEBUGGING, LOGGING);
//		try {
////			mPairs = pairs;
//			mServer = new ServerSocket(port);
//			start();
//		} catch (BindException e) {
//			Utils.LOG(TAG + "Port already in use", DEBUGGING, LOGGING);
//			throw e;
//		}
//	}
	
	public RequestListener(int port) throws IOException {
		Utils.LOG(TAG + "Creating the RTSP Server listener", DEBUGGING, LOGGING);
		try {
			mServer = new ServerSocket(port);
			start();
		} catch (BindException e) {
			Utils.LOG(TAG + "Port already in use", DEBUGGING, LOGGING);
			System.err.println("Port already in use");
			//postError(e, ERROR_BIND_FAILED);
			throw e;
		}
	}
	
	@Override
	public void run() {
		Utils.LOG(TAG + "RTSP server listening on port " + mServer.getLocalPort(), DEBUGGING, LOGGING);
		while(!Thread.interrupted()) {
			try {
				new WorkerThread(mServer.accept()).start();
			} catch (SocketException e) {
				break;
			} catch (IOException e) {
				Utils.LOG(TAG + e.getMessage(), DEBUGGING, LOGGING);
				continue;
			}
		}
		Utils.LOG(TAG + "RTSP server stopped", DEBUGGING, LOGGING);
	}
	
	public void kill() {
		try {
			mServer.close();
		} catch (IOException e) {}
		try {
			this.join();
		} catch (InterruptedException ignore) {}
	}
}
