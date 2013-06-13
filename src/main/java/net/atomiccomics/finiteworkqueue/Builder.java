package net.atomiccomics.finiteworkqueue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Builder {
	
	public static <T> ConsumerBuilder<T> from(BlockingQueue<T> queue) {
		return new ConsumerBuilder<T>(queue);
	}

	public static class ConsumerBuilder<T> {
		private final BlockingQueue<T> queue;
		
		private ConsumerBuilder(final BlockingQueue<T> queue) {
			this.queue = queue;
		}
		
		public LatchBuilder<T> takeWith(final Consumer<T> consumer) {
			return new LatchBuilder<T>(consumer, queue);
		}
	}
	
	public static class LatchBuilder<T> {
		private final Consumer<T> consumer;
		private final BlockingQueue<T> queue;
		
		private LatchBuilder(final Consumer<T> consumer, final BlockingQueue<T> queue) {
			this.consumer = consumer;
			this.queue = queue;
		}
		
		public ThreadBuilder<T> items(final int count) {
			return new ThreadBuilder<T>(new CountDownLatch(count), consumer, queue);
		}
	}
	
	public static class ThreadBuilder<T> {
		private ExecutorService executorService;
		private int threadCount;
		
		private final CountDownLatch latch;
		private final Consumer<T> consumer;
		private final BlockingQueue<T> queue;
		
		private ThreadBuilder(final CountDownLatch latch, final Consumer<T> consumer, final BlockingQueue<T> queue) {
			this(latch, consumer, queue, Executors.newCachedThreadPool(),  Runtime.getRuntime().availableProcessors());
		}
		
		private ThreadBuilder(ThreadBuilder<T> copy, final ExecutorService executorService) {
			this(copy.latch, copy.consumer, copy.queue, executorService, copy.threadCount);
		}
		
		private ThreadBuilder(ThreadBuilder<T> copy, final int threadCount) {
			this(copy.latch, copy.consumer, copy.queue, copy.executorService, threadCount);
		}
		
		private ThreadBuilder(final CountDownLatch latch, final Consumer<T> consumer, final BlockingQueue<T> queue,
				final ExecutorService executorService, final int threadCount) {
			this.latch = latch;
			this.consumer = consumer;
			this.queue = queue;
			this.threadCount = threadCount;
			this.executorService = executorService;
		}
		
		public ThreadBuilder<T> parallel(final int threadCount) {
			return new ThreadBuilder<T>(this, threadCount);
		}
		
		public ThreadBuilder<T> executeWith(final ExecutorService executorService) {
			return new ThreadBuilder<T>(this, executorService);
		}
		
		public FiniteWorkQueue<T> create() {
			Set<FiniteWorker<T>> workers = new HashSet<FiniteWorker<T>>();
			for(int i = 0; i < threadCount; i++) {
				workers.add(new FiniteWorker<T>(consumer, queue, latch));
			}
			return new FiniteWorkQueue<T>(workers, executorService, latch);
		}
	}
	
	
}
