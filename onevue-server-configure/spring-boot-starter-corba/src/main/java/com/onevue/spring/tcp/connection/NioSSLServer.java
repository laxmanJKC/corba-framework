package com.onevue.spring.tcp.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.SecureRandom;
import java.util.Iterator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

public class NioSSLServer extends NioSSLPeer implements Lifecycle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NioSSLServer.class);

	@Override
	public boolean isRunning() {
		return this.active;
	}

	/**
	 * Declares if the server is active to serve and create new connections.
	 */
	private boolean active;

	/**
	 * The context will be initialized with a specific SSL/TLS protocol and will
	 * then be used to create {@link SSLEngine} classes for each new connection that
	 * arrives to the server.
	 */
	private SSLContext context;

	/**
	 * A part of Java NIO that will be used to serve all connections to the server
	 * in one thread.
	 */
	private Selector selector;
	
	private ServerSocket serverSocket;

	/**
	 * Server is designed to apply an SSL/TLS protocol and listen to an IP address
	 * and port.
	 *
	 * @param protocol    - the SSL/TLS protocol that this server will be configured
	 *                    to apply.
	 * @param hostAddress - the IP address this server will listen to.
	 * @param port        - the port this server will listen to.
	 * @throws Exception
	 */
	public NioSSLServer(String protocol, String hostAddress, int port) throws Exception {

		context = SSLContext.getInstance(protocol);
		context.init(createKeyManagers("./src/main/resources/server.jks", "storepass", "keypass"),
				createTrustManagers("./src/main/resources/trustedCerts.jks", "storepass"), new SecureRandom());

		SSLSession dummySession = context.createSSLEngine().getSession();
		myAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		myNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		peerAppData = ByteBuffer.allocate(dummySession.getApplicationBufferSize());
		peerNetData = ByteBuffer.allocate(dummySession.getPacketBufferSize());
		dummySession.invalidate();

		selector = SelectorProvider.provider().openSelector();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		this.serverSocket = serverSocketChannel.socket();
		this.serverSocket.bind(new InetSocketAddress(hostAddress, port));
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		active = true;

	}

	/**
	 * Should be called in order the server to start listening to new connections.
	 * This method will run in a loop as long as the server is active. In order to
	 * stop the server you should use {@link NioSslServer#stop()} which will set it
	 * to inactive state and also wake up the listener, which may be in blocking
	 * select() state.
	 * @throws Exception 
	 */
	private void startServer() throws Exception {

		LOGGER.debug("Initialized and waiting for new connections...");

		while (isActive()) {
			selector.select();
			Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
			while (selectedKeys.hasNext()) {
				SelectionKey key = selectedKeys.next();
				selectedKeys.remove();
				if (!key.isValid()) {
					continue;
				}
				if (key.isAcceptable()) {
					accept(key);
				} else if (key.isReadable()) {
					read((SocketChannel) key.channel(), (SSLEngine) key.attachment());
				}
			}
		}

		LOGGER.debug("Goodbye!");

	}

	/**
	 * Sets the server to an inactive state, in order to exit the reading loop in
	 * {@link NioSslServer#start()} and also wakes up the selector, which may be in
	 * select() blocking state.
	 */
	public void stop() {
		LOGGER.debug("Will now close server...");
		active = false;
		executor.shutdown();
		selector.wakeup();
	}

	/**
	 * Will be called after a new connection request arrives to the server. Creates
	 * the {@link SocketChannel} that will be used as the network layer link, and
	 * the {@link SSLEngine} that will encrypt and decrypt all the data that will be
	 * exchanged during the session with this specific client.
	 *
	 * @param key - the key dedicated to the {@link ServerSocketChannel} used by the
	 *            server to listen to new connection requests.
	 * @throws Exception
	 */
	private void accept(SelectionKey key) throws Exception {

		LOGGER.debug("New connection request!");

		SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
		socketChannel.configureBlocking(false);

		SSLEngine engine = context.createSSLEngine();
		engine.setUseClientMode(false);
		engine.beginHandshake();

		if (doHandshake(socketChannel, engine)) {
			socketChannel.register(selector, SelectionKey.OP_READ, engine);
		} else {
			socketChannel.close();
			LOGGER.debug("Connection closed due to handshake failure.");
		}
	}

	/**
	 * Will be called by the selector when the specific socket channel has data to
	 * be read. As soon as the server reads these data, it will call
	 * {@link NioSslServer#write(SocketChannel, SSLEngine, String)} to send back a
	 * trivial response.
	 *
	 * @param socketChannel - the transport link used between the two peers.
	 * @param engine        - the engine used for encryption/decryption of the data
	 *                      exchanged between the two peers.
	 * @throws IOException if an I/O error occurs to the socket channel.
	 */
	@Override
	protected void read(SocketChannel socketChannel, SSLEngine engine) throws IOException {

		LOGGER.debug("About to read from a client...");

		peerNetData.clear();
		int bytesRead = socketChannel.read(peerNetData);
		if (bytesRead > 0) {
			peerNetData.flip();
			while (peerNetData.hasRemaining()) {
				peerAppData.clear();
				SSLEngineResult result = engine.unwrap(peerNetData, peerAppData);
				switch (result.getStatus()) {
				case OK:
					peerAppData.flip();
					LOGGER.debug("Incoming message: " + new String(peerAppData.array()));
					break;
				case BUFFER_OVERFLOW:
					peerAppData = enlargeApplicationBuffer(engine, peerAppData);
					break;
				case BUFFER_UNDERFLOW:
					peerNetData = handleBufferUnderflow(engine, peerNetData);
					break;
				case CLOSED:
					LOGGER.debug("Client wants to close connection...");
					closeConnection(socketChannel, engine);
					LOGGER.debug("Goodbye client!");
					return;
				default:
					throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
				}
			}

			write(socketChannel, engine, "Hello! I am your server!");

		} else if (bytesRead < 0) {
			LOGGER.error("Received end of stream. Will try to close connection with client...");
			handleEndOfStream(socketChannel, engine);
			LOGGER.debug("Goodbye client!");
		}
	}

	/**
	 * Will send a message back to a client.
	 *
	 * @param key     - the key dedicated to the socket channel that will be used to
	 *                write to the client.
	 * @param message - the message to be sent.
	 * @throws IOException if an I/O error occurs to the socket channel.
	 */
	@Override
	protected void write(SocketChannel socketChannel, SSLEngine engine, String message) throws IOException {

		LOGGER.debug("About to write to a client...");

		myAppData.clear();
		myAppData.put(message.getBytes());
		myAppData.flip();
		while (myAppData.hasRemaining()) {
			// The loop has a meaning for (outgoing) messages larger than 16KB.
			// Every wrap call will remove 16KB from the original message and send it to the
			// remote peer.
			myNetData.clear();
			SSLEngineResult result = engine.wrap(myAppData, myNetData);
			switch (result.getStatus()) {
			case OK:
				myNetData.flip();
				while (myNetData.hasRemaining()) {
					socketChannel.write(myNetData);
				}
				LOGGER.debug("Message sent to the client: " + message);
				break;
			case BUFFER_OVERFLOW:
				myNetData = enlargePacketBuffer(engine, myNetData);
				break;
			case BUFFER_UNDERFLOW:
				throw new SSLException("Buffer underflow occured after a wrap. I don't think we should ever get here.");
			case CLOSED:
				closeConnection(socketChannel, engine);
				return;
			default:
				throw new IllegalStateException("Invalid SSL status: " + result.getStatus());
			}
		}
	}

	/**
	 * Determines if the the server is active or not.
	 *
	 * @return if the server is active or not.
	 */
	private boolean isActive() {
		return active;
	}

	@Override
	public void start() {
		try {
			startServer();
		} catch (Exception e) {
			LOGGER.error("Unable to start NioSSLServer", e);
			e.printStackTrace();
		}
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}
}
