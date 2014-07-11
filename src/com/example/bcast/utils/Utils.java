package com.example.bcast.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.example.bcast.Global;

public class Utils {
	public static void LOG(String message, boolean DEBUGGING, boolean LOGGING) {
		if(DEBUGGING) {
			if(LOGGING) {
				logToFile(message);
			}
			System.out.println(message);
		}
	}
	
	public static void logToFile(String message) {
		
	}
	
	public static void printNetworkInterfaces(String TAG) {
		Enumeration e = null;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while(e.hasMoreElements())
		{
		    NetworkInterface n = (NetworkInterface) e.nextElement();
		    Enumeration ee = n.getInetAddresses();
		    while (ee.hasMoreElements())
		    {
		        InetAddress i = (InetAddress) ee.nextElement();
		        if(!i.getHostAddress().startsWith("127") &&
		        		!i.getHostAddress().contains("a") &&
		        		!i.getHostAddress().contains("A") &&
		        		!i.getHostAddress().contains("b") &&
		        		!i.getHostAddress().contains("B") &&
		        		!i.getHostAddress().contains("c") &&
		        		!i.getHostAddress().contains("C") &&
		        		!i.getHostAddress().contains("d") &&
		        		!i.getHostAddress().contains("D") &&
		        		!i.getHostAddress().contains("e") &&
		        		!i.getHostAddress().contains("E") &&
		        		!i.getHostAddress().contains("f") &&
		        		!i.getHostAddress().contains("F") &&
		        		 Utils.getOccurrence(i.getHostAddress(), ".") == 3) {
		        	System.out.println(TAG + i.getHostAddress());
		        	Global.localIPAddress = i.getHostAddress();
		        }
		    }
		}
	}
	
	public static int getOccurrence(final String haystack, final String needle) {
		String s = haystack;
		boolean flag = false;
		int noOfOccurrences = 0;
		do {
			flag = false;
			if(s.contains(needle)) {
				flag = true;
				noOfOccurrences++;
				s = s.substring(s.indexOf(needle) + needle.length());
			}
		} while(flag);
		return noOfOccurrences;
	}

}
