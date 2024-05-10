
public class BrokerInfo {
	private int ID;
	private double balance;

	BrokerInfo(int tempID, double tempBalance) {
		setID(tempID);
		setBalance(tempBalance);
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

}
