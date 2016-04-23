import java.net.*;
import java.io.*;

public class UDPClient {

    public static void main (String[] args) {
	DatagramSocket DGSock = null;
	InetAddress address = null;
	ProtogramPacket previouslySent = new ProtogramPacket();
	ProtogramPacket previouslyReceived = new ProtogramPacket();
	DatagramPacket packetDock = null;
	FileInputStream fileIn;
	File fileToSend = null;
	String filename = null;
	byte[] bbuf;
	byte[] bytes;
	long filesize = 0;
	int port = 0;
	int bytesRead = 0;
	int bookmark = 0;
	final int MAX_SIZE = 480;

	//usage checking
	try {
	    address = InetAddress.getByName(args[0]);
	    port = Integer.parseInt(args[1]);
	    filename = args[2];
	    fileToSend = new File(filename);
	    filesize = fileToSend.length();
	} catch ( Exception e ) {
	    System.out.println("Usage: java UDPClient [server name] [port] [filename]");
	    System.exit(-1);
	}

	//setup
	try {
	    bbuf = new byte[MAX_SIZE+32];
	    fileIn = new FileInputStream(fileToSend);	
	    DGSock = new DatagramSocket();
	    DGSock.setSoTimeout(1000);
	    packetDock = new DatagramPacket(bbuf, bbuf.length);

	    //send file descriptor to host
	    bytes = new byte[filename.length() + Long.toString(filesize).length() + 1];
	    int i;
	    for ( i=0; i<filename.length(); i++ )		
		bytes[i] = (byte)filename.charAt(i);
	    bytes[i] = ' '; i++;
	    for ( int j=0; j<Long.toString(filesize).length(); j++ ) {
		bytes[i] = (byte)(Long.toString(filesize).charAt(j));
		i++;
	    }
	    previouslySent = new ProtogramPacket(-(bytes.length), 0, bytes, address, port);
	    
	    //guarantee delivery
	    while (true) {	       
		try {
		    DGSock.send(previouslySent.asDatagram());
		    DGSock.receive(packetDock);
		    break;
		} catch ( SocketTimeoutException e ) {
		} catch ( Exception e ) {
		    e.printStackTrace();
		    System.exit(-1);
		}
	    }		
					   
	    //traffic loop
	    while ( previouslyReceived.end < filesize ) {
		
		//create next packet to send		
		bytes = new byte[MAX_SIZE];
		bytesRead = fileIn.read(bytes);
		previouslySent = new ProtogramPacket(bookmark, bookmark+bytesRead, bytes, address, port);

		//guarantee delivery
		while (true) {
		    try {
			DGSock.send(previouslySent.asDatagram());
			DGSock.receive(packetDock);
			previouslyReceived = ProtogramPacket.fromDatagram(packetDock);
			if ( previouslyReceived.end < previouslySent.end ) continue;
			break;
		    } catch ( SocketTimeoutException e ) {
		    } catch ( Exception e ) {
			e.printStackTrace();
		    }
		}
		if ( (bookmark < filesize) && (bytesRead > 0) ) bookmark += bytesRead;
		    
	    }
	} catch ( FileNotFoundException e ) {
	    System.out.println("Target file not found. Terminating program.");
	    System.exit(-1);
	} catch ( Exception e ) {
	    e.printStackTrace();
	}

	//FIN cycle
	previouslySent = new ProtogramPacket(-1, -1, address, port);
	for ( int i=0; i<20; i++ ) {
	    try {
		DGSock.send(previouslySent.asDatagram());
		DGSock.receive(packetDock);
		previouslyReceived = ProtogramPacket.fromDatagram(packetDock);
		if ( (previouslySent.end == -1) && (previouslyReceived.end != -1) ) {
		    i = 0;
		    continue;
		}
		break;
	    } catch ( SocketTimeoutException e ) {
		if ( i == 0 ) System.out.println("Client hung. Will terminate momentarily.");
	    } catch ( Exception e ) {
		e.printStackTrace();
	    }
	}
	
	System.out.println("Client process terminated.");

    }
}