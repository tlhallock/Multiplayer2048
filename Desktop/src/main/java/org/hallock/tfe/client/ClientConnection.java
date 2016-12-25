package org.hallock.tfe.client;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.dsktp.gui.TileView;
import org.hallock.tfe.msg.ClientMessage;
import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.PlayerAction;

public class ClientConnection implements Runnable
{
	private PrintWriter output;
	private Scanner input;

	public ClientConnection(Scanner scanner, PrintWriter writer)
	{
		this.input = scanner;
		this.output = writer;
	}

	@Override
	public void run()
	{
		
	}
	
	public void sendMessage(PlayerAction playerAction)
	{
		playerAction.write(output);
		output.flush();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void launch() throws UnknownHostException, IOException
	{
		TileView myself = new TileView();
		TileView other  = new TileView();
		
		JFrame frame = new JFrame();
		frame.setBounds(50, 50, 500, 500);
		frame.setTitle("Viewer");

		// This will be the list of available evil actions...
		JPanel left = new JPanel();
		left.setBackground(Color.black);
		
		JSplitPane inner = new JSplitPane();
		inner.setLeftComponent(other);
		inner.setRightComponent(myself);
		inner.setDividerLocation(200);

		JSplitPane outer = new JSplitPane();
		outer.setLeftComponent(left);
		outer.setRightComponent(inner);
		
		Utils.attach(frame.getContentPane(), outer);
		
		frame.setVisible(true);
		
		try (Socket socket = new Socket("localhost", Constants.TEMP_PORT);
			Scanner scanner = new Scanner(socket.getInputStream());
			PrintWriter writer = new PrintWriter(socket.getOutputStream());)
		{
			ClientConnection connection = new ClientConnection(scanner, writer);
			
			ClientKeyListener listener = new ClientKeyListener(connection);
			other.addKeyListener(listener);
			other.setFocusable(true);
			other.requestFocus();
			
			
			GameClient client = new GameClient();
			client.add(myself);
			client.add(other);
			
			connection.sendMessage(new PlayerAction(PossiblePlayerActions.ShowAllTileBoards));
			
			while (scanner.hasNext())
			{
				Message parse = Message.parse(scanner);
				if (!(parse instanceof ClientMessage))
				{
					System.out.println("Ignoring " + parse);
					continue;
				}
				ClientMessage msg = (ClientMessage) parse;
				msg.perform(client);
			}
		}
	}
}
