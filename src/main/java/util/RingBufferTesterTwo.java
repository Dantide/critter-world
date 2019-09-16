package util;

import java.util.concurrent.BlockingQueue;

public class RingBufferTesterTwo {
	public static void main(String args[]) {
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
}
