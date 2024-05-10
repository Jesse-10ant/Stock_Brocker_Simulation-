import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

public class ClientMain {

	private static Broker broker = null;
	private static List<TradeOrders> tradeList = new ArrayList<TradeOrders>();
	private static boolean isBusy = false;
	private static DateFormat timestamp;

	public static void main(String[] args) {
		timestamp = new SimpleDateFormat("[HH:mm:ss:SSS]");
		timestamp.setTimeZone(TimeZone.getTimeZone("UTC"));

		System.out.println("Welcome to JoesStocks v2.0!");
		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter the server hostname:");
		String host = scanner.nextLine();

		System.out.println("Enter the server port:");
		int port = scanner.nextInt();
		scanner.nextLine();

		try (Socket socket = new Socket(host, port);
				BufferedReader buff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

			String lineString;
			while ((lineString = buff.readLine()) != null) {
				handleMessage(lineString, out);
			}

		} catch (SocketException e) {
			System.out.println("Processing Complete.");
		} catch (Exception e) {
			System.err.println("An error occurred: " + e.getMessage());
			e.printStackTrace();
		} finally {
			scanner.close();
		}
	}

	private static void handleMessage(String message, PrintWriter out) throws Exception {
		boolean processed = false;

		if ("START_TIMER".equals(message)) {
			if (broker != null) {
				broker.startTimer();
				processed = true;
			}
		}
		if ("ALL_TRADES_FINISHED".equals(message)) {

			broker.FinalReport();
			processed = true;

		} else if ("AVAILABILITY".equalsIgnoreCase(message)) {
			processed = true;

			if (isBusy) {
				out.println("FALSE");
				out.flush();
			} else {
				out.println("TRUE");
				out.flush();
			}
			processed = true;
		} else if ("FINAL_REPORT".equalsIgnoreCase(message)) {
			broker.FinalReport();
			processed = true;
		}
		if (!processed) {
			processTradeAssignment(message, out);
		}
	}

	private static void processTradeAssignment(String message, PrintWriter out) throws Exception {
		String[] parts = message.split(",");

		if (parts.length == 4) {
			String tickerString = parts[0].trim();
			int amount = Integer.parseInt(parts[1].trim());
			int tradeTime = Integer.parseInt(parts[2].trim());
			double price = Double.parseDouble(parts[3].trim());
			boolean isBuy;
			if (amount < 0) {
				isBuy = false;
			} else {
				isBuy = true;
			}
			TradeOrders trade = new TradeOrders(tickerString, amount, isBuy, tradeTime, price);

			tradeList.add(trade);
			if (broker != null) {
				String timeString = broker.getTimePrint();
				if (amount < 0) {
					System.out.println(timeString + "Assigned sale of " + (Math.abs(trade.getAmount()))
							+ " stock(s) of " + trade.getTicker() + ". Total esimated gain = " + price + " * "
							+ Math.abs(trade.getAmount()) + " = " + Math.abs(amount * price) + ".");
				} else {
					System.out.println(timeString + "Assigned purchase of " + (Math.abs(trade.getAmount()))
							+ " stock(s) of " + trade.getTicker() + ". Total esimated cost  = " + price + " * "
							+ (Math.abs(trade.getAmount())) + " = " + Math.abs(amount * price) + ".");
				}
				if (!isBusy) {
					exeTrades(out);
				}
			}
		}

		else if (parts.length == 2) {
			if (broker == null) {
				broker = new Broker(Integer.parseInt(parts[1].trim()), Double.parseDouble(parts[0].trim()));
			}
		} else {
			System.out.println(message);
		}

	}

	private static void exeTrades(PrintWriter out) {
		new Thread(() -> {
			isBusy = true;
			try {
				while (!tradeList.isEmpty()) {
					TradeOrders trade = tradeList.remove(0);
					broker.exeTrade(trade);
				}
			} finally {
				isBusy = false;
				out.println("AVALIBLE");
				out.flush();
			}
		}).start();
	}
}
