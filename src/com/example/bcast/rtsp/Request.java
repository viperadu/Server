package com.example.bcast.rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.bcast.utils.Utils;

public class Request {
	public static final String TAG = "Request: ";
	public static final boolean DEBUGGING = true;
	public static final boolean LOGGING = true;

	public static final Pattern regexMethod = Pattern.compile(
			"(\\w+) (\\S+) RTSP", Pattern.CASE_INSENSITIVE);
	public static final Pattern regexHeader = Pattern.compile("(\\S+):(.+)",
			Pattern.CASE_INSENSITIVE);
	public String method;
	public String uri;
	public HashMap<String, String> headers = new HashMap<String, String>();

	public static Request parseRequest(BufferedReader input)
			throws IOException, IllegalStateException, SocketException {
		Request request = new Request();
		String line;
		Matcher matcher;

		if ((line = input.readLine()) == null) {
			throw new SocketException("Client disconnected");
		}
		System.out.println("");
		Utils.LOG(TAG + line, DEBUGGING, LOGGING);
		matcher = regexMethod.matcher(line);
		matcher.find();
		request.method = matcher.group(1);
		request.uri = matcher.group(2);
		while ((line = input.readLine()) != null && line.length() > 3) {
			Utils.LOG(TAG + line, DEBUGGING, LOGGING);
			matcher = regexHeader.matcher(line);
			matcher.find();
			request.headers.put(matcher.group(1).toLowerCase(Locale.US),
					matcher.group(2));
		}
		if (line == null) {
			throw new SocketException("Client disconnected");
		}
		// Utils.LOG(TAG + request.method + " " + request.uri, DEBUGGING,
		// LOGGING);
		return request;
	}
}
