package com.onevue.spring.beans.handler;

import org.springframework.context.Lifecycle;

public class Handler implements Runnable {

	private final Lifecycle lifecycle;
	
	private final Thread thread;

	private Handler(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
		this.thread = new Thread(this);
	}
	
	public static Handler startHandler(Lifecycle lifecycle) {
		Handler handler = new Handler(lifecycle);
		handler.startHandler();
		return handler;
	}
	
	public static void stopHandler(Handler handler) {
		handler.stopHandler();
	}

	@Override
	public void run() {
		this.lifecycle.start();
	}

	public void startHandler() {
		this.thread.start();
	}

	public void stopHandler() {
		this.lifecycle.stop();
		this.thread.stop();
	}

	public boolean isRunning() {
		return this.lifecycle.isRunning();
	}
}
