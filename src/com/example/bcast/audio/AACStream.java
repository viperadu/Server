package com.example.bcast.audio;

import java.io.IOException;
import java.io.Serializable;

public class AACStream extends AudioStream implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int mActualSamplingRate;
	private int mProfile, mSamplingRateIndex, mChannel, mConfig;
	private int mEncodingMode;
	
	public AACStream() throws IOException {
		super();
	}
//	
//	private static final String[] AUDIO_OBJECT_TYPES = { "NULL", // 0
//			"AAC Main",
//			"AAC LC (Low Complexity)",
//			"AAC SSR (Scalable Sample Rate)",
//			"AAC LTP (Long Term Prediction)"
//	};

	public static final int[] AUDIO_SAMPLING_RATES = { 96000, // 0
			88200, // 1
			64000, // 2
			48000, // 3
			44100, // 4
			32000, // 5
			24000, // 6
			22050, // 7
			16000, // 8
			12000, // 9
			11025, // 10
			8000, // 11
			7350, // 12
			-1, // 13
			-1, // 14
			-1, // 15
	};

	public String generateSessionDescription() throws IllegalStateException,
			IOException {
		if (mEncodingMode == 0) {

			return "m=audio "
					+ String.valueOf(getDestinationPorts()[0])
					+ " RTP/AVP 96\r\n"
					+ "a=rtpmap:96 mpeg4-generic/"
					+ mActualSamplingRate
					+ "\r\n"
					+ "a=fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config="
					+ Integer.toHexString(mConfig)
					+ "; SizeLength=13; IndexLength=3; IndexDeltaLength=3;\r\n";

		} else {

			for (int i = 0; i < AUDIO_SAMPLING_RATES.length; i++) {
				if (AUDIO_SAMPLING_RATES[i] == mQuality.samplingRate) {
					mSamplingRateIndex = i;
					break;
				}
			}
			mProfile = 2; // AAC LC
			mChannel = 1;
			mConfig = mProfile << 11 | mSamplingRateIndex << 7 | mChannel << 3;

			return "m=audio "
					+ String.valueOf(getDestinationPorts()[0])
					+ " RTP/AVP 96\r\n"
					+ "a=rtpmap:96 mpeg4-generic/"
					+ mQuality.samplingRate
					+ "\r\n"
					+ "a=fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config="
					+ Integer.toHexString(mConfig)
					+ "; SizeLength=13; IndexLength=3; IndexDeltaLength=3;\r\n";
		}
	}

}
