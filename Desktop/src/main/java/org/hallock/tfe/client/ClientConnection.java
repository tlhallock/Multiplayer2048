package org.hallock.tfe.client;

import java.awt.Color;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.hallock.tfe.cmn.game.PossiblePlayerActions;
import org.hallock.tfe.cmn.sys.Constants;
import org.hallock.tfe.cmn.util.Connection;
import org.hallock.tfe.cmn.util.Json;
import org.hallock.tfe.cmn.util.Utils;
import org.hallock.tfe.dsktp.gui.TileView;
import org.hallock.tfe.msg.GCClientMessage;
import org.hallock.tfe.msg.GSPlayerAction;
import org.hallock.tfe.msg.Message;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public class ClientConnection
{
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
			JsonGenerator generator = Json.createOpenedGenerator(socket.getOutputStream());
			JsonParser parser = Json.createParser(socket.getInputStream());)
		{
			Connection connection = new Connection(socket, generator, parser);
			connection.readOpen();
			
			ClientKeyListener listener = new ClientKeyListener(connection);
			other.addKeyListener(listener);
			other.setFocusable(true);
			other.requestFocus();
			
			GameClient client = new GameClient();
			client.add(myself);
			client.add(other);
			
			connection.sendMessageAndFlush(new GSPlayerAction(PossiblePlayerActions.ShowAllTileBoards));

			Message message;
			while ((message = connection.readMessage()) != null)
			{
				if (!(message instanceof GCClientMessage))
				{
					System.out.println("Ignoring " + message);
					continue;
				}
				GCClientMessage msg = (GCClientMessage) message;
				msg.perform(client);
			}
		}
	}
}
