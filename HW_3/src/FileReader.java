import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileReader {
	private static String API = "cntp4mpr01qt3uhjkbh0cntp4mpr01qt3uhjkbhg";
	static Gson gson = new Gson();

	public static TreeMap<Integer, List<TradeOrders>> csvScheduleReader(String file) throws FileNotFoundException {
		TreeMap<Integer, List<TradeOrders>> tradeSchedule = new TreeMap<Integer, List<TradeOrders>>();

		try (Scanner csvFile = new Scanner(new File(file))) {
			while (csvFile.hasNextLine()) {
				String ln = csvFile.nextLine();
				String[] val = ln.split(",");

				if (val.length != 3) {
					throw new FileNotFoundException("Invalid line format" + ln);
				}

				int time = Integer.parseInt(val[0].trim());
				String tick = val[1].trim();
				int amount = Integer.parseInt(val[2].trim());
				Boolean isbuy = (amount > 0);
				TradeQuote tradeQuote;
				try {

					tradeQuote = getUpdateStockPrice(tick);
					TradeOrders trade = new TradeOrders(tick, amount, isbuy, time, tradeQuote.getC());
					tradeSchedule.computeIfAbsent(trade.getTradeTime(), k -> new ArrayList<>()).add(trade);				} catch (Exception e) {
					e.printStackTrace();
				}
	

			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return tradeSchedule;
		}

		return tradeSchedule;
	}

	public static List<BrokerInfo> csvTraderReader(String file) throws FileNotFoundException {
		List<BrokerInfo> brokersInfo = new ArrayList<BrokerInfo>();
		try (Scanner csvFile = new Scanner(new File(file))) {
			while (csvFile.hasNextLine()) {
				String ln = csvFile.nextLine();
				String[] val = ln.split(",");

				if (val.length != 2) {
					throw new FileNotFoundException("Invalid line format" + ln);
				}

				int ID = Integer.parseInt(val[0].trim());
				double balance = Integer.parseInt(val[1].trim());
				brokersInfo.add(new BrokerInfo(ID, balance));
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return brokersInfo;
		}

		return brokersInfo;
	}

	
	
	public static TradeQuote getUpdateStockPrice(String ticker) throws Exception {
		URL url = new URL("https://finnhub.io/api/v1/quote?symbol=" + ticker + "&token=" + API);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		BufferedReader read = new BufferedReader(new InputStreamReader((connection.getInputStream())));
		TradeQuote quote = gson.fromJson(read, TradeQuote.class);
		connection.disconnect();
		return quote;
	}
}
