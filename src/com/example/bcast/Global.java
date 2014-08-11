package com.example.bcast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

import com.example.bcast.session.Session;
import com.example.bcast.utils.Pair;
import com.example.bcast.video.MP4Config;
import com.example.bcast.video.VideoQuality;

public class Global {
	public static volatile ArrayList<Pair<Session, InetAddress, MP4Config, VideoQuality, Integer>> mPairs;
	public static volatile ArrayList<Queue<DatagramPacket>> mQueues;
	
	public static volatile ArrayList<Uploader> mUploaders;
	// TODO: merge this with Pair object
	public static final int audioSendingPort = 5004;
	public static final int audioRtcpSendingPort = 5005;
	public static final int videoSendingPort = 5006;
	public static final int videoRtcpSendingPort = 5007;
	
	public static String localIPAddress;
	public static InetAddress localAddress;
	
	public static final String serverIPAddress = "194.102.231.189";
	public static final String serverPort = "1935";
	public static final String serverAppName = "Server";
	
	public static final String RTMP = "rtmp://";
	public static final String RTSP = "rtsp://";
	
	public static DatabaseConnection conn;
	
	public static int getUploaderIndex(Uploader uploader) {
		synchronized(mUploaders) {
			for(int i=0; i<mUploaders.size(); i++) {
				if(mUploaders.get(i).mSSRC == uploader.mSSRC &&
						mUploaders.get(i).mName.equals(uploader.mName)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public static Uploader getUploaderByURI(String uri) {
		synchronized(mUploaders) {
			for(int i=0; i<mUploaders.size(); i++) {
				if(mUploaders.get(i).mName.equals(uri.substring(uri.lastIndexOf("/") + 1))) {
					return mUploaders.get(i);
				}
			}
		}
		return null;
	}
	
	private static void addViewer(Viewer viewer, int uploaderIndex) {
		synchronized(mUploaders) {
			mUploaders.get(uploaderIndex).mViewers.add(viewer);
		}
	}
	
	public static void addViewer(Viewer mViewer, Uploader uploader) {
		synchronized(mUploaders) {
			addViewer(mViewer, getUploaderIndex(uploader));
		}
	}
	
	private static int getViewerIndex(Viewer viewer, int uploaderIndex) {
		synchronized(mUploaders) {
			for(int i=0; i<mUploaders.get(uploaderIndex).mViewers.size(); i++) {
				if(mUploaders.get(uploaderIndex).mViewers.get(i).id == viewer.id) {
					return i;
				}
			}
		}
		return -1;
	}
	
	private static void removeViewer(int viewerIndex, int uploaderIndex) {
		synchronized(mUploaders) {
			if(viewerIndex != -1) {
				mUploaders.get(uploaderIndex).mViewers.remove(viewerIndex);
			}
		}
	}

	private static void removeViewer(Viewer viewer, int uploaderIndex) {
		synchronized(mUploaders) {
			removeViewer(getViewerIndex(viewer, uploaderIndex), uploaderIndex);
		}
	}

	public static void removeViewer(Viewer mViewer, Uploader mUploader) {
		synchronized(mUploaders) {
			removeViewer(mViewer, getUploaderIndex(mUploader));
		}
	}
	
	public static void addUploader(Uploader uploader) {
		boolean added = false;
		synchronized(mUploaders) {
			for(Uploader u : mUploaders) {
				if(u.isStreaming == false) {
					Global.mUploaders.set(Global.getUploaderIndex(u), uploader);
					added = true;
				}
			}
			if(!added) {
				Global.mUploaders.add(uploader);
			}
		}
	}
	
	public static int assignNewId() {
		int newId = 0;
		do {
			newId = new Random().nextInt();
		} while(checkIfIdExists(newId));
		return newId;
	}
	
	private static boolean checkIfIdExists(int id) {
		synchronized(mUploaders) {
			for(int i=0; i<Global.mUploaders.size(); i++) {
				for(Viewer v : Global.mUploaders.get(i).mViewers) {
					if(id == v.id) {
						return true;
					}
				}
			}
			return false;
		}
	}

	public static void pauseViewer(Viewer mViewer, Uploader mUploader) {
		mUploaders.get(getUploaderIndex(mUploader)).mViewers.get(getViewerIndex(mViewer, getUploaderIndex(mUploader))).play = false;
	}
	
	public static void playViewer(Viewer mViewer, Uploader mUploader) {
		mUploaders.get(getUploaderIndex(mUploader)).mViewers.get(getViewerIndex(mViewer, getUploaderIndex(mUploader))).play = true;
	}

	public static boolean checkIfTitleExists(String title) {
		for(int i=0; i<mUploaders.size(); i++) {
			if(mUploaders.get(i).mName.equals(title)) {
				return true;
			}
		}
		return false;
	}

	public static Uploader getUploaderBySession(Session mSession) {
		for(int i=0; i<mUploaders.size(); i++) {
			//TODO: change this maybe?
			if(mSession == mUploaders.get(i).mSession) {
				return mUploaders.get(i);
			}
		}
		return null;
	}

}
