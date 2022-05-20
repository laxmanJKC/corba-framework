package com.onevue.sample;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLClientMain {

	public static void main(String[] argv) throws Exception {
		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		/*SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(8080);
		String[] suites = serverSocket.getSupportedCipherSuites();
		for (int i = 0; i < suites.length; i++) {
			System.out.println(suites[i]);
		}
		serverSocket.setEnabledCipherSuites(suites);
		String[] protocols = serverSocket.getSupportedProtocols();
		for (int i = 0; i < protocols.length; i++) {
			System.out.println(protocols[i]);
		}
		SSLSocket socket = (SSLSocket) serverSocket.accept();
		socket.startHandshake();
		*/
		Socket socket = new Socket("localhost", 8443);
		InputStream in = socket.getInputStream();
	    OutputStream wr = socket.getOutputStream();
	    wr.write("GET / HTTP/1.0\r\n".getBytes());
		//wr.write("Content-Length: " + data.length() + "\r\n");
		wr.write("Content-Type: application/json\r\n".getBytes());
		wr.write("\r\n".getBytes());

		//wr.write(data);
		wr.flush();
	    int ch ;
	    while((ch = in.read()) > 0 ) {
	    	System.out.println((char)ch);
	    }
		System.out.println(socket.getRemoteSocketAddress());
	}
}
