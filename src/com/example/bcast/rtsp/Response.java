package com.example.bcast.rtsp;

import java.io.IOException;
import java.io.OutputStream;

import com.example.bcast.RtspServer;
import com.example.bcast.utils.Utils;

public class Response {
	public static final String TAG = "Response: ";
	public static final boolean DEBUGGING = true;
	public static final boolean LOGGING = true;
	
	public static final String STATUS_OK = "200 OK";
	public static final String STATUS_BAD_REQUEST = "400 Bad Request";
	public static final String STATUS_NOT_FOUND = "404 Not Found";
	public static final String STATUS_INTERNAL_SERVER_ERROR = "500 Internal Server Error";
	
	public String status = STATUS_INTERNAL_SERVER_ERROR;
	public String content = "";
	public String attributes = "";
	
	private Request mRequest;
	
	public Response(Request request) {
		mRequest = request;
	}
	
	public Response() {
		mRequest = null;
	}
	
	public void send(OutputStream output) throws IOException {
		int seqid = -1;
		try {
			seqid = Integer.parseInt(mRequest.headers.get("cseq").replace(" ", ""));
		} catch (Exception e) {
			Utils.LOG(TAG + "Error parsing", DEBUGGING, LOGGING);
		}
		
		String response = "RTSP/1.0 " + status + "\r\n" +
				"Server: " + RtspServer.SERVER_NAME + "\r\n" +
				(seqid>=0?("Cseq: " + seqid + "\r\n"):"") +
				"Content-Length: " + content.length() + "\r\n" +
				attributes +
				"\r\n" + 
				content;
		
		System.out.println("");
		Utils.LOG(TAG + response, DEBUGGING, LOGGING);
		output.write(response.getBytes());
	}
}
