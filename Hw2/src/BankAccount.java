import java.util.concurrent.locks.ReentrantLock;
 
public class BankAccount {
	private long balance;
	ReentrantLock lock = new ReentrantLock();
	public void submitBalance(int ammount) {
		lock.lock();
		
		try {
				balance += ammount;
		}finally {
			lock.unlock();
		}
	}
	
	public long getBalance() {
		 	lock.lock();	        
		 	long currentBalance = balance;
	            try {
	                currentBalance = balance; 
	            } finally {
	                lock.unlock();
	            }
	        return currentBalance;
	    }
}
