import java.net.*;
import java.io.*;

public class UDPServer {    
    public static void main (String[] args) {
    	DatagramSocket DGSock = null;
		OutputStream dataOut = null;
		ProtogramPacket previouslySent = new ProtogramPacket();;
		ProtogramPacket previouslyReceived = new ProtogramPacket();
		DatagramPacket packetDock = null;
		byte[] bbuff = null;
		String filename = "";
		int port = 0;
		long filesize = 0;

		try { port = Integer.parseInt(args[0]); } catch ( Exception e ) {
	    System.out.println("Usage: java UDPServer [port]");
	    System.exit(-1);
	}
    }
}