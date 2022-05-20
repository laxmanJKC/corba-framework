package com.onevue.spring.enums;

public enum TLSVersion {

	TLS_1("1.0", "TLSv1"), TLS_1_1("1.1", "TLSv1.1"), TLS_1_2("1.2", "TLSv1.2");

	private final String version;

	private final String tlsVersion;

	private TLSVersion(String version, String tlsVersion) {
		this.version = version;
		this.tlsVersion = tlsVersion;
	}

	public String getVersion() {
		return version;
	}

	public String getTlsVersion() {
		return tlsVersion;
	}
}
