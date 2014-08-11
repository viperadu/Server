package com.example.bcast.handlers;

import java.io.IOException;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.jcodec.common.StringUtils;

import com.example.bcast.Global;
import com.example.bcast.Uploader;
import com.example.bcast.Viewer;
import com.example.bcast.utils.Utils;

public abstract class RtcpHandler extends AbstractHandler {
	private static final String TAG = "RtcpHandler: ";
	
	public RtcpHandler(DatagramSocket socket, Uploader uploader, int bufferLength, int bufferType) {
		super(socket, uploader, bufferLength);
		this.bufferType = bufferType;
		this.mBuffer = new RtcpBuffer(socket, bufferLength, bufferType);
		this.mActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(streamingCounter == 0) {
					kill();
				} else {
					streamingCounter--;
				}
			}
		};
		this.mBufferListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				uploaderIndex = Global.getUploaderIndex(mUploader);
				if(Global.mUploaders.get(uploaderIndex).mViewers.size() > 0) {
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
			while(!Thread.interrupted()) {
				mSocket.receive(mPacket);
				
				
// TODO: delete this when you're done
//String line = "";
//for(int i=0; i<mPacket.getLength(); i++) {
//	line += "[" + i + "]=" + String.format("%8s", Integer.toBinaryString((mPacket.getData()[i] + 256) % 256)).replace(' ', '0') + "|";
//	if(i == 6) {
//		line += "\n";
//	}
//}
//System.out.println("[" + dateFormat.format(new Date()) + "] Received the following RTCP packet: " +
//line.substring(0, line.lastIndexOf("|")) + " from " + mPacket.getAddress());
				
				Utils.logToFile(Utils.getCurrentTimestamp() + " " + getBufferType(bufferType) + " control packet received of size " + mPacket.getLength(), "video_rtcp_received.txt");
				if(firstTimeOnly) {
					timer = new Timer(5000, mActionListener);
					timer.start();
					firstTimeOnly = false;
					Utils.LOG(TAG + "Received the first " + getBufferType(bufferType).toLowerCase() + " control packet! (" + mPacket.getLength() + " bytes)", DEBUGGING, LOGGING);
					timestamp = System.currentTimeMillis();
				}
				stillStreaming = true;
				streamingCounter = 3;
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
						uploaderIndex = Global.getUploaderIndex(mUploader);
						for(Viewer v : Global.mUploaders.get(uploaderIndex).mViewers) {
							if(v.play) {
								mPacket.setPort(getPort(bufferType));
								mPacket.setAddress(v.mDestination);

//								mSocket.send(mPacket);
							}
						}
					}
				}
				
				if (count > 336) {
					MBs++;
					DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
					Date date = new Date();
					System.err.println( "[" + dateFormat.format(date) + "] " + TAG + (MBs*336) + " bytes of video RTCP data received from "
							+ mPacket.getAddress());
					count -= 336;
				}
				count += mPacket.getLength();
			}
		} catch (IOException e) {
			Utils.LOG(
					getBufferType(bufferType) + "RtcpHandler: Error while receiving / sending video RTCP packets",
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
