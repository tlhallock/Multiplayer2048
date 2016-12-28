package org.hallock.tfe.cmn.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

public interface Jsonable
{
	public void write(JsonGenerator generator) throws IOException;
}
