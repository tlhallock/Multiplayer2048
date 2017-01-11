package org.hallock.tfe.msg;

import org.hallock.tfe.cmn.util.Jsonable;

public abstract class Message implements Jsonable
{
	public static final String TYPE_FIELD = "type";
	
	public static final String SERVER_CREATE_LOBBY_TYPE = "create_lobby";
	public static final String SERVER_LIST_LOBBIES_TYPE = "list_lobbies";
	public static final String SERVER_SET_PLAYER_INFO_TYPE = "set_player_info";
	public static final String SERVER_JOIN_LOBBY_TYPE = "join_lobby";
	
	public static final String LOBBY_UPDATE_PLAYER_TYPE = "update_player";
	public static final String LOBBY_UPDATE_OPTIONS_TYPE = "update_options";
	public static final String LOBBY_LAUNCH_TYPE = "launch";
	public static final String LOBBY_READY_TYPE = "state_update";
	public static final String LOBBY_REFRESH_TYPE = "refresh";
	
	public static final String CLIENT_LOBBY_INFO_TYPE = "lobby_changed";
	public static final String CLIENT_LOBBY_LOBBIES_LIST_TYPE = "lobbieslist";
	public static final String LOBBY_CLIENT_KICK_TYPE = "kicked";
	
	public static final String GAME_STATE_CHANGED_TYPE = "state_change";
	public static final String GAME_AWARD_EVIL_ACTION_TYPE = "awarded_action";
	
	public static final String CLIENT_CONNECTION_LAUNCH_TYPE = "open_game";
	
	public static final String GAME_SERVER_PLAY_TYPE = "play_action";
	public static final String GAME_SERVER_PLAY_ACTION_TYPE = "play_evil_action";
	
	
	public static final String[] ALL_MESSAGE_TYPES =
	{
		SERVER_CREATE_LOBBY_TYPE      ,
		SERVER_LIST_LOBBIES_TYPE      ,
		SERVER_SET_PLAYER_INFO_TYPE   ,
		SERVER_JOIN_LOBBY_TYPE        ,
		LOBBY_UPDATE_PLAYER_TYPE      ,
		LOBBY_UPDATE_OPTIONS_TYPE     ,
		LOBBY_LAUNCH_TYPE             ,
		LOBBY_READY_TYPE              ,
		LOBBY_REFRESH_TYPE            ,
		CLIENT_LOBBY_INFO_TYPE        ,
		CLIENT_LOBBY_LOBBIES_LIST_TYPE,
		LOBBY_CLIENT_KICK_TYPE        ,
		GAME_STATE_CHANGED_TYPE       ,
		GAME_AWARD_EVIL_ACTION_TYPE   ,
		CLIENT_CONNECTION_LAUNCH_TYPE ,
		GAME_SERVER_PLAY_TYPE         ,
		GAME_SERVER_PLAY_ACTION_TYPE  ,
	};
	
//	public static Message parse(JsonParser parser) throws IOException
//	{
//		JsonToken nextToken = parser.nextToken();
//		switch (nextToken)
//		{
//		case END_ARRAY:
//			break;
//		case FIELD_NAME:
//			break;
//		default:
//			throw new RuntimeException("Unexpected.");
//
//		}
//		if (!nextToken.equals(JsonToken.FIELD_NAME))
//		{
//			throw new RuntimeException("Unexpected.");
//		}
//		if (!parser.getCurrentName().equals(TYPE_FIELD))
//		{
//			throw new RuntimeException("Unexpected.");
//		}
//		nextToken = parser.nextToken();
//		if (!nextToken.equals(JsonToken.VALUE_STRING))
//		{
//			throw new RuntimeException("Unexpected.");
//		}
//		
//		
//		String next = parser.getValueAsString();
//		switch (next)
//		{
//		case Message.GAME_STATE_CHANGED_TYPE:
//			return new StateChanged(parser);
//			
//		case PlayerAction.TYPE:
//			return new PlayerAction(parser);
//			
//			
//			
//		case Message.CLIENT_LOBBY_LOBBIES_LIST_TYPE:
//			return new LobbiesList(parser);
//		case Message.CLIENT_LOBBY_INFO_TYPE:
//			return new LobbyInfoMessage(parser);
//		case Message.LOBBY_CLIENT_KICK_TYPE:
//			return new Kicked(parser);
//			
//			
//			
//		case Message.CLIENT_CONNECTION_LAUNCH_TYPE:
//			return new LaunchGame(parser);
//			
//		case ListLobbies.TYPE:
//			return new ListLobbies(parser);
//		case CreateLobby.TYPE:
//			return new CreateLobby(parser);
//		case Refresh.TYPE:
//			return new Refresh(parser);
//		case Ready.TYPE:
//			return new Ready(parser);
//		case Message.LOBBY_UPDATE_OPTIONS_TYPE:
//			return new UpdateOptions(parser);
//		case Message.LOBBY_UPDATE_PLAYER_TYPE:
//			return new UpdatePlayer(parser);
//		case SJoinLobby.TYPE:
//			return new SJoinLobby(parser);
//		case Message.LOBBY_LAUNCH_TYPE:
//			return new Launch(parser);
//			
//			
//		case SetPlayerInfo.TYPE:
//			return new SetPlayerInfo(parser);
//			
//		case AwardedEvilAction.GAME_EVIL_ACTION_TYPE:
//			return new AwardedEvilAction(parser);
//		case PlayEvilAction.TYPE:
//			return new PlayEvilAction(parser);
//		default:
//			throw new RuntimeException("Unrecognized type: " + next);
//		}
//	}

//	
//	@Override
//	public String toString()
//	{
//		return Jsonable.toString(this);
//	}
}
