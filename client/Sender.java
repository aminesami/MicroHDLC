import java.net.*;

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
	try {
	    s = new Sender(args[0], Integer.parseInt(args[1]));
	} catch (Exception e) {

	}
    }
}
