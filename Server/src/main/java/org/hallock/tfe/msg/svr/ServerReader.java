package org.hallock.tfe.msg.svr;

import org.hallock.tfe.msg.MessageReader;
import org.hallock.tfe.serve.Game;
import org.hallock.tfe.serve.GameServer;
import org.hallock.tfe.serve.Lobby;
import org.hallock.tfe.serve.PlayerConnection;

public class ServerReader extends MessageReader
{
	GameServer server;
	PlayerConnection connection;
	
	public ServerReader(GameServer server, PlayerConnection connection)
	{
		this.server = server;
		this.connection = connection;
	}

	public void playerWaiting()
	{
		removeAllTypes();
		
		add(new CreateLobbyHandler  (server, connection));
		add(new JoinLobbyHandler    (server, connection));
		add(new ListLobbiesHandler  (server, connection));
		add(new SetPlayerInfoHandler(server, connection));
	}
	
	public void joinedLobby(Lobby lobby)
	{
		removeAllTypes();
		
		add(new LaunchHandler       (lobby, connection));
		add(new ReadyHandler        (lobby, connection));
		add(new RefreshHandler      (lobby, connection));
		add(new UpdateOptionsHandler(lobby, connection));
		add(new UpdatePlayerHandler (lobby, connection));
	}
	
	public void joinedGame(Game game)
	{
		removeAllTypes();
		
		add(new PlayerActionHandler  (game, connection));
		add(new PlayEvilActionHandler(game, connection));
	}
}
