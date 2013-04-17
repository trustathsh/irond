package de.fhhannover.inform.iron.mapserver.communication.bus;

/*
 * #%L
 * ====================================================
 *   _____                _     ____  _____ _   _ _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \|  ___| | | | | | |
 *    | | | '__| | | / __| __|/ / _` | |_  | |_| | |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _| |  _  |  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_|   |_| |_|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Fachhochschule Hannover 
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.inform.fh-hannover.de/
 * 
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * A {@link Processor} has variable number of {@link ForwardTask} threads waiting
 * for someone to put work into {@link #mQueue}. The work fetched from the queue
 * is then executed using a {@link ExecutorService} and a very simple
 * {@link Runnable} implementation named {@link WorkTask}.
 * A fixed thread pool is used as {@link ExecutorService}. The number of threads
 * to be used can be configured using the constructor.
 * 
 * What exactly is done with the work has to be implemented in the processWork()
 * hook method in a sub-class.
 * 
 * @author aw
 *
 *
 * @param <T> type of work which can be found in the queue
 */
public abstract class Processor<T>  {
	
	/**
	 * Represents the source queue. This is where we can find some work.
	 */
	private Queue<T> mQueue;

	/**
	 * Represents the {@link ExecutorService} to process the work in the form of
	 * {@link Runnable} objects.
	 */
	private ExecutorService mWorkerExecService;

	/**
	 * Represents the {@link ExecutorService} which executes threads taking
	 * work from the queue and spawning new {@link WorkTask}s.
	 */
	private ExecutorService mFwdExecService;

	/**
	 * Gives the number of worker threads in the thread pool.
	 */
	private int mWorkersCount;

	/**
	 * Gives the number of threads forwarding work from the queue to the
	 * thread pool.
	 */
	private int mForwardersCount;
	
	/**
	 * Creates a processor.
	 * 
	 * 
	 * @param queue source queue for work.
	 * @param forwardThreadsN number of {@link ForwardingThread}s to be used.
	 * @param workerThreadsN number of threads used in fixed thread pool
	 */
	public Processor(Queue<T> queue, int workers, int forwarders) {
		
		NullCheck.check(queue, "queue is null");
		if (workers < 1 || forwarders < 1) {
			throw new RuntimeException("must be initialized with > 0 threads");
		}
		mQueue = queue;
		mWorkersCount = workers;
		mForwardersCount = forwarders;
		mWorkerExecService = Executors.newFixedThreadPool(mWorkersCount);
		mFwdExecService = Executors.newFixedThreadPool(mForwardersCount);
	}
	
	
	public int getForwardersCount() {
		return mForwardersCount;
	}
	
	public int getWorkersCount() {
		return mWorkersCount;
	}
	
	
	/**
	 * Start the {@link Processor} by executing the right number of
	 * {@link ForwardTask}s. These in turn will start up {@link WorkTask}s.
	 */
	public void start() {
		for (int i = 0; i < mForwardersCount; i++)
			mFwdExecService.execute(new ForwardTask());
	}
	
	/**
	 * Stop the {@link Processor}.
	 * 
	 * We simply shut both {@link ExecutorService}s down.
	 * 
	 */
	public void stop() {

		mFwdExecService.shutdown();
		mWorkerExecService.shutdown();
		
		while (!mFwdExecService.isShutdown()) {
			try {
				mFwdExecService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				// execute while() again, we wait until the executor service is
				// shut down.
			}
		}
		
		while (!mWorkerExecService.isShutdown()) {
			try {
				mWorkerExecService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				// execute while() again, we wait until the executor service is
				// shut down.
			}
		}
	}
	
	
	
	/**
	 * Hook method to do actual work on the elements which are taken
	 * out of the queue. This method has to be reentrant because it may be
	 * executed by multiple threads at the same time.
	 * 
	 * @param e
	 */
	public abstract void processWork(T work);
	
	

	
	/**
	 * Simple {@link Runnable} implementation.
	 * 
	 * Call the hook method processWork() of the outer class.
	 * 
	 * @author aw
	 *
	 */
	private class WorkTask implements Runnable {
		
		private T mWork;
		
		public WorkTask(T work) {
			mWork = work;
		}

		@Override
		public void run() {
			processWork(mWork);
		}
	}
	
	/**
	 * The {@link ForwardTask} blocking waits to get some work out of the source
	 * queue. If there is new work to do a {@link WorkTask} object is created.
	 * This object is then executed by worker {@link ExecutorService}.
	 * 
	 * If a interrupt is received at any point, the task will return from
	 * the run() method and thereby stop running.
	 * 
	 * @author aw
	 */
	private class ForwardTask implements Runnable {
		
		// If the task is blocked (mQueue.get()) and in the meanwhile
		// interrupted  a InterruptedException is thrown.
		// If the thread isn't blocked, the interrupt flag is set.
		// We break out of the while() when a InterruptException occurs,
		// we do not enter the while() anymore if the interrupt flag is set.

		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				T work;
				try {
					work = mQueue.get();
					Runnable workJob = new WorkTask(work);
					mWorkerExecService.execute(workJob);
				} catch (InterruptedException e) {
					// jump out of the while loop if interrupted
					break;
				}
			}
		}
		
	}
}
