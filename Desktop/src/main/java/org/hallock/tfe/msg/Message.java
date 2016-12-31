package org.hallock.tfe.msg;

import java.io.IOException;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.msg.g.PlayEvilAction;
import org.hallock.tfe.msg.g.PlayerAction;
import org.hallock.tfe.msg.gc.LaunchGame;
import org.hallock.tfe.msg.gv.AwardedEvilAction;
import org.hallock.tfe.msg.gv.StateChanged;
import org.hallock.tfe.msg.lc.Kicked;
import org.hallock.tfe.msg.lc.LobbiesList;
import org.hallock.tfe.msg.lc.LobbyInfoMessage;
import org.hallock.tfe.msg.ls.Launch;
import org.hallock.tfe.msg.ls.Ready;
import org.hallock.tfe.msg.ls.Refresh;
import org.hallock.tfe.msg.ls.UpdateOptions;
import org.hallock.tfe.msg.ls.UpdatePlayer;
import org.hallock.tfe.msg.svr.ListLobbies;
import org.hallock.tfe.msg.svr.SCreateLobby;
import org.hallock.tfe.msg.svr.SJoinLobby;
import org.hallock.tfe.msg.svr.SetPlayerInfo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public abstract class Message implements Jsonable
{
	public static final String TYPE_FIELD = "type";
	
	public static Message parse(JsonParser parser) throws IOException
	{
		JsonToken nextToken = parser.nextToken();
		switch (nextToken)
		{
		case END_ARRAY:
			break;
		case FIELD_NAME:
			break;
		default:
			throw new RuntimeException("Unexpected.");

		}
		if (!nextToken.equals(JsonToken.FIELD_NAME))
		{
			throw new RuntimeException("Unexpected.");
		}
		if (!parser.getCurrentName().equals(TYPE_FIELD))
		{
			throw new RuntimeException("Unexpected.");
		}
		nextToken = parser.nextToken();
		if (!nextToken.equals(JsonToken.VALUE_STRING))
		{
			throw new RuntimeException("Unexpected.");
		}
		
		
		String next = parser.getValueAsString();
		switch (next)
		{
		case StateChanged.TYPE:
			return new StateChanged(parser);
			
		case PlayerAction.TYPE:
			return new PlayerAction(parser);
			
			
			
		case LobbiesList.TYPE:
			return new LobbiesList(parser);
		case LobbyInfoMessage.TYPE:
			return new LobbyInfoMessage(parser);
		case Kicked.TYPE:
			return new Kicked(parser);
			
			
			
		case LaunchGame.TYPE:
			return new LaunchGame(parser);
			
		case ListLobbies.TYPE:
			return new ListLobbies(parser);
		case SCreateLobby.TYPE:
			return new SCreateLobby(parser);
		case Refresh.TYPE:
			return new Refresh(parser);
		case Ready.TYPE:
			return new Ready(parser);
		case UpdateOptions.TYPE:
			return new UpdateOptions(parser);
		case UpdatePlayer.TYPE:
			return new UpdatePlayer(parser);
		case SJoinLobby.TYPE:
			return new SJoinLobby(parser);
		case Launch.TYPE:
			return new Launch(parser);
			
			
		case SetPlayerInfo.TYPE:
			return new SetPlayerInfo(parser);
			
		case AwardedEvilAction.TYPE:
			return new AwardedEvilAction(parser);
		case PlayEvilAction.TYPE:
			return new PlayEvilAction(parser);
		default:
			throw new RuntimeException("Unrecognized type: " + next);
		}
	}
	
	@Override
	public String toString()
	{
		return Jsonable.toString(this);
	}
}
