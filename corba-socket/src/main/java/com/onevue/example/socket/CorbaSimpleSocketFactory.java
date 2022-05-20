package com.onevue.example.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.transport.Acceptor;
import com.sun.corba.ee.spi.transport.ORBSocketFactory;

public class CorbaSimpleSocketFactory implements ORBSocketFactory {
	
	private org.omg.CORBA.ORB orb;

	@Override
	public void setORB(ORB orb) {
		this.orb = orb;
	}

	@Override
	public ServerSocket createServerSocket(String type, InetSocketAddress inetSocketAddress) throws IOException {
		System.out.println("createServerSocket: " + type);
		return new ServerSocket(inetSocketAddress.getPort(), 1000, inetSocketAddress.getAddress());
	}

	@Override
	public Socket createSocket(String type, InetSocketAddress inetSocketAddress) throws IOException {
		System.out.println("createSocket: " + type);
		return new Socket(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
	}

	@Override
	public void setAcceptedSocketOptions(Acceptor acceptor, ServerSocket serverSocket, Socket socket)
			throws SocketException {
		System.out.println("setAcceptedSocketOptions");
	}
}