import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.locks.*;



public class Trader implements Runnable{
	private final HashMap<String,Company> companyMap; // need to access the companies quickly by using the ticker as the key
	private Trade trade; //Holds all trades at a current time 
	//From Piaza Post of how to accurately display timing in console 
    private DateFormat timestamp;
    private BankAccount balance;
    Date result;
	Date curr; //Will be used for holding the current time 

	

	public Trader(List<Company> companyList, Trade trade, BankAccount balance , long start) {
		companyMap = new HashMap<>();
        this.balance = balance;
        this.timestamp = new SimpleDateFormat("[HH:mm:ss:SSS]"); 
        this.timestamp.setTimeZone(TimeZone.getTimeZone("UTC"));
        result = new Date(start);
		for(Company company : companyList) {
			companyMap.put(company.getTicker(), company);
		}
		
		this.trade=trade;
	}
	
	
	@Override
	public void run() {
		//For all trades in the List<Trade>
		
			Company company = companyMap.get(trade.getTicker());
			if(company != null) { //if the pointer is not null
				try {
					//see if you can get a semaphore to trade company
					//If you cant then just sit and wait for a semaphore to be released before making trade
					company.getSemaphore().acquire();
					makeTrade(trade);
				}catch(InterruptedException e){
					Thread.currentThread().interrupt();
					}finally {
						Thread.yield();
						company.getSemaphore().release();
					}
				}
			}
			
		

	
	void makeTrade(Trade trade) throws InterruptedException {
	    boolean isPurchase = trade.getQuantity() > 0;
	    int price = (int) Math.abs(trade.getPrice() * trade.getQuantity()); // Use absolute value for price to consider neg quantity for sale
	    curr = new Date(System.currentTimeMillis());
	    if (isPurchase) {
	        // For purchases, check and update the balance atomically

	            printTrade(curr, "Beggining purchase of ", trade);
	            long currBalance = balance.getBalance();
	            if (currBalance >= price) {
	                balance.submitBalance(-price);
	            } else { //Not enough money, trade cant happen, move on to next 
	                System.out.println("Transaction failed dur to insuffiencent balance. Unsuccessful purchase of " +
	                    trade.getQuantity() + " stocks of " + trade.getTicker());
	                return;
	            }

	        Thread.sleep(2000); // simming trade time

	    } else {
	        //Sale is simply adding ammount to balance. Dont need to check balance before 
	        try {
	            printTrade(curr, "Beggining sale of ", trade);
	            balance.submitBalance(price);
	        } finally {
	        }
	        Thread.sleep(3000); // simming trade time

	    }
	    printBalance(isPurchase);

	}

	//Prints beggining and completion of trade action
	public  synchronized void printTrade(Date curr ,String string, Trade trade) {
		if(trade.getQuantity() < 0) { //If the trade was a sale the quantity must be *-1 
			System.out.println(timestamp.format(new Date(System.currentTimeMillis() - result.getTime())) +string + (-1 * trade.getQuantity()) + " stocks of " + trade.getTicker());
		}else {
			System.out.println(timestamp.format(new Date(System.currentTimeMillis() - result.getTime())) +string + trade.getQuantity() + " stocks of " + trade.getTicker());
		}
	}
	
	public synchronized void printBalance(Boolean purchase) {
		 
	    if (purchase) {
	        printTrade(curr, "Completed purchase of ", trade);
	        System.out.println("Current Balance after trade : " + balance.getBalance());
	    } else {
	        printTrade(curr, "Completed sale of ", trade);
	        System.out.println("Current Balance after trade : " + balance.getBalance());
	    }
	}
}


