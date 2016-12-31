/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hallock.tfe.serve;

import java.io.IOException;

import org.hallock.tfe.cmn.util.Connection;

public class PlayerConnection
{
	// Connection
	Connection connection;
	PlayerRole role;

	// Player information
	String playerName;
	
	public PlayerConnection(Connection connection2)
	{
		this.connection = connection2;
		playerName = "Unknown";
	}
	
	public void setName(String name) throws IOException
	{
		playerName = name;
		getRole().nameChanged();
	}
	
	public PlayerRole getRole()
	{
		return role;
	}
	
	public void setRole(PlayerRole role)
	{
		this.role = role;
	}
	
	String getHostInfo()
	{
		return connection.getConnectionInfo();
	}
	
	public String getName()
	{
		return playerName;
	}

	public void setWaiting()
	{
		this.role = new PlayerRole()
		{
			@Override
			public PlayerState getState()
			{
				return PlayerState.Waiting;
			}

			@Override
			public void nameChanged() {}

			@Override
			public int getIndex()
			{
				return -1;
			}
		};
	}
	
	
	
	
	
	

	public static interface PlayerRole
	{
		enum PlayerState
		{
			Waiting,
			InLobby,
			InGame,
		}
		
		
		public PlayerState getState();
		public void nameChanged() throws IOException;
		public int getIndex();
		
		public static boolean isInLobby(PlayerConnection role)
		{
			return role.getRole().getState().equals(PlayerState.InLobby);
		}
		public static boolean isInGame(PlayerConnection role)
		{
			return role.getRole().getState().equals(PlayerState.InGame);
		}
		public static boolean isWaiting(PlayerConnection role)
		{
			return role.getRole().getState().equals(PlayerState.Waiting);
		}
		
		static interface GameRole extends PlayerRole
		{
			public Game getGame();
		}
		
		static interface LobbyRole  extends PlayerRole
		{
			public Lobby getLobby();
		}
	}
}
