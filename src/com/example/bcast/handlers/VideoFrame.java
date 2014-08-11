package com.example.bcast.handlers;

import java.net.DatagramPacket;
import java.util.ArrayList;

public class VideoFrame {
	public ArrayList<DatagramPacket> mPackets;
	public int NALULength;
	public long mTimestamp;
	public int sequenceNumber;
	public int firstSequenceNumber;
	public boolean keyFrame;
	
	public VideoFrame() {
		mPackets = new ArrayList<DatagramPacket>();
		NALULength = 0;
		mTimestamp = 0;
		sequenceNumber = 0;
		keyFrame = false;
	}
	
	public VideoFrame(VideoFrame mTempVideoFrame) {
		this.NALULength = mTempVideoFrame.NALULength;
		this.sequenceNumber = mTempVideoFrame.sequenceNumber;
		this.mTimestamp = mTempVideoFrame.mTimestamp;
		this.keyFrame = mTempVideoFrame.keyFrame;
		this.mPackets = new ArrayList<DatagramPacket>(mTempVideoFrame.mPackets.size());
		for(DatagramPacket p : mTempVideoFrame.mPackets) {
			this.mPackets.add(new DatagramPacket(p.getData(), p.getLength()));
		}
	}

	public static VideoFrame copyVideoFrame(VideoFrame frame) {
		VideoFrame f = new VideoFrame();
		f.NALULength = frame.NALULength;
		f.sequenceNumber = frame.sequenceNumber;
		f.mTimestamp = frame.mTimestamp;
		f.keyFrame = frame.keyFrame;
		f.mPackets = new ArrayList<DatagramPacket>(frame.mPackets);
		for(DatagramPacket p : frame.mPackets) {
			f.mPackets.add(new DatagramPacket(p.getData(), p.getLength()));
		}
		return f;
	}
	
	public void addPacket(DatagramPacket packet) {
		/*sequenceNumber = getSequenceNumber(packet);
		if(mPackets.size() == 0) {
			firstSequenceNumber = sequenceNumber;
			mPackets.add(packet);
		} else if (getSequenceNumber(mPackets.get(mPackets.size() - 1)) <= sequenceNumber) {
			mPackets.add(packet);
		} else if(getSequenceNumber(mPackets.get(0)) >= sequenceNumber) {
			mPackets.add(0, packet);
		} else {
			for(int i=0; i<mPackets.size() - 1; i++) {
				if(getSequenceNumber(mPackets.get(i)) >= sequenceNumber &&
						getSequenceNumber(mPackets.get(i + 1)) <= sequenceNumber) {
					mPackets.add(i, packet);
					break;
				}
			}
		}*/
		mPackets.add(packet);
	}
	
	private int getSequenceNumber(DatagramPacket packet) {
		/*byte[] seqNo = new byte[] {
				packet.getData()[2],
				packet.getData()[3]
		};*/
//System.out.println("VideoBuffer: Sequence number = " + ((seqNo[0] & 0xFF ) << 8 | (seqNo[1] & 0xFF)));  
		return (packet.getData()[2] & 0xFF ) << 8 | (packet.getData()[3] & 0xFF);
	}

	public static VideoFrame[] duplicate(VideoFrame[] mVideoFrames) {
		VideoFrame[] temp = new VideoFrame[mVideoFrames.length];
		for(int i=0; i<mVideoFrames.length; i++) {
			temp[i] = new VideoFrame(mVideoFrames[i]);
		}
		return temp;
	}
}
