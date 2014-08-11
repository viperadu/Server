package com.example.bcast.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.bcast.Global;
import com.example.bcast.Viewer;

public class AudioBuffer extends AbstractBuffer {
	private final String TAG = "AudioBuffer: ";
	private Queue<AudioSample> mSamplesQueue;
	private long lastTimestamp;
	private boolean firstTime;
	private int samplingRate;
	private AudioSample buffer;

	public AudioBuffer(DatagramSocket socket, int samplingRate, int bufferLength) {
		super(socket, bufferLength);
		this.mSamplesQueue = new ConcurrentLinkedQueue<AudioSample>();
		this.samplingRate = samplingRate;
		this.firstTime = true;
		this.buffer = new AudioSample();
	}
	
	@Override
	public void addPacket(DatagramPacket mPacket) {
//String line = "";
//for(int i=0; i<16; i++) {
//	line += String.format("%8s", Integer.toBinaryString((mPacket.getData()[i] + 256) % 256)).replace(' ', '0') + "|";
//}
//System.out.println("[" + dateFormat.format(new Date()) + "] " + TAG + line.substring(0, line.lastIndexOf("|")));

		if(firstTime) {
			lastTimestamp = getTimestamp(mPacket);
			firstTime = false;
		}
		if(Math.abs(getTimestamp(mPacket) - lastTimestamp) >= samplingRate) {
			lastTimestamp = getTimestamp(mPacket);
			if(mSamplesQueue.size() >= mBufferLength) {
				mSamplesQueue.poll();	
			}
			mSamplesQueue.offer(AudioSample.duplicate(buffer));
			buffer = new AudioSample();
			buffer.mSamples.add(new DatagramPacket(mPacket.getData(), mPacket.getLength()));
		} else {
			buffer.mSamples.add(new DatagramPacket(mPacket.getData(), mPacket.getLength()));
		}
	}

	@Override
	public void sendPacket(ArrayList<Viewer> mViewers) {
		if(mSamplesQueue.size() > 0 && mViewers.size() > 0) {
			AudioSample as = mSamplesQueue.poll();
			for(Viewer v : mViewers) {
				for(DatagramPacket p : as.mSamples) {
					p.setAddress(v.mDestination);
					p.setPort(Global.audioSendingPort);
					try {
						mSocket.send(p);
					} catch (IOException e) {
						System.err.println("Something went wrong when trying to send audio packets");
					}
				}
			}
		}
	}

}
