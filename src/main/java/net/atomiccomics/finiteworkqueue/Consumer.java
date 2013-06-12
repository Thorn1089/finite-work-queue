package net.atomiccomics.finiteworkqueue;

/**
 * The {@code Consumer} class describes some type which can process items from a work queue
 * in some fashion. Instances of this class <em>must</em> be thread-safe.
 * 
 * @author Tom
 *
 * @param <T> The type of objects which can be consumed from a work queue.
 */
public interface Consumer<T> {

	/**
	 * Consumes a single {@code T} from a work queue.
	 * @param t An object from a work queue; never null.
	 */
	void consume(T t);
	
}
