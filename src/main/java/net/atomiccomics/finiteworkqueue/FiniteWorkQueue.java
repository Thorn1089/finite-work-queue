package net.atomiccomics.finiteworkqueue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * The {@code FiniteWorkQueue} wraps a set of {@link FiniteWorker workers}, their associated
 * {@link CountDownLatch}, and an {@link ExecutorService} to run the group of workers.
 * 
 * @author Tom
 *
 * @param <T> The type of items to process.
 */
public class FiniteWorkQueue<T> {

	private final Set<FiniteWorker<T>> workers;
	
	private final ExecutorService executor;
	
	private final CountDownLatch latch;
	
	/**
	 * Creates a new {@link FiniteWorkQueue} which will run the given workers via the executor service
	 * until the countdown has expired.
	 * @param workers A {@code Set} of {@link FiniteWorker} instances which all feed from some work queue.
	 * @param executor An {@link ExecutorService} which is used to run the workers in parallel.
	 * @param latch A {@link CountDownLatch} which imposes a limit on the total number of items processed.
	 */
	public FiniteWorkQueue(final Set<FiniteWorker<T>> workers, final ExecutorService executor,
			final CountDownLatch latch) {
		this.workers = Collections.unmodifiableSet(workers);
		this.executor = executor;
		this.latch = latch;
	}
	
	public void start() {
		Set<Future<?>> threads = new HashSet<Future<?>>();
		for(FiniteWorker<T> worker : workers) {
			threads.add(executor.submit(worker));
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Future<?> thread : threads) {
			thread.cancel(true);
		}
	}
	
}
