package com.onevue.sample;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

public class SSLServerMain {

	public static void main(String[] argv) throws Exception {
		int port = 443;
		ServerSocketFactory ssocketFactory = SSLServerSocketFactory.getDefault();
		ServerSocket ssocket = ssocketFactory.createServerSocket(port);

		Socket socket = ssocket.accept();

		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		// Read from in and write to out...

		in.close();
		out.close();
	}
}
