package org.hallock.tfe.serve;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ExperimentClient
{
	public static void main(String[] text) throws UnknownHostException, IOException
	{
		String hostName = "localhost";
		int  portNumber = 8808;
		// open a socket
		// open an output stream
		// write a string
		try(Socket socket = new Socket(hostName, portNumber);
				Scanner s = new Scanner(System.in);
				PrintWriter out = new PrintWriter(socket.getOutputStream());)
		{
			while(s.hasNextLine()){
				out.println("From Client: " + s.nextLine());
				out.flush();
			}
		}
	}
}
