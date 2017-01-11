package org.hallock.tfe.msg.lc;

import java.io.IOException;
import java.util.LinkedList;

import org.hallock.tfe.msg.Message;
import org.hallock.tfe.msg.svr.ClientSender;
import org.hallock.tfe.serve.LobbyInfo;

import com.fasterxml.jackson.core.JsonGenerator;

public class LobbiesListSender extends ClientSender
{
	LinkedList<LobbyInfo> lobbies = new LinkedList<>();

	public void foundLobby(LobbyInfo info)
	{
		lobbies.add(info);
	}

	@Override
	public void sendMessage(JsonGenerator writer) throws IOException
	{
		writer.writeStartObject();
		writer.writeStringField(Message.TYPE_FIELD, Message.CLIENT_LOBBY_LOBBIES_LIST_TYPE);
		writer.writeFieldName("lobbies");
		writer.writeStartArray();
		for (LobbyInfo info : lobbies)
			info.write(writer);
		writer.writeEndArray();
		writer.writeEndObject();
	}
}
