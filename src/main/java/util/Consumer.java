package util;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable{
	private BlockingQueue<Integer> queue;
	private final int stopValue;
	public Consumer(BlockingQueue<Integer> queue, int stopValue) {
		this.queue = queue; 
		this.stopValue = stopValue; 
	}
	@Override
	public void run() {
		try {
			while(true) {
				Integer value = queue.take();
				if (value.equals(stopValue)) {return;}
				System.out.println(Thread.currentThread().getName() + " output: " + value);
				
			}
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
