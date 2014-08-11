package com.example.bcast.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.bcast.Global;
import com.example.bcast.Viewer;

public class VideoBuffer extends AbstractBuffer {
	/**
	 * A two-dimensional array used for storing video frames.
	 * Each row of the matrix corresponds to a second of video, 
	 * and it contains {@code mFPS} {@link VideoFrame}s. For 
	 * example, a 10 second buffer will have 10 rows, and if the 
	 * video has 15 frames per second, it will have 15 columns.
	 */
	private volatile VideoFrame[][] mFrames;		// MAYBE A CONCURRENT LINKED QUEUE for easier access (adding / removing)?
	private Queue<VideoFrame[]> mFramesQueue;
	private VideoFrame[] mVideoFrames;

	/**
	 * The number of frames per second of the video stream.
	 */
	private int mFPS;
	
	/**
	 * A temporary video frame used to create a complete video 
	 * frame from multiple {@code DatagramPacket}s.
	 */
	private VideoFrame mTempVideoFrame;
	/**
	 * A counter to help introducing new datagram packets into
	 * the {@link VideoFrame} structure.
	 */
	private int mCount;
	/**
	 * A secondary counter that helps with introducing new frames 
	 * intro the {@link VideoFrame} structure. 
	 */
	private volatile int mSecondsCount;
	/**
	 * The timestamp will help mark packets appropriately so that 
	 * the receivers can play the video continuously.
	 */
	private long mTimestamp;
	/**
	 * This timestamp will help mark frames within a second. We use 
	 * this in case we have a frame rate of 15 (i.e. the result of the
	 * division of 1000 (ms in a second) with the frame rate is not 
	 * an integer), then the {@code mTimestamp} will be slightly out of 
	 * sync with the video.
	 */
	private long mTempTimestamp;
//	/**
//	 * A simple variable that makes sure a block of code is executed 
//	 * only the first time.
//	 */
//	private boolean firstTime = true;
	private DateFormat dateFormat;
	private int secondNALULength;
	
	/**
	 * Constructor for the buffering mechanism.
	 * @param socket The socket from which the datagram packet 
	 * will be sent.
	 * @param fps The number of frames per second of the video 
	 * stream.
	 * @param bufferLength The length of the buffer in seconds.
	 */
	public VideoBuffer(DatagramSocket socket, int fps, int bufferLength) {
		super(socket, bufferLength);
		mFPS = fps;
//		mSocket = socket;
//		mBufferLength = bufferLength;
		mCount = 0;
		mSecondsCount = 0;
		mTempTimestamp = mTimestamp = 0;
		secondNALULength = 0;
		mTempVideoFrame = new VideoFrame();
		
		mFramesQueue = new ConcurrentLinkedQueue<VideoFrame[]>();
		mVideoFrames = new VideoFrame[mFPS];
		for(int i=0; i<mFPS; i++) {
			mVideoFrames[i] = new VideoFrame();
		}
		dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	}
	
	/**
	 * @param packet
	 */
	public void addPacket(DatagramPacket packet) {
		
//String line = "";
//for(int i=4; i<8; i++) {
//	line += String.format("%8s", Integer.toBinaryString((packet.getData()[i] + 256) % 256)).replace(' ', '0') + "|";
//}
//System.out.println("[" + dateFormat.format(new Date()) + "] " + "VideoBuffer: " + line.substring(0, line.lastIndexOf("|")) + " " + getTimestamp(packet));
		
		// TODO: check order of packets
		mTempVideoFrame.addPacket(new DatagramPacket(packet.getData(), packet.getLength()));
		mTempVideoFrame.NALULength += packet.getLength();// - 14;
		// End of the video frame, next packet will contain
		// information for a new frame.
		if((packet.getData()[13] & 0x40) == 0x40) {
			if((packet.getData()[13] & 0x04) == 0x04) {
				mTempVideoFrame.keyFrame = true;
			}
			secondNALULength += mTempVideoFrame.NALULength;
			mVideoFrames[mCount++] = new VideoFrame(mTempVideoFrame);
			
//			String line = "";
//			for(int i=0; i<mTempVideoFrame.mPackets.size(); i++) {
//				line += mTempVideoFrame.mPackets.get(i).getLength() + " ";
//			}
//			System.out.println("[" + mTempVideoFrame.NALULength + "]" + line);
			
			
			mTempTimestamp += 1000 / mFPS;
			// We filled a complete second worth of video frames.
			if (mCount >= mFPS) {
System.err.println("[" + dateFormat.format(new Date()) + "] VideoBuffer.addPacket(): " + "We filled a complete second of video data (" + secondNALULength + " bytes)");
				secondNALULength = 0;
				mCount = 0;
				if(mFramesQueue.size() >= mBufferLength) {
					mFramesQueue.poll();
System.err.println("[" + dateFormat.format(new Date()) + "] VideoBuffer.addPacket(): " + "Queue was filled with 10 seconds of data, removing one element so that mFramesQueue.size()=" + mFramesQueue.size());
				}

				mFramesQueue.offer(VideoFrame.duplicate(mVideoFrames));
				mTimestamp += 1000;
				mTempTimestamp = mTimestamp;
				mVideoFrames = new VideoFrame[mFPS];
				for(int i=0; i<mFPS; i++) {
					mVideoFrames[i] = new VideoFrame();
				}
			}
			mTempVideoFrame = new VideoFrame();
		}
	}
	
	/**
	 * @param packet
	 * @return
	 */
	private DatagramPacket updateTimestamp(DatagramPacket packet) {
System.out.println("\t\t\tVideoBuffer: mTempTimestamp=" + mTempTimestamp);
		byte[] newTimestamp = new byte[] {
				(byte) (mTempTimestamp >> 24),
		        (byte) (mTempTimestamp >> 16),
		        (byte) (mTempTimestamp >> 8),
		        (byte) mTempTimestamp};
		byte[] packetData = packet.getData();
		for(int i=0; i<4; i++) {
			packetData[i+4] = newTimestamp[i];
		}
		packet.setData(packetData);

		// TODO: delete this if everything is right
//String line = "";
//for(int i=0; i<8; i++) {
//	line += String.format("%8s", Integer.toBinaryString((packet.getData()[i] + 256) % 256)).replace(' ', '0') + " | ";
//}
//System.out.println("VideoBuffer: " + " " + line.substring(0, line.lastIndexOf(" | ")) + ", should be " + mTempTimestamp);
		return packet;
	}

	/**
	 * @param addr
	 */
	public VideoFrame[] sendFrameByFrame(ArrayList<Viewer> viewers, int count) {
		VideoFrame frameToSend = null;
		if(count == mFPS - 1) {
			frameToSend = mFramesQueue.poll()[count];
		} else {
			frameToSend = mFramesQueue.peek()[count];
		}
		String line = "";
		for(int i=0; i<frameToSend.mPackets.size(); i++) {
			line += frameToSend.mPackets.get(i).getLength() + " ";
		}
		System.out.println("[" + dateFormat.format(new Date()) + "] [" + count + "] " + line);
		for(DatagramPacket p : frameToSend.mPackets) {
			DatagramPacket temp = new DatagramPacket(p.getData(), p.getLength());
			try {
				temp.setAddress(InetAddress.getByName("192.168.0.3"));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			temp.setPort(Global.videoSendingPort);
			try {
				mSocket.send(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void sendPacket(ArrayList<Viewer> viewers) {
		VideoFrame[] framesToSend = mFramesQueue.poll();
		if(framesToSend != null && viewers.size() > 0) {
			for(int j=0; j<framesToSend.length; j++) {
				for(int i=0; i<viewers.size(); i++) {
					InetAddress destination = viewers.get(i).mDestination;
					for(int k=0; k<framesToSend[j].mPackets.size(); k++) {
						DatagramPacket p = framesToSend[j].mPackets.get(k);
						p.setAddress(destination);
						p.setPort(Global.videoSendingPort);
						try {
							mSocket.send(p);
						} catch(IOException ignore) {
							System.err.println("sendFrame(): Something wrong with sending the video packets");
						}
					}
					/*System.out.println("[" + dateFormat.format(new Date()) + "] Sent a video frame to " +
							destination + ":" + Global.videoSendingPort + " from " + mSocket.getLocalAddress() + 
							":" + mSocket.getLocalPort() + ", having " + framesToSend[j].NALULength + " bytes (in " + 
							framesToSend[j].mPackets.size() + " packets).");*/
				}
			}
		}
	}
}
