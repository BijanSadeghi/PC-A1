package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersDeadlocked {
	
	static int numberOfPhilosophers = 5;
	static Lock[] chopsticks = new Lock[numberOfPhilosophers];

	public static void main(String[] args) {
		
		// Fill the array of chopsticks with locks representing each chopstick
		for(int j=0; j<numberOfPhilosophers; j++) {
			Lock lock = new ReentrantLock();
			chopsticks[j] = lock;
		}
		
		// Execute a thread for each philosopher
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);
		for(int i=0; i<numberOfPhilosophers; i++) {
			executor.execute(new Philosopher(i));
		}
		executor.shutdown();
		
	}

	public static class Philosopher implements Runnable {
		private int id; // id to identify a particular Philosopher
		
		public Philosopher(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			Lock leftChopstick, rightChopstick;
			
			// Assign the correct left/right chopsticks to the philosopher based on his id
			leftChopstick = chopsticks[id];
			if (id == numberOfPhilosophers - 1) {
				rightChopstick = chopsticks[0];
			} else {
				rightChopstick = chopsticks[id + 1];
			}
			
			while(true) {
				
				// Sleep for 3 seconds to allow time to read output
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// If the left chopstick is available, philosopher acquires the lock
				if(leftChopstick.tryLock()) {
					System.out.println("Philosopher " + id + " acquired left chopstick.");
					
					try {
						
						// Couldn't acquire right chopstick, wait in loop
						while(!rightChopstick.tryLock()) {
							System.out.println("Philosopher " + id + " failed to acquire right chopstick.");
							
							// Sleep for 3 seconds
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						// Acquired the right chopstick, Philosopher can begin eating
						System.out.println("Philosopher " + id + " acquired left & right chopsticks.");
						System.out.println("Philosopher " + id + " is eating");
						
						try {
							// Sleep for 3 seconds
							Thread.sleep(3000);
							
							// Philosopher finishes eating
							System.out.println("Philosopher " + id + " finished eating");
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							rightChopstick.unlock();
						}
						
					} finally {
						leftChopstick.unlock();
					}
				}
				// If the left chopstick is unavailable
				else {
					System.out.println("Philosopher " + id + " failed to acquire left chopstick.");
				}
				
				// If the right chopstick is available, philosopher acquires the lock
				if(rightChopstick.tryLock()) {
					System.out.println("Philosopher " + id + " acquired right chopstick.");
					
					try {
						
						// Couldn't acquire left chopstick, wait in loop
						while(!leftChopstick.tryLock()) {
							System.out.println("Philosopher " + id + " failed to acquire left chopstick.");
						}
						
						// Acquired the left chopstick, Philosopher can begin eating
						System.out.println("Philosopher " + id + " acquired left & right chopsticks.");
						System.out.println("Philosopher " + id + " is eating");
						
						try {
							// Sleep for 3 seconds
							Thread.sleep(3000);
							
							// Philosopher finishes eating
							System.out.println("Philosopher " + id + " finished eating");
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							leftChopstick.unlock();
						}
						
					} finally {
						rightChopstick.unlock();
					}
				}
					
			}

		}

	}

}
