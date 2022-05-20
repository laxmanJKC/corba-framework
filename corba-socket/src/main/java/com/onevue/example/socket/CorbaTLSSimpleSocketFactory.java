package com.onevue.example.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import com.onevue.spring.beans.handler.Handler;
import com.onevue.spring.tcp.connection.NioSSLClient;
import com.onevue.spring.tcp.connection.NioSSLServer;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.transport.Acceptor;
import com.sun.corba.ee.spi.transport.ORBSocketFactory;

public class CorbaTLSSimpleSocketFactory implements ORBSocketFactory {

	private org.omg.CORBA.ORB orb;
	
	private ServerSocket serverSocket;
	
	private Socket socket;
	
	private boolean connected;

	@Override
	public void setORB(ORB orb) {
		this.orb = orb;
	}

	@Override
	public ServerSocket createServerSocket(String type, InetSocketAddress inetSocketAddress) {
		System.out.println("createServerSocket: " + type);
		try {
			NioSSLServer nioSSLServer = new NioSSLServer("TLSv1.2", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
			this.serverSocket = nioSSLServer.getServerSocket();
			Handler.startHandler(nioSSLServer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return serverSocket;
	}

	@Override
	public Socket createSocket(String type, InetSocketAddress inetSocketAddress) throws IOException {
		System.out.println("createSocket: " + type);
		NioSSLClient nioSSLClient;
		try {
			nioSSLClient = new NioSSLClient("TLSv1.2", inetSocketAddress.getHostName(), inetSocketAddress.getPort());
			this.socket = nioSSLClient.connect();
			SocketChannel socketChannel = nioSSLClient.getSocketChannel();
			SSLEngine engine = nioSSLClient.getEngine();
			engine.beginHandshake();
	    	nioSSLClient.doHandshake(socketChannel, engine);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socket;
	}

	@Override
	public void setAcceptedSocketOptions(Acceptor acceptor, ServerSocket serverSocket, Socket socket)
			throws SocketException {
		System.out.println("setAcceptedSocketOptions: " );
	}

}
