package org.hallock.tfe.msg;

import org.hallock.tfe.client.GameClient;

public abstract class GCClientMessage extends Message
{
	public abstract void perform(GameClient client);
}
