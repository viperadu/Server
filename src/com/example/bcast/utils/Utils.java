package com.example.bcast.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;

import com.example.bcast.Global;

public class Utils {
	public static File file;

	public static void LOG(String message, boolean DEBUGGING, boolean LOGGING) {
		if (DEBUGGING) {
			if (LOGGING) {
				logToFile(message);
			}
			System.out.println(message);
		}
	}

	public static void logToFile(String message) {
		if (file == null) {
			file = new File("log.txt");
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(file, true)));
			out.println(message);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void logToFile(String message, String filename) {
		if (file == null) {
			file = new File(filename);
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(file, true)));
			out.println(message);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getCurrentTimestamp() {
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		return ts.toString().substring(ts.toString().indexOf(" ") + 1);
	}

	public static void printNetworkInterfaces(String TAG) {
		Enumeration e = null;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();
				/*
				 * if(!i.getHostAddress().startsWith("127") &&
				 * !i.getHostAddress().contains("a") &&
				 * !i.getHostAddress().contains("A") &&
				 * !i.getHostAddress().contains("b") &&
				 * !i.getHostAddress().contains("B") &&
				 * !i.getHostAddress().contains("c") &&
				 * !i.getHostAddress().contains("C") &&
				 * !i.getHostAddress().contains("d") &&
				 * !i.getHostAddress().contains("D") &&
				 * !i.getHostAddress().contains("e") &&
				 * !i.getHostAddress().contains("E") &&
				 * !i.getHostAddress().contains("f") &&
				 * !i.getHostAddress().contains("F") &&
				 * Utils.getOccurrence(i.getHostAddress(), ".") == 3) {
				 */
				if (isValidPublicIPAddress(i.getHostAddress())) {
					System.out.println(TAG + i.getHostAddress());
					Global.localIPAddress = i.getHostAddress();
					Global.localAddress = i;
				}
			}
		}
	}

	public static boolean isValidPublicIPAddress(String address) {
		if (getOccurrence(address, ".") != 3) {
			return false;
		}
		String[] bytes = address.split("\\.");
		int[] numbers = new int[4];
		if (bytes.length == 4) {
			if (bytes[0].startsWith("/")) {
				bytes[0] = bytes[0].substring(1);
			}
			int count = 0;
			try {
				for (String s : bytes) {
					numbers[count] = Integer.parseInt(s);
					count++;
				}
			} catch (Exception e) {
				return false;
			}
			if (numbers[0] == 10) {
				return false;
			}
			if (numbers[0] == 127) {
				return false;
			}
			if (numbers[0] == 172 && numbers[1] > 15 && numbers[1] < 33) {
				return false;
			}
			// if(numbers[0] == 192 && numbers[1] == 168) {
			// return false;
			// }
		}
		System.out.println("Address found: " + address);
		return true;
	}

	public static int getOccurrence(final String haystack, final String needle) {
		String s = haystack;
		boolean flag = false;
		int noOfOccurrences = 0;
		do {
			flag = false;
			if (s.contains(needle)) {
				flag = true;
				noOfOccurrences++;
				s = s.substring(s.indexOf(needle) + needle.length());
			}
		} while (flag);
		return noOfOccurrences;
	}

}
