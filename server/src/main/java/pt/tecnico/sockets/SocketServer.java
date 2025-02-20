package pt.tecnico.sockets;

import java.io.*;
import java.net.*;

import org.w3c.dom.css.Counter;

public class SocketServer {

	public static void main(String[] args) throws IOException {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", SocketServer.class.getName());
			return;
		}

		// Convert port from String to int
		final int port = Integer.parseInt(args[0]);

		Counter counter = new Counter();
		(new Thread(new CounterRunnable(counter))).start();

		// Create server socket
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.printf("Server accepting connections on port %d %n", port);

		// wait for and then accept client connection
		// a socket is created to handle the created connection
		Socket clientSocket = serverSocket.accept();
		final String clientAddress = clientSocket.getInetAddress().getHostAddress();
		final int clientPort = clientSocket.getPort();
		System.out.printf("Connected to client %s on port %d %n", clientAddress, clientPort);

		// Create buffered stream to receive data from client, one line at a time
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

		// Receive data until client closes the connection
		String response;

		//exercicio 4
		DataOutputStream out1 = new DataOutputStream(clientSocket.getOutputStream());

		while (true) {
			// Read a line of text.
			// A line ends with a line feed ('\n').
			response = in.readLine();
			if (response == null) {
				break;
			}
			counter.increment();
			System.out.printf("Received message with content: '%s'%n", response);
			
			out1.writeBytes("recebi memsagem do cliente");
		}

		// Close connection to current client
		clientSocket.close();
		System.out.println("Closed connection with client");

		// Close server socket
		serverSocket.close();
		System.out.println("Closed server socket");
	}

}

class Counter {
	private int count = 0;

	public synchronized int get() {
		return count;
	}

	public synchronized void increment() {
		count++;
		if (count % 3 == 0) {
			notify();
		}
	}

	public synchronized void waitForMultiple() {
		try {
			if (count % 3 == 0) {
				System.out.println(count);
			}
			wait();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}

class CounterRunnable implements Runnable {
	private final Counter counter;

	public CounterRunnable(Counter counter) {
		this.counter = counter;
	}

	public void run() {
		while (true) {
			counter.waitForMultiple();
		}
	}
}
