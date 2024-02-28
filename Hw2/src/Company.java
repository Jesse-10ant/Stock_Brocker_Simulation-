import java.util.concurrent.Semaphore;



public class Company {
	private String ticker;
	private int stockBrokers;
	private transient Semaphore semaphore; // Replicating the ammount of stock brokers per company
	
	//as reading from json and no constructor this is used to assign semphore as unknown at runtime 
	public void initCompany() {
		  this.semaphore = new Semaphore(this.stockBrokers, true); 		  
    }
	
	public int getBrokers() {
		return stockBrokers;
	}
    public Semaphore getSemaphore() {
        return semaphore;
    }
	
	public String getTicker() {
		return ticker;
	}
}
