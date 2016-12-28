package org.hallock.tfe.cmn.sys;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Animator implements Runnable
{
	private static long NOT_RUNNING_MS = 10000;

	final Lock lock = new ReentrantLock();
	final Condition condition = lock.newCondition();
	
	boolean running;
	boolean quit;
	
	long waitPeriod;

	Runnable runnable;
	
	public Animator(long waitPeriod, Runnable runnable)
	{
		this.waitPeriod = waitPeriod;
		this.runnable = runnable;
	}
	
	public void setRunning(boolean val)
	{
		if (running == val)
			return;
		
		running = val;
		try
		{
			lock.lock();
			condition.signalAll();
		}
		finally
		{
			lock.unlock();
		}
	}

	private void runit()
	{
		if (runnable == null)
			return;
		try
		{
			runnable.run();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}

	public void quit()
	{
		try
		{
			lock.lock();
			quit = true;
			condition.signalAll();
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override
	public void run()
	{
		boolean quit;
		boolean running;

		try
		{
			lock.lock();
			quit = this.quit;
			running = this.running;
		}
		finally
		{
			lock.unlock();
		}
		
		
		while (true)
		{
			long waitTime = NOT_RUNNING_MS;
			
			if (running)
			{
				long now = System.currentTimeMillis();
				runit();
				waitTime = Math.max(1, now + waitPeriod - System.currentTimeMillis());
			}
				
			try
			{
				lock.lock();
				quit = this.quit;
				running = this.running;
				if (quit)
					break;
				if (!running)
					waitTime = NOT_RUNNING_MS;
				condition.await(waitTime, TimeUnit.MILLISECONDS);
				quit = this.quit;
				running = this.running;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			finally
			{
				lock.unlock();
			}
		}
	}
}
