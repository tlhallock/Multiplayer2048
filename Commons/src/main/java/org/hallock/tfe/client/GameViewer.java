package org.hallock.tfe.client;

import org.hallock.tfe.cmn.game.evil.EvilAction;
import org.hallock.tfe.serve.GamePlayerInfo;
import org.hallock.tfe.sys.GameUpdateInfo;

public interface GameViewer
{
	void updatePlayer(GamePlayerInfo info);
	void die();
	void updateInfo(GameUpdateInfo info);
	void award(EvilAction action);
}
