package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.ArrayList;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.msg.gc.LaunchGame;
import org.hallock.tfe.msg.ls.UpdatePlayer.UpdateAction;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;


public class Lobby
{
	String lobbyName;
	GameOptions options;
	PlayerConnection host;
	String id;

	ArrayList<PlayerPlaceHolder> players = new ArrayList<>();

	private GameServer server;
	
	public Lobby(GameServer server)
	{
		this.server = server;
	}

	public void setNumPlayers(int numPlayers, boolean b) throws IOException
	{
		// Add enough
		while (players.size() < numPlayers)
		{
			players.add(new PlayerPlaceHolder());
		}
		pushUp();
		// Remove extra
		while (players.size() > numPlayers)
		{
			PlayerPlaceHolder playerPlaceHolder = players.get(players.size() - 1);
			players.remove(players.size() - 1);
			playerPlaceHolder.kick();
		}

		if (b)
			broadcastChanges();
	}

	private boolean pushUp() throws IOException
	{
		boolean changed = false;
		for (int i=0;i<players.size();i++)
		{
			if (!players.get(i).isWaitingForPlayer())
			{
				continue;
			}
			for (int j=i+1;j<players.size();j++)
			{
				if (!players.get(i).isConnected())
				{
					continue;
				}
				players.get(i).connected = players.get(j).connected;
				players.get(i).connected.lobbyNumber = i;
				players.get(j).connected = null;
				changed = true;
			}
		}
		return changed;
	}

	public void setPlayerSpec(int player, PlayerSpec spec, boolean b) throws IOException
	{
		if (players.get(player).isConnected() && spec.equals(PlayerSpec.Computer))
		{
			boolean ableToMove = false;
			for (int i = 0; i < players.size() && !ableToMove; i++)
			{
				if (i == player || !players.get(i).isWaitingForPlayer())
					continue;
				players.get(i).connected = players.get(player).connected;
				players.get(i).connected.lobbyNumber = i;
				players.get(player).connected = null;
				ableToMove = true;
			}
			if (!ableToMove)
			{
				players.get(player).kick();
			}
			
		}
		players.set(player, new PlayerPlaceHolder(spec));
		if (b)
			broadcastChanges();
	}
        
        
        
        
        
        
        
        
	public boolean addPlayer(PlayerConnection player, boolean b) throws IOException
	{
		if (!needsPlayers())
			return false;

		boolean ableToAdd = false;
		for (int i = 0; i < players.size() && !ableToAdd; i++)
		{
			if (!players.get(i).isWaitingForPlayer())
				continue;
			players.get(i).connected = player;
			players.get(i).connected.lobbyNumber = i;
			ableToAdd = true;
		}

		if (b)
			broadcastChanges();
		return true;
	}
        
        
        
        
        
        
        
        
        
        
        public boolean needsPlayers()
        {
        	for (PlayerPlaceHolder p : players)
        		if (p.isWaitingForPlayer())
        			return true;
        	return false;
        }
        
        public boolean allAreReady()
        {
        	for (PlayerPlaceHolder p : players)
        		if (!p.isReady())
        			return false;
        	return true;
        }

	public int countDesiredHumanPlayers()
	{
		int count = 0;
		for (PlayerPlaceHolder p : players)
			if (p.isWaitingForPlayer())
				count++;
		return count;
	}
        
        
        
        
	public LobbyInfo getInfo()
	{
		LobbyInfo info = new LobbyInfo();
		info.id = id;
		info.options = new GameOptions(options);
		info.players = new PlayerInfo[players.size()];
		info.allReady = allAreReady();
		
		for (int i = 0; i < players.size(); i++)
		{
			info.players[i] = new PlayerInfo(players.get(i), i);
		}

		return info;
	}

	/*
	 * This should be combined with the InGamePlayer somehow...
	 */
	public static final class PlayerPlaceHolder
	{
		public PlayerSpec spec;
		public PlayerConnection connected = null;
		// hack to be here
		public int playerNumber = -1;
		
		public PlayerPlaceHolder(PlayerSpec spec2)
		{
			this.spec = spec2;
		}

		public boolean isReady()
		{
			switch (spec)
			{
			case HumanPlayer:
				return connected != null && connected.ready;
			case Computer:
				return true;
			default:
				throw new RuntimeException("Not implemented.");
			}
		}

		public PlayerPlaceHolder()
		{
			this(PlayerSpec.HumanPlayer);
		}

		public boolean isWaitingForPlayer()
		{
			return spec.equals(PlayerSpec.HumanPlayer) && !isConnected();
		}

		public void kick()
		{
			if (isConnected())
				connected.kick();
		}

		public boolean isConnected()
		{
			return spec.equals(PlayerSpec.HumanPlayer) && connected != null;
		}
		
		@Override
		public String toString()
		{
			return spec.name();
		}
		
	}
	// This is a serializable version...
	public static final class LobbyInfo implements Jsonable
	{
		public boolean allReady;
		public GameOptions options;
		public PlayerInfo[] players;
		public String id;

		public LobbyInfo() {}

		public LobbyInfo(JsonParser parser) throws IOException
		{
			int numPlayers = -1;
			JsonToken next;
			while (!(next = parser.nextToken()).equals(JsonToken.END_OBJECT))
			{
				if (!next.equals(JsonToken.FIELD_NAME))
					throw new RuntimeException("Unexpected.");

				String currentName = parser.getCurrentName();
				System.out.println("Parsing " + currentName);
				switch (next = parser.nextToken())
				{
				case VALUE_NULL:
					System.out.println("Value was null for " + currentName + " in lobby info");
					break;
				case VALUE_FALSE:
					switch (currentName)
					{
					case "all_ready":
						allReady = false; 
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case VALUE_TRUE:
					switch (currentName)
					{
					case "all_ready":
						allReady = true; 
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case VALUE_STRING:
					switch (currentName)
					{
					case "id":
						id = parser.getValueAsString();
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case VALUE_NUMBER_INT:
					switch (currentName)
					{
					case "numplayers":
						numPlayers = parser.getNumberValue().intValue();
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case START_OBJECT:
					switch (currentName)
					{
					case "options":
						options = new GameOptions(parser);
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				case START_ARRAY:
					switch (currentName)
					{
					case "players":
						players = new PlayerInfo[numPlayers];
						int index = 0;
						while (!(next = parser.nextToken()).equals(JsonToken.END_ARRAY))
						{
							switch (next)
							{
							case START_OBJECT:
								players[index++] = new PlayerInfo(parser);
								break;
							default:
								throw new RuntimeException("Unexpected.");
							}
						}
						break;
					default:
						throw new RuntimeException("Unexpected.");
					}
					break;
				default:
					throw new RuntimeException("Unexpected: " + next);
				}
			}
		}

		@Override
		public void write(JsonGenerator writer) throws IOException
		{
			writer.writeStartObject();
			writer.writeStringField("id", id);
			writer.writeBooleanField("all_ready", allReady);
			writer.writeFieldName("options");
			options.print(writer);
			writer.writeNumberField("numplayers", players.length);
			writer.writeFieldName("players");
			writer.writeStartArray();
			for (PlayerInfo player : players)
				player.write(writer);
			writer.writeEndArray();
			writer.writeEndObject();
		}

		public String getId()
		{
			return id;
		}

		public String getName()
		{
			return "a name";
		}
	}

	public void broadcastChanges() throws IOException
	{
		for (PlayerPlaceHolder player : players)
		{
			if (player.isConnected())
				player.connected.updateLobby();
		}
		server.lobbyChanged(this);
	}

	public void performAction(UpdateAction action, int playerNumber, PlayerConnection player) throws IOException
	{
		if (!player.admin)
		{
			return;
		}
		switch (action)
		{
		case Kick:
			player.kick();
			players.get(playerNumber).connected = null;
			break;
		case SetComputer:
			setPlayerSpec(playerNumber, PlayerSpec.Computer, true);
			break;
		case SetHuman:
			setPlayerSpec(playerNumber, PlayerSpec.HumanPlayer, true);
			break;
		default:
			throw new RuntimeException("not implemented.");
		}
	}

	public void setOptions(PlayerConnection player, GameOptions options2) throws IOException
	{
		if (!player.equals(host))
			return;
		this.options = options2;
		setNumPlayers(options.numberOfPlayers, true);
	}
	
	

	public void startGame() throws IOException
	{
		Game game = new Game(options);
		server.migrateToGame(this, game);
		for (PlayerPlaceHolder player : players)
		{
			switch (player.spec)
			{
			case HumanPlayer:
				if (player.connected == null)
					throw new RuntimeException("bad state.");
				player.playerNumber = game.add(player.connected);
				player.connected.game = game;
				break;
			case Computer:
				player.playerNumber = game.add(new ComputerAI(game));
				break;
			default:
				throw new RuntimeException("not implemented.");
			}
		}
		
		LobbyInfo info = getInfo();

		server.migrateToGame(this, game);

		for (PlayerPlaceHolder player : players)
		{
			switch (player.spec)
			{
			case HumanPlayer:
				if (player.connected == null)
					throw new RuntimeException("bad state.");
				// shoudl be a different type of game message...
				player.connected.connection.sendMessageAndFlush(new LaunchGame(player.playerNumber, info));
				break;
			case Computer:
				break;
			default:
				throw new RuntimeException("not implemented.");
			}
		}
		
		game.broadCast();
		game.launchComputerPlayers();
	}
}
