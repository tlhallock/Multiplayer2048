package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.hallock.tfe.cmn.game.GameOptions;
import org.hallock.tfe.msg.LSUpdatePlayer.UpdateAction;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;


public class Lobby
{
	GameServer game;
	
	String lobbyName;
	GameOptions options;
	WaitingPlayer host;
	String id;

	ArrayList<PlayerSpec> desiredPlayers = new ArrayList<>();
	ArrayList<WaitingPlayer> waitingPlayers = new ArrayList<>();

	private HashMap<Integer, Integer> mapping = new HashMap<>();

	public void setNumPlayers(int numPlayers) throws IOException
	{
		// Remove extra
		while (desiredPlayers.size() > numPlayers)
		{
			desiredPlayers.remove(desiredPlayers.size() - 1);
		}
		// Add enough
		while (desiredPlayers.size() < numPlayers)
		{
			desiredPlayers.add(desiredPlayers.size() == 0 ? PlayerSpec.HumanPlayer : PlayerSpec.Computer);
		}

		reassign();
	}

	private void reassign() throws IOException
	{
		mapping.clear();
		// Reassign
		int assignedIndex = 0;
		for (int i = 0; i < desiredPlayers.size(); i++)
		{
			if (!desiredPlayers.get(i).equals(PlayerSpec.HumanPlayer))
			{
				continue;
			}

			if (assignedIndex >= waitingPlayers.size())
			{
				mapping.put(i, -1);
			}
			else
			{
				waitingPlayers.get(assignedIndex).assignedPlayerNumber = i;
				mapping.put(i, assignedIndex);
				assignedIndex++;
			}
		}

		// Remove extra players
		while (waitingPlayers.size() > assignedIndex)
		{
			// try catch
			waitingPlayers.remove(waitingPlayers.size() - 1).kick();
		}
		changed();
	}

	public void setPlayerSpec(int player, PlayerSpec spec) throws IOException
	{
		desiredPlayers.set(player, spec);
		reassign();
	}
        
        
        
        
        
        
        
        
        
        
        public boolean addPlayer(WaitingPlayer player) throws IOException
        {
            if (!needsPlayers())
                return false;
            
            waitingPlayers.add(player);
            reassign();
            
            return true;
        }
        
        
        
        
        
        
        
        
        
        
        
        public boolean needsPlayers()
        {
            int desired = getDesiredHumanPlayers();
            int have    = waitingPlayers.size();
            return have < desired;
        }
        
        public int getDesiredHumanPlayers()
        {
            int count = 0;
            for (PlayerSpec spec : desiredPlayers)
                if (spec.equals(PlayerSpec.HumanPlayer))
                    count++;
            return count;
        }
        
        
        
        
        
        
        
	public LobbyInfo getInfo()
	{
		LobbyInfo info = new LobbyInfo();
		info.id = id;
		info.options = new GameOptions(options);
		info.players = new PlayerInfo[desiredPlayers.size()];
		info.allReady = true;
		
		for (int i = 0; i < desiredPlayers.size(); i++)
		{
			info.players[i] = new PlayerInfo();
			info.players[i].playerNumber = i;
			if (!desiredPlayers.get(i).equals(PlayerSpec.HumanPlayer))
			{
				info.players[i].name = "Computer";
				info.players[i].status = "ready";
				info.players[i].type = PlayerSpec.Computer;
//				info.players[i].playerNumber = -1;
				continue;
			}

			int waitingIndex = mapping.get(i);
			if (waitingIndex < 0)
			{
				info.players[i].name = "empty";
				info.players[i].status = "waiting";
				info.players[i].type = PlayerSpec.HumanPlayer;
//				info.players[i].playerNumber = -1;
				info.allReady = false;
				break;
			}

			WaitingPlayer wp = waitingPlayers.get(waitingIndex);
			info.players[i].name = wp.getHostInfo();
			info.allReady &= wp.ready;
			info.players[i].status = wp.ready ? "Ready" : "Not ready";
			info.players[i].type = PlayerSpec.HumanPlayer;
//			info.players[i].playerNumber = wp.assignedPlayerNumber;
		}

		return info;
	}

	// This is a serializable version...
	public static final class LobbyInfo
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
				switch (parser.nextToken())
				{
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
					throw new RuntimeException("Unexpected.");
				}
			}
		}

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
				player.print(writer);
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

	public void changed() throws IOException
	{
		for (WaitingPlayer player : waitingPlayers)
		{
			player.updateLobby();
		}
	}

	public void performAction(UpdateAction action, int playerNumber, WaitingPlayer player) throws IOException
	{
		if (!player.admin)
		{
			return;
		}
		switch (action)
		{
		case Kick:
			player.kick();
			break;
		case SetComputer:
			desiredPlayers.set(playerNumber, PlayerSpec.Computer);
			reassign();
			break;
		case SetHuman:
			desiredPlayers.set(playerNumber, PlayerSpec.HumanPlayer);
			reassign();
			break;
		}
	}
}
