package org.hallock.tfe.serve;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.hallock.tfe.cmn.game.GameOptions;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;


public class Lobby
{
        String lobbyName;
	GameOptions options;
	WaitingPlayer host;
	String id;
	
	
	
        ArrayList<PlayerSpec> desiredPlayers = new ArrayList<>();
        ArrayList<WaitingPlayer> waitingPlayers = new ArrayList<>();
        
        private HashMap<Integer, Integer> mapping = new HashMap<>();
        
        public void setNumPlayers(int numPlayers)
        {
            // Remove extra
            while (desiredPlayers.size() > numPlayers)
            {
                desiredPlayers.remove(desiredPlayers.size()-1);
            }
            // Add enough
            while (desiredPlayers.size() < numPlayers)
            {
                desiredPlayers.add(PlayerSpec.Computer);
            }
            
            reassign();
        }
        
        private void reassign()
        {
            mapping.clear();
            // Reassign
            int assignedIndex = 0;
            for (int i=0;i<desiredPlayers.size();i++)
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
                waitingPlayers.remove(waitingPlayers.size()-1).kick();
            }
        }
        
        
        public void setPlayerSpec(int player, PlayerSpec spec)
        {
            desiredPlayers.set(player, spec);
            reassign();
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        public boolean addPlayer(WaitingPlayer player)
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
		for (int i = 0; i < desiredPlayers.size(); i++)
		{
			info.players[i] = new PlayerInfo();
			if (!desiredPlayers.get(i).equals(PlayerSpec.HumanPlayer))
			{
				info.players[i].name = "Computer";
				info.players[i].status = "ready";
				continue;
			}

			int waitingIndex = mapping.get(i);
			if (waitingIndex < 0)
			{
				info.players[i].name = "empty";
				info.players[i].status = "waiting";
				break;
			}

			WaitingPlayer wp = waitingPlayers.get(waitingIndex);
			info.players[i].name = wp.getHostInfo();
			info.players[i].status = wp.ready ? "Ready" : "Not ready";
		}

		return info;
	}

	// This is a serializable version...
	public static final class LobbyInfo
	{
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
}
