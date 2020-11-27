package client;

import java.net.*;
import java.nio.file.*;
import static java.nio.file.Files.readAllBytes;

import common.*;

public class Sender {
    private Socket socket;
    
    public Sender (String hostname, int port, byte[] data) {
	try {
	    socket = new Socket(hostname, port);

	    Frame[] frames = FrameBuilder.splitFrames(data);
	    
	    System.out.writeBytes(data);
	    
	    socket.close();
	} catch (Exception  e) {
	    e.printStackTrace();
	}
    }
    
    public static void main (String[] args) {
	Sender s;
	try {
	    if (args.length != 4) {
		System.err.println("usage: Sender <hostname> <port> <file> <0>");
		System.exit(1);
	    }
	    String hostname = args[0];
	    int port = Integer.parseInt(args[1]);
	    byte[] data = readAllBytes(Paths.get(args[2]));
	    int n = Integer.parseInt(args[3]);
	    s = new Sender(args[0], port, data);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
