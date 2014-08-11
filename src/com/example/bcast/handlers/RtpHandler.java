package com.example.bcast.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Date;

import javax.swing.Timer;

import com.example.bcast.Global;
import com.example.bcast.Uploader;
import com.example.bcast.Viewer;
import com.example.bcast.utils.Utils;

public abstract class RtpHandler extends AbstractHandler {
	
	private static String TAG = "Handler: ";

	public RtpHandler(DatagramSocket socket, Uploader uploader, int bufferLength, int bufferTyp) {
		super(socket, uploader, bufferLength);
		this.bufferType = bufferTyp;
		if(bufferTyp == 1) {
			TAG = "AudioHandler: ";
		} else if(bufferTyp == 2) {
			TAG = "VideoHandler: ";
		} else {
			TAG = "RtpHandler: ";
		}
		if(bufferLength > 0) {
			if(bufferTyp == 1) {
				this.mBuffer = new AudioBuffer(socket, mUploader.mSession.getAudioTrack().mQuality.samplingRate, bufferLength);
System.out.println("buffer is " + getBufferType(bufferType));
			} else if(bufferType == 2) {
				this.mBuffer = new VideoBuffer(socket, mUploader.mSession.getVideoTrack().mQuality.framerate, bufferLength);
System.out.println("buffer is " + getBufferType(bufferType));
			}
		}
		
		this.mActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(streamingCounter == 0) {
					kill();
				} else {
					streamingCounter--;
				}
			}
		};
		
		this.mBufferListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				uploaderIndex = Global.getUploaderIndex(mUploader);
				if(Global.mUploaders.get(uploaderIndex).mViewers.size() > 0) {
System.err.println("[" + dateFormat.format(new Date()) + "] "
	+ getBufferType(bufferType) + "Handler: Sending a second worth of data.");
					// TODO: here pass the return value from the sendFrame method to XugglerConverter
					mBuffer.sendPacket(Global.mUploaders.get(uploaderIndex).mViewers);
				}
			}
		};
	}

	@Override
	public void run() {
		try {
			long timestamp = 0;
			int count = 0;
			int MBs = 0;
System.out.println("TAG = " + TAG + ", bufferType = " + this.bufferType);
			while(!Thread.interrupted()) {
				mSocket.receive(mPacket);

				Utils.logToFile(Utils.getCurrentTimestamp() + " " + getBufferType(bufferType) + " packet received of size " + mPacket.getLength(), getBufferType(bufferType).toLowerCase() + "_received.txt");
				if(firstTimeOnly) {
					Global.mUploaders.get(Global.getUploaderIndex(mUploader)).isStreaming = true;
					timer = new Timer(5000, mActionListener);
					timer.start();
					firstTimeOnly = false;
					Utils.LOG(TAG + "Received the first video packet! (" + mPacket.getLength() + " bytes)", DEBUGGING, LOGGING);
					timestamp = System.currentTimeMillis();
				}
				streamingCounter = 3;
				stillStreaming = true;
				if(mBufferLength > 0) {
					if(!bufferingStarted) {
						if(System.currentTimeMillis() - timestamp >= mBufferLength * 1000) {
							bufferTimer.start();
							bufferingStarted = true;
						}
					}
					mBuffer.addPacket(mPacket);
				} else {
					if(mUploader != null) {
						// TODO: make this less processing-power-consuming
						// Not too cost effective searching every time for number of viewers
						uploaderIndex = Global.getUploaderIndex(mUploader);
						if(Global.mUploaders.get(uploaderIndex).mViewers.size() > 0) {
							for(Viewer v : Global.mUploaders.get(uploaderIndex).mViewers) {
								if(v.play) {
									mPacket.setPort(Global.videoSendingPort);
									mPacket.setAddress(v.mDestination);
									mSocket.send(mPacket);
									switch(bufferType) {
									case 1:
										v.audioBytesSent += mPacket.getLength();
										if(v.audioBytesSent > 10240) {
											Utils.LOG(TAG + Global.mUploaders.get(uploaderIndex).mViewers.size() + " viewers connected to " + mUploader.mName + " stream.", DEBUGGING, LOGGING);
											v.audioMBs++;
											Utils.LOG(TAG + "Sent " + (v.audioMBs * 10) + "Kb to " + v.mDestination + ":" + Global.audioSendingPort, DEBUGGING, LOGGING);
											v.audioBytesSent -= 10240;
										}
										break;
									case 2:
										v.videoBytesSent += mPacket.getLength();
										if(v.videoBytesSent > 1048576) {
											Utils.LOG(TAG + Global.mUploaders.get(uploaderIndex).mViewers.size() + " viewers connected to " + mUploader.mName + " stream.", DEBUGGING, LOGGING);
											v.videoMBs++;
											Utils.LOG(TAG + "Sent " + v.videoMBs + "Mb to " + v.mDestination + ":" + Global.videoSendingPort, DEBUGGING, LOGGING);
											v.videoBytesSent -= 1048576;
										}
										break;
									}
								}
							}
						}
					}
				}
				switch(bufferType) {
				case 1:
					if (count > 10240) {
						MBs++;
						System.err.println("[" + dateFormat.format(new Date()) + "] " + TAG + (MBs * 10) + "Kb of audio data received from "
								+ mPacket.getAddress());
						count -= 10240;
					}
					break;
				case 2:
					if (count > 1048576) {
						MBs++;
						System.err.println("[" + dateFormat.format(new Date()) + "] " + TAG + MBs + "Mb of video data received from "
								+ mPacket.getAddress());
						count -= 1048576;
					}
					break;
				}
				count += mPacket.getLength();
			}
		} catch (IOException e) {
			Utils.LOG(
					getBufferType(bufferType) + "Handler: Error while receiving / sending " + getBufferType(bufferType).toLowerCase() + " packets",
					DEBUGGING, LOGGING);
		}
	}

	@Override
	public void kill() {
		if(!Thread.interrupted()) {
			stillStreaming = false;
			Global.mUploaders.get(Global.getUploaderIndex(mUploader)).isStreaming = false;
			this.interrupt();
		}
	}

}
