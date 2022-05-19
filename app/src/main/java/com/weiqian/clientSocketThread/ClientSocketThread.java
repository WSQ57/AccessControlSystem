package com.weiqian.clientSocketThread;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class ClientSocketThread extends Thread {
	private Socket socket = null;
	private static ClientSocketThread clientSocket = null;
	private MessageListener listener;
	private final int buffer_size = 64;
	public static Boolean isConnected;
	/** get client socket thread instance */
	public static ClientSocketThread getClientSocket(String ip, int port) {
		if (clientSocket == null) {
			clientSocket = new ClientSocketThread(ip, port);
			clientSocket.start();
		}
		return clientSocket;
	}
	/** constructor */
	private ClientSocketThread(String ip, int port) {
		try {
			isConnected = true;
			socket = new Socket(ip, port);
		} catch (IOException e) {
			isConnected = false;
			e.printStackTrace();
		}
	}
	/** set a listener to report message received */
	public void setListener(MessageListener listener) {
		this.listener = listener;
	}
	
	public void release()
	{
		this.interrupt();
		isConnected = false;
		clientSocket = null;
		socket = null;
	}

	@Override
	public void run() {
		int len = buffer_size;
		while (!interrupted() && isConnected) {
			byte[] buffer = new byte[buffer_size];
			try {
				len = this.getInputStream().read(buffer, 0, buffer_size);
				FrameFilter(buffer, len);
				sleep(10);
			} catch (InterruptedException e) {
				isConnected = false;
				clientSocket = null;
			} catch (Exception e) {
				isConnected = false;
				clientSocket = null;
			}
		}
	}
	/**
	 * Frame filter
	 * 
	 * @param buffer
	 *            frame data
	 * @param len
	 *            frame lenth
	 * egg: FE E0 0B 55 72 00 6E 99 E6 AF 0A 
	 */
	public void FrameFilter(byte[] buffer, int len) {
		int index = 0, frmlen = 0;
		byte ch;
		byte status = 0;
		byte[] sensordata = null;
		while ((len--) > 0) {
			ch = buffer[index++];
			switch (status) {
			case 0:
				if (ch == (byte) 0xFE)
					status = 1;
				break;
			case 1:
				if ((byte)(ch & 0xE0) == (byte) 0xE0)
					status = 2;
				else
					status = 0;
				break;
			case 2:
				frmlen = ch;
				if (frmlen < buffer_size) {
					frmlen -= 6;
					index++;
					index++;
					sensordata = new byte[frmlen];
					System.arraycopy(buffer, index, sensordata, 0, frmlen);
					index = index + frmlen;
					status = 3;
				} else
					status = 0;
				break;
			case 3:
				if(this.listener != null)
					this.listener.Message(sensordata, frmlen);
				status = 0;
				break;
			}
		}
	}

	/**
	 * get inputstream from the ClientSocketThread
	 * 
	 * @return InputStream
	 * @throws Exception 
	 */
	private InputStream getInputStream() throws Exception {
			return socket.getInputStream();

	}

	/**
	 * get outputstream from ClientSocketThread
	 * 
	 * @return OutputStream
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws Exception {
			return socket.getOutputStream();
	}
}
