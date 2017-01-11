package org.hallock.tfe.json.grep.results;

import java.io.IOException;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.json.grep.Match;

public abstract class MatchListener implements Jsonable
{
	public abstract int getNumberOfResults();
	
	public abstract void found(Match match) throws IOException;
}