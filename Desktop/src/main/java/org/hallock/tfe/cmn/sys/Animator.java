package org.hallock.tfe.cmn.sys;

import java.awt.Component;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Animator implements Runnable
{
	private static long NOT_RUNNING_MS = 10000;

	final Lock lock = new ReentrantLock();
	final Condition condition = lock.newCondition();
	
	boolean quit;
	
	long waitPeriod;

	LinkedList<Component> runnables = new LinkedList<>();
	
	public Animator(long waitPeriod)
	{
		this.waitPeriod = waitPeriod;
	}
	
	public void startRunning(Component runnable)
	{
		try
		{
			lock.lock();
			runnables.add(runnable);
			condition.signalAll();
		}
		finally
		{
			lock.unlock();
		}
	}
	public void stopRunning(Component runnable)
	{
		try
		{
			lock.lock();
			runnables.remove(runnable);
			condition.signalAll();
		}
		finally
		{
			lock.unlock();
		}
	}

	private void runit(Component run)
	{
		try
		{
			run.repaint();
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
		Thread.currentThread().setName("Animator");
		
		boolean quit;
		LinkedList<Component> running;

		try
		{
			lock.lock();
			quit = this.quit;
			running = (LinkedList<Component>) runnables.clone();
		}
		finally
		{
			lock.unlock();
		}
		
		
		while (!quit)
		{
			long waitTime = NOT_RUNNING_MS;

			long now = System.currentTimeMillis();
			for (Component runnable : running)
				runit(runnable);
			waitTime = Math.max(1, now + waitPeriod - System.currentTimeMillis());
				
			try
			{
				lock.lock();
				quit = this.quit;
				running = (LinkedList<Component>) runnables.clone();
				if (quit)
					break;
				if (running.isEmpty())
					waitTime = NOT_RUNNING_MS;
				condition.await(waitTime, TimeUnit.MILLISECONDS);
				quit = this.quit;
				running = (LinkedList<Component>) runnables.clone();
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
