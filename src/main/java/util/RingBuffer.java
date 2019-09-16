package util;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Blocking queue to manage a thread pool
 *
 */
public class RingBuffer<T> implements BlockingQueue<T>{
	/**
	 * The fixed-size array representing the ring buffer
	 * Holds the objects
	 */
	private T[] elements;
	/**
	 * The current index in {@code elements} to be read from
	 */
	private int head;
	/**
	 * The current index in {@code elements} to be written to
	 */
	private int tail;
	/**
	 * The amount of objects currently held in elements
	 */
	private int size;
	/**
	 * 
	 * @param capacity size of the ring buffer
	 * @throws IllegalArgumentException if the capacity of the ringbuffer is <= 0
	 */
	public RingBuffer(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("RingBuffer capacity should be positive."); 
		}
		elements = (T[]) new Object[capacity];
		head = 0;
		tail = 0; 
		size = 0;
	}
	
	@Override
	public synchronized T remove() {
		T value = element(); 
		head = (head + 1) % elements.length;
		size--;
		return value;
	}

	@Override
	public synchronized T poll() {
		T value = peek(); 
		head = (head + 1) % elements.length;
		size--;
		return value;
	}

	@Override
	public synchronized T element() {
		if (size ==0) {
			throw new IllegalStateException("The queue is empty."); 
		}
		return elements[head];
	}

	@Override
	public synchronized T peek() {
		if (size == 0) {
			return null;
		}
		return elements[head];
	}

	@Override
	public synchronized int size() {
		return size;
	}

	@Override
	public synchronized boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new RingBufferIterator<T>();
	}

	@Override
	public synchronized boolean add(T e) {
		if (e == null) {
			throw new NullPointerException(); 
		}
		if (size == elements.length) {
			throw new IllegalStateException("The queue is full."); 
		}
		elements[tail] = e;
		tail = (tail + 1) % elements.length; 
		size++; 
		notifyAll(); 
		return true;
	}

	@Override
	public synchronized boolean offer(T e) {
		if (e == null) {
			throw new NullPointerException();
		}
		if (size == elements.length) {
			return false; 
		}
		elements[tail] = e;
		tail = (tail + 1) % elements.length;
		size++; 
		notifyAll();
		return true;
	}

	@Override
	public synchronized void put(T e) throws InterruptedException {
		if (e == null) {
			throw new NullPointerException();
		}
		while (size == elements.length) {
			//System.out.println(Thread.currentThread().getName() + " Waiting");
			wait();
		}
		elements[tail] = e;
		tail = (tail + 1) % elements.length; 
		size++;
		notifyAll();
	}

	@Override
	public synchronized boolean contains(Object o) {
		for (int i = 0; i < elements.length; i++) {
			if (elements[i].equals(o)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public synchronized T take() throws InterruptedException {
		assert(size <= elements.length && size >= 0);
		while (size == 0) {
			//System.out.println(Thread.currentThread().getName() + " Waiting");
			wait();
		}
		T value = elements[head];
		head = (head + 1) % elements.length;
		size--;
		notifyAll();
		return value;
	}
	
	public class RingBufferIterator<T> implements Iterator<T>{
		private int startingPoint = head;
		@Override
		public boolean hasNext() {
			return startingPoint != size;
		}

		@Override
		public T next() {
			int i = startingPoint;
			startingPoint++;
			return (T)elements[i];
		}
	}
	@Override
	public boolean offer(Object e, long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException(); 
	}
	@Override
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public int remainingCapacity() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection c) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public int drainTo(Collection c, int maxElements) {
		throw new UnsupportedOperationException(); 
	}
	
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public Object[] toArray(Object[] a) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public boolean containsAll(Collection c) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public boolean addAll(Collection c) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException(); 
	}
}
