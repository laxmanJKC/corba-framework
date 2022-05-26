package com.onevue.corba.sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.onevue.spring.net.ssl.SocketTrustManager;

public class CorbaSocketClient {

	public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, CertificateException,
			IOException, KeyManagementException {
		SSLContext context = SSLContext.getInstance("TLS");

		KeyStore clientStore = KeyStore.getInstance("pkcs12");
		File file = new File("src/main/resources/keystore.p12");
		FileInputStream inputStream = new FileInputStream(file);
		System.out.println(file.isFile());
		clientStore.load(inputStream, "changeit".toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(clientStore);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
		SocketTrustManager tm = new SocketTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);

		SSLSocketFactory factory = (SSLSocketFactory) context.getSocketFactory();
		SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 8443);
		socket.setNeedClientAuth(false);

		socket.startHandshake();

		String path = "/sayHello";
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
