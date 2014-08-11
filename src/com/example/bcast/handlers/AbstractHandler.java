package com.example.bcast.handlers;

import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.Timer;

import com.example.bcast.Global;
import com.example.bcast.Uploader;

public abstract class AbstractHandler extends Thread {
	protected static final boolean DEBUGGING = true;
	protected static final boolean LOGGING = true;
	
	protected static final int BUFFER_TYPE_AUDIO = 1;
	protected static final int BUFFER_TYPE_VIDEO = 2;
	protected int bufferType;
	 
	protected volatile DatagramSocket mSocket;
	protected DatagramPacket mPacket;
	protected byte[] mData;
	protected Uploader mUploader;
	protected int uploaderIndex = 0;
	
	protected Timer timer;
	protected Timer bufferTimer;
	protected volatile boolean stillStreaming;
	protected volatile int streamingCounter;
	protected boolean firstTimeOnly = true;
	protected AbstractBuffer mBuffer;
	protected boolean bufferingStarted = false;
	protected int mBufferLength = 0;
	
	protected DateFormat dateFormat;
	
	protected ActionListener mActionListener;
	protected ActionListener mBufferListener;
	
	public AbstractHandler(DatagramSocket socket, Uploader uploader, int bufferLength) {
		this.mData = new byte[1500];
		this.mSocket = socket;
		this.mPacket = new DatagramPacket(mData, mData.length);
		this.mUploader = uploader;
		this.uploaderIndex = Global.getUploaderIndex(mUploader);
		this.bufferTimer = new Timer(1000, mBufferListener);
		this.timer = new Timer(1000, mActionListener);
		this.dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		this.mBufferLength = bufferLength;
		this.streamingCounter = 3;
	}
	
	public abstract void run();
	
	public abstract void kill();
	
	public boolean isStillStreaming() {
		return stillStreaming;
	}
	
	protected String getBufferType(int type) {
		return type == 1 ? "Audio" : "Video";
	}
	
	protected int getPort(int type) {
		return type == 1 ? Global.audioRtcpSendingPort : Global.videoRtcpSendingPort;
	}
}
