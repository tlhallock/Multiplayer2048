package org.hallock.tfe.cmn.sys;

import org.hallock.tfe.sys.Logger;

public class SystemOutLogger implements Logger
{
	public void log(String message)
	{
		System.out.println(message);
	}
}
