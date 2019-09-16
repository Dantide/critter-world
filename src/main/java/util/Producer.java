package util;

import java.util.concurrent.BlockingQueue;
import java.util.Random; 
public class Producer implements Runnable{
	private BlockingQueue<Integer> queue;
	private final int stopValue;
	private final int stopValuePerProducer;
	
	public Producer (BlockingQueue<Integer> queue, int stopValue, int stopValuePerProducer) {
		this.queue = queue; 
		this.stopValue = stopValue;
		this.stopValuePerProducer = stopValuePerProducer;
	}
	
	@Override
	public void run() {
		try {
			generateRandomNumbers(); 
		}
		catch (InterruptedException e){
			Thread.currentThread().interrupt();
		}
	}

	public void generateRandomNumbers() throws InterruptedException{
		Random rand = new Random();
		for (int i = 0; i < 100; i++) {
			queue.put(i);
			Thread.sleep(rand.nextInt(1000));
			System.out.println(i);
		}
		for (int i = 0; i < stopValuePerProducer; i++) {
			queue.put(stopValue);
		}
	}
}
