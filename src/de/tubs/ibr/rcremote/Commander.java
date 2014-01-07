package de.tubs.ibr.rcremote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

public class Commander extends Thread {
	
	private final String TAG = "Commander";
	
	private int frequency = 10;
	private String host = null;
	private int port = 4242;
	private Boolean aborted = false;
	
	private Object datalock = new Object();
	private int x1 = 0;
	private int y1 = 0;
	private int x2 = 0;
	private int y2 = 0;
	
	public Commander(String host, Integer port, Integer frequency) {
		this.host = host;
		this.port = port;
		this.frequency = frequency;
	}
	
	@Override
	public void run() {
		try {
			int pause = 100;
			
			if (frequency != 0) {
				pause = 1000 / Math.abs(frequency);
			}
			
			// setup socket
			DatagramSocket socket = new DatagramSocket();
		
			try {
				while (!this.aborted) {
					// transmit state
					transmit(socket);
					
					synchronized(this) {
						this.wait(pause, 0);
					}
				}
			} catch (InterruptedException e) {
				Log.e(TAG, "interrupted", e);
			}
			
			socket.close();
		} catch (SocketException e) {
			Log.e(TAG, "cannot setup socket", e);
		}
	}
	
	private synchronized void abort() {
		aborted = true;
		this.notifyAll();
	}
	
	public void set(int index, int x, int y) {
		synchronized(datalock) {
			if (index == 0) {
				this.x1 = x;
				this.y1 = y;
			} else {
				this.x2 = x;
				this.y2 = y;
			}
		}
	}
	
	private void transmit(DatagramSocket sock) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
			synchronized(datalock) {
				OutputStreamWriter writer = new OutputStreamWriter(stream);
				writer.append("x1=" + String.valueOf(this.x1) + "\n");
				writer.append("y1=" + String.valueOf(this.y1) + "\n");
				writer.append("x2=" + String.valueOf(this.x2) + "\n");
				writer.append("y2=" + String.valueOf(this.y2) + "\n");
				writer.flush();
			}
			
			stream.flush();
			
			DatagramPacket pack = new DatagramPacket(stream.toByteArray(), stream.size(), InetAddress.getByName(this.host), this.port);
			
			// send out packet
			sock.send(pack);
		} catch (UnknownHostException e) {
			Log.e(TAG, "cannot reach destination", e);
			abort();
		} catch (IOException e) {
			Log.e(TAG, "cannot send datagram", e);
			abort();
		}
	}

	public void terminate() throws InterruptedException {
		abort();
		this.join();
	}
}
