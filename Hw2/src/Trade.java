


public class Trade {
	private int time;
	private String ticker;
	private int quantity;
	private double price;
	
	public Trade(int time2, String ticker2, int quantity2, double price2) {
		time = time2;
		ticker = ticker2;
		quantity = quantity2;
		price = price2;
	}
	
	
	public int getTime() {
		return time;
	}

	public String getTicker() {
		return ticker;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getPrice() {
		return price;
	}

}
