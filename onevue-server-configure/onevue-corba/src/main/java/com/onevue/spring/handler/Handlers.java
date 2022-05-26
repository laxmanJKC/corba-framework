package com.onevue.spring.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.Lifecycle;

public class Handlers implements Runnable {

	private final Lifecycle lifecycle;

	private final Thread thread;
	
	private static Handlers corbaHandlers;
	
	private List<Handlers> handlers = new ArrayList<Handlers>();
	
	private Handlers(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
		this.thread = new Thread(this);
	}

	public static Handlers addHandler(Lifecycle lifecycle) {
		return new Handlers(lifecycle);
	}
	
	public void start() {
		CompletableFuture<Handlers> completableFutures = null;
		for (Handlers handler: corbaHandlers.handlers) {
			if (completableFutures == null ) {
				completableFutures = CompletableFuture.completedFuture(handler);
			} else {
				completableFutures.complete(handler);
			}
		}
		
	}

	public static void stopHandler(Handlers handler) {
		handler.stopHandler();
	}

	@Override
	public void run() {
		this.lifecycle.start();
		//this.start();
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