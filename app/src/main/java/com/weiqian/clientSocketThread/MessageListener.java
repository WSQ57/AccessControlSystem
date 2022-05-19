package com.weiqian.clientSocketThread;


/** message listener */
public interface MessageListener {
	public void Message(byte[] message, int message_len);
}
