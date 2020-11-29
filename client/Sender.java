package client;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import static java.nio.file.Files.readAllBytes;

public class Sender {
    private Socket socket;
    
    public Sender (String hostname, int port, byte[] data) {
	try {
	    socket = new Socket(hostname, port);

            // should not close the streams
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            
	    Frame[] frames = FrameBuilder.splitFrames(data);

            int i = 0;
            while (i < frames.length) {
                for (int j = 0; j < FrameBuilder.MAX_NUM_FRAMES && i + j < frames.length; j++) {
                    frames[i + j].send(out);
                }
                // wait for RR or REJ
            }
	    
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
