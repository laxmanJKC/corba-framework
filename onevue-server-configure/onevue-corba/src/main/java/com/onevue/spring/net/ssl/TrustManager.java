package com.onevue.spring.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class TrustManager {

	private final X509TrustManager tm;

	public X509Certificate[] chain;

	TrustManager(X509TrustManager tm) {
		this.tm = tm;
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		throw new UnsupportedOperationException();
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		this.chain = chain;
		tm.checkServerTrusted(chain, authType);
	}
}
