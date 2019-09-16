package util;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

public class RingBufferTester {
	
	@Test (expected = NullPointerException.class)
	public void ringBufferAddTest() {
		RingBufferFactory<Integer> factory = new RingBufferFactory<Integer>();
		BlockingQueue<Integer> queue = factory.getSynchronizedBuffer(10);
		for (int i = 0; i < 10; i++) {
			queue.add(i); 
		}
		queue.add(null); 
	}
	@Test
	public void ringBufferOfferTest() {
		RingBufferFactory<Integer> factory = new RingBufferFactory<Integer>();
		BlockingQueue<Integer> queue = factory.getSynchronizedBuffer(10);
		for (int i = 0; i < 10; i++) {
			queue.offer(i); 
		}
		assertFalse(queue.offer(11));
	}
	@Test
	public void ringBufferPeekTest() {
		RingBufferFactory<Integer> factory = new RingBufferFactory<Integer>();
		BlockingQueue<Integer> queue = factory.getSynchronizedBuffer(10);
		for (int i = 0; i < 10; i++) {
			queue.offer(i); 
		}
		assertTrue(queue.peek() == 0);
	}
	@Test
	public void ringBufferElementTest() {
		RingBufferFactory<Integer> factory = new RingBufferFactory<Integer>();
		BlockingQueue<Integer> queue = factory.getSynchronizedBuffer(10);
		for (int i = 0; i < 10; i++) {
			queue.offer(i); 
		}
		assertTrue(queue.size() == 10); 
		assertTrue(queue.element() == 0); 
	}
	
	@Test (expected = IllegalStateException.class)
	public void ringBufferRemoveTest() {
		RingBufferFactory<Integer> factory = new RingBufferFactory<Integer>();
		BlockingQueue<Integer> queue = factory.getSynchronizedBuffer(10);
		for (int i = 0; i < 10; i++) {
			queue.offer(i); 
		}
		for (int i = 0; i < 10; i++) {
			assertTrue(queue.remove() == i);
		}
		queue.remove();
	}
	
	@Test
	public void ringBufferPollTest() {
		RingBufferFactory<Integer> factory = new RingBufferFactory<Integer>();
		BlockingQueue<Integer> queue = factory.getSynchronizedBuffer(10);
		for (int i = 0; i < 10; i++) {
			queue.offer(i); 
		}
		for (int i = 0; i < 10; i++) {
			assertTrue(queue.poll() == i);
		}
		assertNull(queue.poll());
	}
	@Test
	public void ringBufferContains() {
		RingBufferFactory<Integer> factory = new RingBufferFactory<Integer>();
		BlockingQueue<Integer> queue = factory.getSynchronizedBuffer(10);
		for (int i = 0; i < 10; i++) {
			queue.offer(i); 
		}
		for (int i = 0; i < 10; i++) {
			assertTrue(queue.contains(i));
		}
	}
	//@Test
	public void takeputTest() {
		int producers = 4;
		int consumers = Runtime.getRuntime().availableProcessors();
		RingBufferFactory<Integer> factory = new RingBufferFactory<Integer>();
		BlockingQueue<Integer> queue = factory.getSynchronizedBuffer(10);
		
		for (int i = 0; i < producers; i++) {
			new Thread(new Producer(queue, Integer.MAX_VALUE, producers/consumers)).start();
		}
		for (int i = 0; i < consumers; i++) {
			new Thread(new Consumer(queue, Integer.MAX_VALUE)).start();
		}
	}
	@Test
	public void iteratorTest() {
		RingBufferFactory<Integer> factory = new RingBufferFactory<Integer>();
		BlockingQueue<Integer> queue = factory.getSynchronizedBuffer(10);
		for (int i = 0; i < 10; i++) {
			queue.offer(i); 
		}
		Iterator<Integer> queueIterator = queue.iterator();
		while (queueIterator.hasNext()) {
			System.out.println(queueIterator.next());
		}
	}
	
}
