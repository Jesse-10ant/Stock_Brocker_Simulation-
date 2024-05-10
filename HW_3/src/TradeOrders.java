import java.time.LocalDateTime;

public class TradeOrders {
	private String mTicker;
	private int mAmount;
	private boolean mIsBuy;
	private int mTradeTime;
	private LocalDateTime mLastPriceUpdate;
	private double mPrice;

	public TradeOrders(String ticker, int amount, boolean isbuy, int time, double price) {
		setAmount(amount);
		setTicker(ticker);
		setIsBuy(isbuy);
		setTradeTime(time);
		setPrice(price);
		mLastPriceUpdate = LocalDateTime.MIN;
	}

	public void updatePrice(TradeQuote tradeQuote) {
		setPrice(tradeQuote.getC());
		mLastPriceUpdate = LocalDateTime.now();
	}

	public boolean isPriceValid() {
		return LocalDateTime.now().minusMinutes(1).isBefore(mLastPriceUpdate);
	}

	public String getTicker() {
		return mTicker;
	}

	public void setTicker(String mTicker) {
		this.mTicker = mTicker;
	}

	public int getAmount() {
		return mAmount;
	}

	public void setAmount(int mAmount) {
		this.mAmount = mAmount;
	}

	public boolean getIsBuy() {
		return mIsBuy;
	}

	public void setIsBuy(boolean mIsBuy) {
		this.mIsBuy = mIsBuy;
	}

	public int getTradeTime() {
		return mTradeTime;
	}

	public void setTradeTime(int mTime) {
		this.mTradeTime = mTime;
	}

	public double getPrice() {
		return mPrice;
	}

	public void setPrice(double mPrice) {
		this.mPrice = mPrice;
	}

}
