package com.shopping.server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.shopping.service.UserServiceImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class UserServer {

	private static final Logger logger = Logger.getLogger(UserServer.class.getName());

	private Server server;

	public void startServer() {
		// Will ensure that userServiceImpl Class is running on a specified host.

		int port = 50051;
		try {
			server = ServerBuilder.forPort(port)
					.addService(new UserServiceImpl())
					.build()
					.start();
			logger.info("Server start ho gya he");
			/** this below code is required in case you kill the jvm, then who will shut the server properly? This code is for that shutdown in case
			 * someone decides to kill the jvm
			 */
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					logger.info("Clean server shutdown");
					try {
					UserServer.this.stopServer();
					}
					catch(Exception e) {
						logger.log(Level.SEVERE, "Server shutdown interrupted",e);
					}
				}
			});
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Server did not start", e);
		}

	}

	public void stopServer() {
		if(server != null) {
			try {
				server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
				
			
			} catch (InterruptedException e) {
				
				logger.log(Level.SEVERE, "Server did not stop",e);
			}
		}
	}

	public void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination(); // ensures that it will keep waiting for termination
		}
	}

	public static void main(String args[]) throws InterruptedException {
		UserServer userServer = new UserServer();
		userServer.startServer();
		userServer.blockUntilShutdown();
		/**
		 * We need this method as main thread will be killed after execution. We need
		 * this method to not kill the server as well ie to keep the server running
		 */

	}
}
