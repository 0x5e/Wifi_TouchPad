package com.huhu.remotecontrol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class UDP {

	static DatagramSocket socket;
	static InetAddress serverAddress;
	static int port = 8888;
	static UDPLooperThread mThread = new UDPLooperThread();

	public static class UDPLooperThread extends Thread {

		public void run() {
			Looper.prepare();
			if (mHandler == null)
				mHandler = new MyHandler();
			Looper.loop();
		}

		Handler mHandler;

		static class MyHandler extends Handler {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					UDP_Send((String) msg.obj);
					break;
				}
			}
		}

	}
/*
	public static void SetHost(String Host) {
		try {
			serverAddress = InetAddress.getByName(Host);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	public static String Login(String msg) {
		try {
			if (socket == null) {
				socket = new DatagramSocket();
				mThread.start();
			}
			serverAddress = InetAddress.getByName("255.255.255.255");
			DatagramPacket sendpacket = new DatagramPacket(
					msg.getBytes("utf-8"), msg.length(), serverAddress, port);
			socket.setSoTimeout(1000);
			socket.send(sendpacket);
			byte[] recvbuf = new byte[128];
			DatagramPacket recvpacket = new DatagramPacket(recvbuf,
					recvbuf.length);
			socket.receive(recvpacket);
			String ret =  new String(recvpacket.getData()).trim();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void Send(String msg) {
		mThread.mHandler.obtainMessage(0, msg).sendToTarget();
	}

	public static boolean UDP_Send(String msg) {
		if (socket == null || serverAddress == null) {
			return false;
		}
		try {
			byte[] sendbuf = msg.getBytes("UTF-8");
			DatagramPacket sendpacket = new DatagramPacket(sendbuf,
					sendbuf.length, serverAddress, port);
			socket.setSoTimeout(50);
			socket.send(sendpacket);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
/*
	public static String UDP_Receive() {
		if (socket == null) {
			return "";
		}
		try {
			byte[] recvbuf = new byte[1024];
			DatagramPacket recvpacket = new DatagramPacket(recvbuf,
					recvbuf.length);
			socket.receive(recvpacket);
			return new String(recvbuf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
*/
}
