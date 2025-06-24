package model;
public class StockModel {

		private String ticker;
		private int numStock;
		private float price;
		private int total_ammount;
		private double avg_price;
		public StockModel(String ticker2, int totalStocks, double averagePrice) {
			setTicker(ticker2);
			setTotalAmmount(totalStocks);
			setAvgPrice(averagePrice);
			
		}
		public String getTicker() {
			return ticker;
		}
		public void setTicker(String ticker) {
			this.ticker = ticker;
		}
		public int getNumStock() {
			return numStock;
		}
		public void setNumStock(int numStock) {
			this.numStock = numStock;
		}
		public float getPrice() {
			return price;
		}
		public void setPrice(float price) {
			this.price = price;
		}
		public int getTotalAmmount() {
			return total_ammount;
		}
		public void setTotalAmmount(int total_ammount) {
			this.total_ammount = total_ammount;
		}
		public double getAvgPrice() {
			return avg_price;
		}
		public void setAvgPrice(double avg_price) {
			this.avg_price = avg_price;
		}
		
}
