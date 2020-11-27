import java.net.*;

public class Receiver {
    private ServerSocket serverSocket;
    
    public Receiver (int port) {
	try {
	    serverSocket = new ServerSocket(port);
	    Socket socket = serverSocket.accept();
	    serverSocket.close();
	} catch (Exception e) { }
    }

    public static void main (String[] args) {
	Receiver r;
	try {
	    r = new Receiver(Integer.parseInt(args[0]));
	} catch (Exception e) { }
    }
}
