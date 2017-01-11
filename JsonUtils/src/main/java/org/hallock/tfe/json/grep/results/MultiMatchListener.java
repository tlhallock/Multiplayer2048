package org.hallock.tfe.json.grep.results;

import java.io.IOException;

import org.hallock.tfe.cmn.util.Jsonable;
import org.hallock.tfe.json.grep.MultiMatch;

public abstract class MultiMatchListener implements Jsonable
{
	public abstract int getNumberOfResults();
	
	public abstract void found(MultiMatch match) throws IOException;
}
