package com.example.bcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.example.bcast.utils.Utils;

public class JsonServer extends Thread {

	private static final String TAG = "JsonServer: ";
	private static final boolean DEBUGGING = true;
	private static final boolean LOGGING = true;
	private ServerSocket mSocket;
	private final String header = "HTTP/1.1 200 OK\r\n" + 
							"Content-Type: text/xml; charset=utf-8\r\n" +
							"Content-Length: ";
	
	public JsonServer(int port) throws IOException {
		try {
			mSocket = new ServerSocket(port);
			start();
		} catch (BindException e) {
			Utils.LOG(TAG + "Port already in use", DEBUGGING, LOGGING);
			System.err.println("Port already in use");
			throw e;
		}
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try {
				new HandlerThread(mSocket.accept()).start();
				System.out.println("Client asked for the available videos");
			} catch (SocketException e) {
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class HandlerThread extends Thread {

		private Socket mSocket;
		private BufferedReader mInput;
		private OutputStream mOutput;
		
		public HandlerThread(Socket accept) throws IOException {
			mSocket = accept;
			mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			mOutput = mSocket.getOutputStream();
		}

		@Override
		public void run() {
			String line = "";
			if(!Thread.interrupted()) {
				try {
					while((line = mInput.readLine()) != "") {
						System.out.println(line);
						if(line.length() < 5) {
							break;
						}
					}
					JSONArray array = getJSONArray();
					mOutput.write((header + array.toJSONString().length() + "\r\n\r\n").getBytes());
					mOutput.write(array.toJSONString().getBytes());
					
					System.out.println(header + array.toJSONString().length() + "\r\n\r\n" + array.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		private JSONArray getJSONArray() {
			JSONArray array = new JSONArray();
			ResultSet result = Global.conn.executeQuery("SELECT * FROM `sessions`");
			try {
				while(result.next()) {
					JSONObject obj = new JSONObject();
					String title = (String) result.getObject("name");
					obj.put("title", title);
					obj.put("author", "");
					obj.put("image", "");
					obj.put("url", Global.RTSP + Global.localIPAddress + ":" + "8086" + "/" + title);
					array.add(obj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				Global.conn.close();
			}
			Global.conn.close();
			return array;
		}
	}
}
