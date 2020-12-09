package server;

import java.io.*;
import java.net.*;
import java.util.*;

import client.*;
import common.*;

public class Receiver {
    private ServerSocket serverSocket;
    
    public Receiver (int port) {
	try {
	    serverSocket = new ServerSocket(port);
	    Socket socket = serverSocket.accept();

            // should not close the streams
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            ArrayList<Frame> frames = new ArrayList<Frame>();
            
            Frame frame;
            // wait for connection request frame
            while ((frame = Decoder.getFrame(in)).getType() != Frame.Type.C)
                ;
            
            boolean shouldAnswer = false;
            int frameCount = 0;
            while ((frame = Decoder.getFrame(in)).getType() != Frame.Type.F) {
                System.out.println(frame.getType() + " " + frame.getNum());
                if (frame.getType() == Frame.Type.I) {
                    if (frame.getNum() == (frameCount + 1) % (FrameBuilder.MAX_NUM_FRAMES + 1)) {
                        frames.add(frame);
                        if (shouldAnswer) {
                            Frame response = new Frame(Frame.Type.A, frameCount, null);
                            response.send(out);
                        }
                        shouldAnswer = !shouldAnswer;
                        frameCount = (frameCount + 1) % (FrameBuilder.MAX_NUM_FRAMES + 1);
                    } else {
                        Frame response = new Frame(Frame.Type.R, frameCount, null);
                        response.send(out);
                    }
                }
            }
            
            serverSocket.close();
	} catch (Exception e) {
            e.printStackTrace();
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
