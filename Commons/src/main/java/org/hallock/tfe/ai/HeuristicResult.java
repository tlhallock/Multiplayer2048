package org.hallock.tfe.ai;

import java.util.Collection;

public interface HeuristicResult<T extends HeuristicResult> extends Comparable<T>
{
	public T add(T t);
	public T divide(T t);
	public T getZero();
	
	
	
	public static <T extends HeuristicResult> void average(Collection<HeuristicResult<T>> ts)
	{
	}
}
