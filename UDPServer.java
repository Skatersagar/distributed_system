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

	    try {
	    bbuff = new byte[512];
	    DGSock = new DatagramSocket(port);
	    packetDock = new DatagramPacket(bbuff, bbuff.length);
	    try {
		DGSock.receive(packetDock);
		DGSock.setSoTimeout(1000);
		previouslyReceived = ProtogramPacket.fromDatagram(packetDock);	    
		filename = ((new String(previouslyReceived.payload)).split(" ")[0]);
		filesize = Long.parseLong((new String(previouslyReceived.payload)).split(" ")[1]);
		dataOut = (OutputStream)(new FileOutputStream(new File(filename)));
	    } catch ( Exception e ) {
		System.out.println("Received bad file descriptor!\nTerminating program...");
		System.exit(-1);
	    }
	}
    }
}