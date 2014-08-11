package com.example.bcast.handlers;

import java.net.DatagramPacket;
import java.util.ArrayList;

public class AudioSample {
	public ArrayList<DatagramPacket> mSamples;
	
	public AudioSample() {
		mSamples = new ArrayList<DatagramPacket>();
	}

	public static AudioSample duplicate(AudioSample buffer) {
		AudioSample aSample = new AudioSample();
		aSample.mSamples = new ArrayList<DatagramPacket>();
		for(DatagramPacket p : buffer.mSamples) {
			aSample.mSamples.add(p);
		}
		return aSample;
	}
}
