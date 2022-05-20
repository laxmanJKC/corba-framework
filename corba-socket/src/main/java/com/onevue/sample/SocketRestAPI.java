package com.onevue.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SocketRestAPI {

	public static void main(String[] args) throws Exception {
		String data = URLEncoder.encode("key1", "UTF-8") + "=" + URLEncoder.encode("value1", "UTF-8");

		SSLContext context = SSLContext.getInstance("TLS");

		KeyStore clientStore = KeyStore.getInstance("pkcs12");
		File file = new File(
				"D:\\DEV\\REVITALIZATION_PROJECT\\work-place\\corba-workspace\\gs-spring-boot-initial\\src\\main\\resources\\keystore.p12");
		FileInputStream inputStream = new FileInputStream(file);
		System.out.println(file.isFile());
		clientStore.load(inputStream, "changeit".toCharArray());
		//inputStream.close();

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(clientStore);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		// clientStore.load(new FileInputStream("src/main/resources/baeldung.cer"),
		// "changeit".toCharArray());

		//KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSASSA-PSS");

		//KeyManagerFactory kmf = KeyManagerFactory.getInstance("PKIX");
		// KeyManagerFactory kmf = KeyManagerFactory.getInstance();
		//kmf.init(clientStore, "changeit".toCharArray());
		//KeyManager[] keyManagerFactory = kmf.getKeyManagers();

		/*
		 * KeyStore trustStore = KeyStore.getInstance("cer"); trustStore.load(new
		 * FileInputStream("baeldung"), "changeit".toCharArray());
		 * 
		 * TrustManagerFactory tmf =
		 * TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		 * tmf.init(trustStore); TrustManager[] tms = tmf.getTrustManagers();
		 */

		//context.init(keyManagerFactory, null, new SecureRandom());
		SSLSocketFactory factory = (SSLSocketFactory) context.getSocketFactory();
		SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 8443);
		socket.setNeedClientAuth(false);
		//socket.setEnabledCipherSuites(new String[] { "ECDHE-RSA-AES256-GCM-SHA384" });
		socket.startHandshake();
//		Socket socket = new Socket("localhost", 8443);

		// NioSSLClient nioSSLClient = new NioSSLClient("TLSv1.2", "localhost", 8443);
		// nioSSLClient.connect();
		// Socket socket = new Socket("127.0.0.1", 8080);
		// SocketChannel channel = nioSSLClient.getSocketChannel();
		// channel.configureBlocking(false);
		// Socket socket = channel.socket();
		String path = "/";
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
		wr.write("GET " + path + " HTTP/1.0\r\n");
		// wr.write("Content-Length: " + data.length() + "\r\n");
		wr.write("Content-Type: application/json\r\n");
		wr.write("\r\n");

		// wr.write(data);
		wr.flush();

		BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			System.out.println(line);
		}
		socket.shutdownInput();
		wr.close();
	}
}
