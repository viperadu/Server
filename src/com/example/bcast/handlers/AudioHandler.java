package com.example.bcast.handlers;

import java.net.DatagramSocket;

import com.example.bcast.Uploader;

public class AudioHandler extends RtpHandler {
//	private static final String TAG = "AudioHandler: ";
//	private static final boolean DEBUGGING = true;
//	private static final boolean LOGGING = true;
//	
//	private DatagramSocket audioSocket;
//	private DatagramPacket audioPacket;
//	private byte[] audioData;
//	private Uploader mUploader;
//	private int uploaderIndex = 0;
//	
//	private Timer timer;
//	private volatile boolean stillStreaming;
//	private boolean firstTimeOnly = true;
	
//	private ActionListener mActionListener = new ActionListener() {
//
//		public void actionPerformed(ActionEvent arg0) {
//			if(!stillStreaming) {
//				kill();
//			}
//			stillStreaming = false;
//		}
//		
//	};

	public AudioHandler(DatagramSocket socket, Uploader uploader, int bufferLength) {
//		this.audioData = new byte[1500];
//		this.audioSocket = socket;
//		this.audioPacket = new DatagramPacket(audioData, audioData.length);
//		this.mUploader = uploader;
//		this.uploaderIndex = Global.getUploaderIndex(uploader);
		super(socket, uploader, bufferLength, BUFFER_TYPE_AUDIO);
//		this.mBuffer = new AudioBuffer(socket, bufferLength);
	}


/*	@Override
	public void run() {
		try {
			int MBs = 0;
			int count = 0;
			while(!Thread.interrupted()) {
				mSocket.receive(mPacket);
				Utils.logToFile(Utils.getCurrentTimestamp() + " Audio packet received of size " + mPacket.getLength(), "audio_received.txt");
				if(firstTimeOnly) {
					timer = new Timer(5000, mActionListener);
					timer.start();
					firstTimeOnly = false;
					Utils.LOG(TAG + "Received the first audio packet! (" + mPacket.getLength() + " bytes)", DEBUGGING, LOGGING);
				}
				stillStreaming = true;
				if(mBufferLength > 0) {
					
				} else {
					if(mUploader != null) {
						uploaderIndex = Global.getUploaderIndex(mUploader);
						if(Global.mUploaders.get(uploaderIndex).mViewers.size() > 0) {
							for(Viewer v : Global.mUploaders.get(uploaderIndex).mViewers) {
								if(v.play) {
									mPacket.setPort(Global.audioSendingPort);
									mPacket.setAddress(v.mDestination);
									mSocket.send(mPacket);
									v.audioBytesSent += mPacket.getLength();
									if(v.audioBytesSent > 102400) {
										Utils.LOG(TAG + Global.mUploaders.get(uploaderIndex).mViewers.size() + " viewers connected to " + mUploader.mName + " stream.", DEBUGGING, LOGGING);
										v.audioMBs++;
										Utils.LOG(TAG + "Sent " + v.audioMBs + "Kb to " + v.mDestination + ":" + Global.audioSendingPort, DEBUGGING, LOGGING);
										v.audioBytesSent -= 102400;
									}
								}
							}
						}
					}
				}
				
				if (count > 102400) {
					MBs++;
					DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
					Date date = new Date();
//					Utils.LOG(
//							TAG + (MBs*100) + "Kb of audio data received from "
//									+ audioPacket.getAddress() + "  ("
//									+ dateFormat.format(date) + ")", DEBUGGING,
//							LOGGING);
					System.err.println(TAG + (MBs*100) + "Kb of audio data received from "
							+ mPacket.getAddress() + "  ("
							+ dateFormat.format(date) + ")");
					count -= 102400;
				}
				count += mPacket.getLength();
			}
		} catch (IOException e) {
			Utils.LOG(
					"AudioHandler: Error while receiving / sending audio packets",
					DEBUGGING, LOGGING);
		}
	}*/
	
	/*public void kill() {
		if(!Thread.interrupted()) {
			this.interrupt();
		}
	}*/
}