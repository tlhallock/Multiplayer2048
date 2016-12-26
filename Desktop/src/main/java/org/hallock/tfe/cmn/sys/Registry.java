package org.hallock.tfe.cmn.sys;

public class Registry
{
	static Logger logger = new SystemOutLogger();

	public static Logger getLogger()
	{
		return logger;
	}
	
	

}
