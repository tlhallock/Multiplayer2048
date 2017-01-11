package org.hallock.tfe.serve;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ExperimentServer
{
	public static void main(String[] text) throws IOException
	{
		try (ServerSocket serverSocket = new ServerSocket(8808);)
		{
			while (true)
			{
				

				System.out.println("waiting for a connection");
				
				
				try (Socket accept = serverSocket.accept();
					InputStream output = accept.getInputStream();)
				{

					System.out.println("waiting for input");
					
					
					Scanner s = new Scanner(output);// read a string
					while(s.hasNextLine()){
						System.out.println(s.nextLine());
					}
					System.out.println("END");
				}
			}
		}
	}
}
