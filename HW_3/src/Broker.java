import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import com.google.gson.Gson;

public class Broker {
	private double mBalance;
	private int mID;
	private double mTotalProfit = 0.0;
	private double mTotalCost = 0.0;
	private String API = "cntp4mpr01qt3uhjkbh0cntp4mpr01qt3uhjkbhg";
	private DateFormat timestamp;
	Date startTime = new Date();
	Gson gson = new Gson();
	Date curr;

	public Broker(int ID, double balance) {
		setID(ID);
		setBalance(balance);
		this.timestamp = new SimpleDateFormat("[HH:mm:ss:SSS]");
		this.timestamp.setTimeZone(TimeZone.getTimeZone("UTC"));
		startTime = new Date();
	}

	public double getBalance() {
		return mBalance;
	}

	public void setBalance(double mBalance) {
		this.mBalance = mBalance;
	}

	public void exeTrade(TradeOrders trade) {
		if (trade != null && trade.getIsBuy()) {
			if (trade.isPriceValid()) {
				double cost = Math.abs(trade.getAmount()) * trade.getPrice();
				if (mBalance >= cost) {
					mBalance -= cost;
					mTotalCost += cost;

					curr = new Date();
					long time = curr.getTime() - startTime.getTime();
					printTrade(time, trade, cost);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(
							getTimePrint() + "Finished purchase of " + trade.getAmount() + " of " + trade.getTicker());
				}
			} else {
				try {
					TradeQuote tq = getUpdateStockPrice(trade.getTicker());
					trade.updatePrice(tq);
				} catch (Exception e) {
					System.out.println("Error updating stock price " + e.getMessage());
					return;
				}

				double cost = Math.abs(trade.getAmount()) * trade.getPrice();

				if (mBalance >= cost) {
					mBalance -= cost;
					mTotalCost += cost;
					curr = new Date();
					long time = curr.getTime() - startTime.getTime();
					printTrade(time, trade, cost);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(getTimePrint() + "Finished purchase of " + Math.abs(trade.getAmount()) + " of "
							+ trade.getTicker());
				}
			}

		} else {
			if (trade.isPriceValid()) {
				double profit = Math.abs(trade.getAmount()) * trade.getPrice();
				mBalance += profit;
				mTotalProfit += profit;
				curr = new Date();
				long time = curr.getTime() - startTime.getTime();
				printTrade(time, trade, profit);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(getTimePrint() + "Finished sale of " + Math.abs(trade.getAmount()) + " of "
						+ trade.getTicker());
			} else {
				try {
					TradeQuote tq = getUpdateStockPrice(trade.getTicker());
					trade.updatePrice(tq);
				} catch (Exception e) {
					System.out.println("Error updating stock price " + e.getMessage());
					return;
				}
				double profit = Math.abs(trade.getAmount()) * trade.getPrice();
				mBalance -= profit;
				mTotalProfit += profit;
				curr = new Date();
				long time = curr.getTime() - startTime.getTime();
				printTrade(time, trade, profit);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				System.out.println(getTimePrint() + "Finished sale of " + Math.abs(trade.getAmount()) + " of "
						+ trade.getTicker());
			}
		}
	}

	public int getID() {
		return mID;
	}

	public void setID(int mID) {
		this.mID = mID;
	}

	public void startTimer() {
		this.startTime = new Date();
	}

	public synchronized void printTrade(long curr, TradeOrders trade, double dollars) {

		if (trade.getAmount() < 0) {
			System.out.println(getTimePrint() + "Beginning sale of " + (Math.abs(trade.getAmount())) + " stock(s) of "
					+ trade.getTicker() + ". Total gain  = " + trade.getPrice() + " * " + Math.abs(trade.getAmount())
					+ " = " + Math.abs(trade.getAmount() * trade.getPrice()) + ".");
		} else {
			System.out.println(getTimePrint() + "Beginning purchase of " + (Math.abs(trade.getAmount()))
					+ " stock(s) of " + trade.getTicker() + ". Total cost  = " + trade.getPrice() + " * "
					+ (Math.abs(trade.getAmount())) + " = " + Math.abs(trade.getAmount() * trade.getPrice()) + ".");
		}
	}

	public TradeQuote getUpdateStockPrice(String ticker) throws Exception {
		URL url = new URL("https://finnhub.io/api/v1/quote?symbol=" + ticker + "&token=" + API);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		BufferedReader read = new BufferedReader(new InputStreamReader((connection.getInputStream())));
		TradeQuote quote = gson.fromJson(read, TradeQuote.class);
		connection.disconnect();
		return quote;
	}

	public void FinalReport() {
		System.out.println("Total Profit Earned from Sales " + mTotalProfit);
		System.out.println("Total Cost from Purchases " + mTotalCost);
	}

	public String getTimePrint() {
		return timestamp.format(new Date(System.currentTimeMillis() - startTime.getTime()));
	}
}
