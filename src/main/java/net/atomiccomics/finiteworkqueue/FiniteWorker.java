package net.atomiccomics.finiteworkqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * The {@code FiniteWorker} is a {@link Runnable} which consumes items from a work queue until
 * interrupted. It notifies other classes via a {@link CountDownLatch} when an item has been
 * dequeued, though not necessarily consumed.
 * 
 * @author Tom
 *
 * @param <T> The type of object which is produced from the work queue and processed by the consumer.
 */
public class FiniteWorker<T> implements Runnable {

	private final Consumer<T> consumer;
	
	private final BlockingQueue<T> workQueue;
	
	private final CountDownLatch latch;
	
	/**
	 * Creates a new {@link FiniteWorker} that uses the given {@link Consumer} instance to process items.
	 * @param consumer A {@code Consumer} to process work tasks.
	 * @param workQueue A {@link BlockingQueue} that provides work tasks.
	 * @param latch A {@link CountDownLatch} to notify when a task has been dequeued.
	 */
	public FiniteWorker(final Consumer<T> consumer, final BlockingQueue<T> workQueue, final CountDownLatch latch) {
		this.consumer = consumer;
		this.workQueue = workQueue;
		this.latch = latch;
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try {
				T t = workQueue.take();
				latch.countDown();
				consumer.consume(t);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

	}

}
