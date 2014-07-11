package com.example.bcast.rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.bcast.Global;
import com.example.bcast.Uploader;
import com.example.bcast.Viewer;
import com.example.bcast.session.Session;
import com.example.bcast.utils.Utils;

public class WorkerThread extends Thread implements Runnable {
	// rtsp server care ofera stream-urile spre vizualizare
	public static final String TAG = "Server: ";
	public static final boolean DEBUGGING = true;
	public static final boolean LOGGING = true;

	// private ServerSocket localSocket = null;
	private Socket mClientSocket = null;
	private BufferedReader mInput = null;
	private OutputStream mOutput = null;
	
	private Viewer mViewer = null;
	private Uploader mUploader = null;

	public WorkerThread(Socket clientSocket) throws IOException {
		mClientSocket = clientSocket;
		mInput = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		mOutput = clientSocket.getOutputStream();
		if(Global.mUploaders.size() == 0) {
			System.err.println("Connection request but no available streams!");
		}
		
		Utils.LOG(TAG + " Connection from " + mClientSocket.getInetAddress() + ":" + mClientSocket.getPort(), DEBUGGING, LOGGING);
	}

	@Override
	public void run() {
		Request request;
		Response response;

		while (!Thread.interrupted()) {
			request = null;
			response = null;
			try {
				request = Request.parseRequest(mInput);
			} catch (SocketException e) {
				break;
			} catch (Exception e) {
				response = new Response();
				response.status = Response.STATUS_BAD_REQUEST;
			}
			if (request != null) {
				try {
					mViewer = new Viewer(mClientSocket.getInetAddress());
					response = processRequest(request);
				} catch (Exception e) {
					Utils.LOG(TAG + "An error occurred", DEBUGGING, LOGGING);
					if (DEBUGGING) {
						e.printStackTrace();
					}
					response = new Response(request);
				}
			}

			try {
				response.send(mOutput);
			} catch (IOException e) {
				Utils.LOG(TAG + "Response was not sent out properly",
						DEBUGGING, LOGGING);
				break;
			}
		}

		try {
			mClientSocket.close();
		} catch (IOException ignore) {
			Utils.LOG("Client disconnected!", DEBUGGING, LOGGING);
		}
	}

	public Response processRequest(Request request)
			throws IllegalStateException, IOException {
		Response response = new Response(request);
		
		if(mUploader == null) {
			for(Uploader u : Global.mUploaders) {
				if(u.mName.equals(request.uri.substring(request.uri.lastIndexOf("/") + 1))) {
					mUploader = u;
					break;
				}
			}
		}
		
		if(mViewer == null) {
			mViewer = new Viewer(mClientSocket.getInetAddress());
		}

		// DESCRIBE method
		if (request.method.equalsIgnoreCase("DESCRIBE")) {
			
			
			
//			mSession = handleRequest(request.uri, mClientSocket);
//			Global.mPairs.get(0).setFirst(handleRequest(request.uri, mClientSocket, Global.mPairs.get(0).getFirst()));
//			RtspServer.mSessions.put(mSession, null);
//			RtspServer.mSessions.put(Global.mPairs.get(0).getFirst(), null);
			

//			String requestContent = Global.mPairs.get(0).getFirst().getSessionDescription();
			String requestContent = mUploader.mSession.getSessionDescription();
			
			//TODO: delete this
			System.err.println(requestContent);
			
			String requestAttributes = "Content-Base: "
					+ mClientSocket.getLocalAddress().getHostAddress() + ":"
					+ mClientSocket.getLocalPort() + "/\r\n"
					+ "Content-Type: application/sdp\r\n";

			response.attributes = requestAttributes;
			response.content = requestContent;

			response.status = Response.STATUS_OK;
			// OPTIONS method
		} else if (request.method.equalsIgnoreCase("OPTIONS")) {
			// TODO: delete the next line
//			response.status = Response.STATUS_OK;
			response.attributes = "Public: DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE\r\n";
			response.status = Response.STATUS_OK;
			// SETUP method
		} else if (request.method.equalsIgnoreCase("SETUP")) {
			Pattern p;
			Matcher m;
			int p1, p2, ssrc, trackId, src[];
			InetAddress destination;

			p = Pattern.compile("trackID=(\\w+)", Pattern.CASE_INSENSITIVE);
			m = p.matcher(request.uri);

			if (!m.find()) {
				response.status = Response.STATUS_BAD_REQUEST;
				return response;
			}

			trackId = Integer.parseInt(m.group(1));

			//TODO: decomment this
			if (!mUploader.mSession.trackExists(trackId)) {
				System.err.println("trackID = " + trackId + " does not exist apparently...");
				response.status = Response.STATUS_NOT_FOUND;
				return response;
			}

			p = Pattern.compile("client_port=(\\d+)-(\\d+)",
					Pattern.CASE_INSENSITIVE);
			m = p.matcher(request.headers.get("transport"));

			if (!m.find()) {
//				int[] ports = mSession.getTrack(trackId).getDestinationPorts();
//				int[] ports = Global.mPairs.get(0).getFirst().getTrack(trackId).getDestinationPorts();
				int[] ports = mUploader.mSession.getTrack(trackId).getDestinationPorts();
				p1 = ports[0];
				p2 = ports[1];
			} else {
				p1 = Integer.parseInt(m.group(1));
				Utils.LOG(TAG + "p1 = " + p1, DEBUGGING, LOGGING);
				p2 = Integer.parseInt(m.group(2));
				Utils.LOG(TAG + "p2 = " + p2, DEBUGGING, LOGGING);
				mViewer.mDestinationPorts[trackId * 2] = p1;
				mViewer.mDestinationPorts[trackId * 2 + 1] = p2;
			}
			
			// TODO: delete this if statement
			if(trackId == 0) {
				ssrc = mUploader.mSSRC;
			} else {
				ssrc = mUploader.mSSRC + 1;
			}
//			src = mSession.getTrack(trackId).getLocalPorts();
//			src = new int[] {mClientSocket.getLocalPort(), mClientSocket.getLocalPort() + 1};
			
			if(trackId == 0) {
//				src = new int[] {Global.audioSendingPort, Global.audioSendingPort + 1};
				src = new int[] {mUploader.mServer.getPorts()[0], mUploader.mServer.getPorts()[1]};
			} else {
//				src = new int[] {Global.videoSendingPort, Global.videoSendingPort + 1};
				src = new int[] {mUploader.mServer.getPorts()[2], mUploader.mServer.getPorts()[3]};
			}
			
//			destination = Global.mPairs.get(0).getFirst().getDestination();
			destination = mClientSocket.getInetAddress();
			
//			mSession.getTrack(trackId).setDestinationPorts(p1, p2);


//			mSession.start(trackId);
			
			
			//TODO: start the streaming to the current client
			//  rtsp://192.168.0.3:8086
			
//			Server.startStreaming(destination);
//			for(Processer proc : PacketProcesser.getProcessers()) {
//				//TODO: change with the processer which handles the stream the user wants
//				if(true) {
//					if(!proc.contains(/*Global.mPairs.get(0).getFirst().getDestination()*/destination)) {
////						Utils.LOG(TAG + "Added " + /*Global.mPairs.get(0).getFirst().getDestination()*/destination.getCanonicalHostName() + " to the destinations list", DEBUGGING, LOGGING);
//						System.err.println(TAG + "Added " + destination.getCanonicalHostName() + " to the destinations list");
//						proc.addDestination(/*Global.mPairs.get(0).getFirst().getDestination()*/destination);
//					}
//				}
//			}
			
			
			
			
			
			
			
			response.attributes = "Transport: RTP/AVP/UDP;"
					+ (destination.isMulticastAddress() ? "multicast"
							: "unicast") + ";destination="
//					+ Global.mPairs.get(0).getFirst().getDestination().getHostAddress()
							
					// TODO: IS THIS CORRECT?
					+ mClientSocket.getInetAddress()
					
					+ ";client_port=" + p1 + "-" + p2 + ";server_port="
					+ src[0] + "-" + src[1] + ";ssrc="
//					+ "8086-8087" + ";"
					+ Integer.toHexString(ssrc) + ";mode=play\r\n"
					+ "Session: " + "1185d20035702ca" + "\r\n"
					+ "Cache-Control: no-cache\r\n";
			response.status = Response.STATUS_OK;
			// PLAY method
		} else if (request.method.equalsIgnoreCase("PLAY")) {
			
			String requestAttributes = "RTP-Info: ";
//				if (Global.mPairs.get(0).getFirst().trackExists(0)) {
				if(mUploader.mSession.trackExists(0)) {
					requestAttributes += "url=rtsp://"
							+ mClientSocket.getLocalAddress().getHostAddress() + ":"
							//TODO: fix this. make it dynamic.
							+ mClientSocket.getLocalPort() + "/trackID=" + 0
							+ ";seq=0,";
				}
//				if (Global.mPairs.get(0).getFirst().trackExists(1)) {
				if(mUploader.mSession.trackExists(1)) {
					requestAttributes += "url=rtsp://"
							+ mClientSocket.getLocalAddress().getHostAddress() + ":"
							+ mClientSocket.getLocalPort() + "/trackID=" + 1
							+ ";seq=0,";
				}
				requestAttributes = requestAttributes.substring(0,
						requestAttributes.length() - 1)
						+ "\r\nSession: 1185d20035702ca\r\n";
				response.attributes = requestAttributes;
				response.status = Response.STATUS_OK;
				if(mViewer != null && mViewer.mDestinationPorts.length == 4 && mViewer.mDestination != null) {
					mUploader.addViewer(mViewer);
					Utils.LOG(TAG + " User " + mClientSocket.getInetAddress() + " added to the receivers list of " + mUploader.mName, DEBUGGING, LOGGING);
				}
			// PAUSE method
		} else if (request.method.equalsIgnoreCase("PAUSE")) {
			response.status = Response.STATUS_OK;
			// TEARDOWN method
		} else if (request.method.equalsIgnoreCase("TEARDOWN")) {
			response.status = Response.STATUS_OK;
			mUploader.removeViewer(mViewer);
		} else {
			Utils.LOG("Command unknown: " + request, DEBUGGING, LOGGING);
			response.status = Response.STATUS_BAD_REQUEST;
		}
		return response;
	}

	protected Session handleRequest(String uri, Socket client)
			throws IllegalStateException, IOException {
		Session session = new Session();
		// TODO: add the session parameters, parsed from the existing stream.
		session.setOrigin(client.getLocalAddress());
		if(session.getDestination() == null) {
			session.setDestination(client.getInetAddress());
		}
		return session;
	}
	
	protected Session handleRequest(String uri, Socket client, Session mSession) 
			throws IllegalStateException, IOException {
		if(mSession != null) {
			mSession.setOrigin(client.getLocalAddress());
			mSession.setDestination(client.getInetAddress());
		}
		return mSession;
	}

	/*
	static class UriParser {
		public final static String TAG = "UriParser: ";

		public static Session parse(String uri) throws IllegalStateException,
				IOException {
			SessionBuilder builder = SessionBuilder.getInstance().clone();

			List<NameValuePair> params = URLEncodedUtils.parse(URI.create(uri),
					"UTF-8");
			if (params.size() > 0) {

				builder.setAudioEncoder(AUDIO_NONE).setVideoEncoder(VIDEO_NONE);

				// Those parameters must be parsed first or else they won't
				// necessarily be taken into account
				for (Iterator<NameValuePair> it = params.iterator(); it
						.hasNext();) {
					NameValuePair param = it.next();

					// FLASH ON/OFF
					if (param.getName().equalsIgnoreCase("flash")) {
						if (param.getValue().equalsIgnoreCase("on"))
							builder.setFlashEnabled(true);
						else
							builder.setFlashEnabled(false);
					}

					// CAMERA -> the client can choose between the front facing
					// camera and the back facing camera
					else if (param.getName().equalsIgnoreCase("camera")) {
						if (param.getValue().equalsIgnoreCase("back"))
							builder.setCamera(CameraInfo.CAMERA_FACING_BACK);
						else if (param.getValue().equalsIgnoreCase("front"))
							builder.setCamera(CameraInfo.CAMERA_FACING_FRONT);
					}

					// MULTICAST -> the stream will be sent to a multicast group
					// The default mutlicast address is 228.5.6.7, but the
					// client can specify another
					else if (param.getName().equalsIgnoreCase("multicast")) {
						if (param.getValue() != null) {
							try {
								InetAddress addr = InetAddress.getByName(param
										.getValue());
								if (!addr.isMulticastAddress()) {
									throw new IllegalStateException(
											"Invalid multicast address !");
								}
								builder.setDestination(addr);
							} catch (UnknownHostException e) {
								throw new IllegalStateException(
										"Invalid multicast address !");
							}
						} else {
							// Default multicast address
							builder.setDestination(InetAddress
									.getByName("228.5.6.7"));
						}
					}

					// UNICAST -> the client can use this to specify where he
					// wants the stream to be sent
					else if (param.getName().equalsIgnoreCase("unicast")) {
						if (param.getValue() != null) {
							try {
								InetAddress addr = InetAddress.getByName(param
										.getValue());
								builder.setDestination(addr);
							} catch (UnknownHostException e) {
								throw new IllegalStateException(
										"Invalid destination address !");
							}
						}
					}

					// TTL -> the client can modify the time to live of packets
					// By default ttl=64
					else if (param.getName().equalsIgnoreCase("ttl")) {
						if (param.getValue() != null) {
							try {
								int ttl = Integer.parseInt(param.getValue());
								if (ttl < 0)
									throw new IllegalStateException();
								builder.setTimeToLive(ttl);
							} catch (Exception e) {
								throw new IllegalStateException(
										"The TTL must be a positive integer !");
							}
						}
					}

					// H.264
					else if (param.getName().equalsIgnoreCase("h264")) {
						VideoQuality quality = VideoQuality.parseQuality(param
								.getValue());
						builder.setVideoQuality(quality).setVideoEncoder(
								VIDEO_H264);
					}

					// H.263
					else if (param.getName().equalsIgnoreCase("h263")) {
						VideoQuality quality = VideoQuality.parseQuality(param
								.getValue());
						builder.setVideoQuality(quality).setVideoEncoder(
								VIDEO_H263);
					}

					// AMR
					else if (param.getName().equalsIgnoreCase("amrnb")
							|| param.getName().equalsIgnoreCase("amr")) {
						AudioQuality quality = AudioQuality.parseQuality(param
								.getValue());
						builder.setAudioQuality(quality).setAudioEncoder(
								AUDIO_AMRNB);
					}

					// AAC
					else if (param.getName().equalsIgnoreCase("aac")) {
						AudioQuality quality = AudioQuality.parseQuality(param
								.getValue());
						builder.setAudioQuality(quality).setAudioEncoder(
								AUDIO_AAC);
					}

				}

			}

			if (builder.getVideoEncoder() == VIDEO_NONE
					&& builder.getAudioEncoder() == AUDIO_NONE) {
				SessionBuilder b = SessionBuilder.getInstance();
				builder.setVideoEncoder(b.getVideoEncoder());
				builder.setAudioEncoder(b.getAudioEncoder());
			}

			return builder.build();

		}
	}
*/
}
