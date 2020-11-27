package server;

import java.net.*;

public class Receiver {
    private ServerSocket serverSocket;
    
    public Receiver (int port) {
	try {
	    serverSocket = new ServerSocket(port);
	    Socket socket = serverSocket.accept();
	    serverSocket.close();
	} catch (Exception e) {
	    
	}
    }

    public static void main (String[] args) {
	Receiver r;
	try {
	    if (args.length != 1) {
		System.err.println("usage: Receiver <port>");
		System.exit(1);
	    }
	    int port = Integer.parseInt(args[0]);
	    r = new Receiver(port);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
