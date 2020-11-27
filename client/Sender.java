package client;

import java.net.*;
import java.nio.file.*;
import static java.nio.file.Files.readAllBytes;

public class Sender {
    private Socket socket;
    
    public Sender (String hostname, int port) {
	try {
	    socket = new Socket(hostname, port);

	    System.out.println("socket opened");
	    
	    socket.close();
	} catch (Exception  e) {

	}
    }
    
    public static void main (String[] args) {
	Sender s;
	byte[] data;
	try {
	    data = readAllBytes(Paths.get(args[2]));
	    /*
	    for (byte b : data) {
		leftPad(Integer.toBinaryString(b), 8);
	    }
	    */
	    s = new Sender(args[0], Integer.parseInt(args[1]));
	} catch (Exception e) {

	}
    }
}
