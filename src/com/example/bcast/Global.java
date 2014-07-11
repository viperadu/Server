package com.example.bcast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Queue;

import com.example.bcast.session.Session;
import com.example.bcast.utils.Pair;
import com.example.bcast.video.MP4Config;
import com.example.bcast.video.VideoQuality;

public class Global {
	public static volatile ArrayList<Pair<Session, InetAddress, MP4Config, VideoQuality, Integer>> mPairs;
	public static volatile ArrayList<Queue<DatagramPacket>> mQueues;
	
	public static volatile ArrayList<Uploader> mUploaders;
	// TODO: merge this with Pair object
	public static int videoSendingPort;
	public static int audioSendingPort;
	
	public static String localIPAddress;
}
