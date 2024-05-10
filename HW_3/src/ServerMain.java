import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ServerMain {
	private static int PORT = 3456;
	private static List<BrokerInfo> broker = new ArrayList<>();
	private static Map<Socket, Boolean> clientAvailbility = new ConcurrentHashMap<Socket, Boolean>();
	private static Map<Socket, PrintWriter> clientWriterMap = new ConcurrentHashMap<Socket, PrintWriter>();
	private static Map<Socket, BufferedReader> clientReadersMap = new ConcurrentHashMap<Socket, BufferedReader>();
	private static TreeMap<Integer, List<TradeOrders>> tradeMap = new TreeMap<>();
	private static Map<Socket, BrokerInfo> socketMap = new HashMap<>();
	private static ConcurrentLinkedQueue<TradeOrders> incompleteTrades = new ConcurrentLinkedQueue<TradeOrders>();
	private static long startTime;
	private static DateFormat timestamp;
	private static Date startDate;

	public static void main(String[] arg) throws IOException {
		timestamp = new SimpleDateFormat("[HH:mm:ss:SSS]");
		timestamp.setTimeZone(TimeZone.getTimeZone("UTC"));
		Scanner scan = new Scanner(System.in);
		boolean schedulePass = false;
		boolean traderPass = false;

		while (!schedulePass) {
			System.out.println("What is the name of the schedule file?");
			String scheduleFileName = scan.nextLine();
			tradeMap = FileReader.csvScheduleReader(scheduleFileName);
			if (tradeMap.isEmpty()) {
				System.out.println("There was an error reading the file. Please re-enter filename");
			} else {
				System.out.println("The schedule file has been properly read.");
				schedulePass = true;
			}
		}

		while (!traderPass) {
			System.out.println("What is the name of the traders file?");
			String traderFileName = scan.nextLine();
			broker = FileReader.csvTraderReader(traderFileName);
			if (broker.isEmpty()) {
				System.out.println("There was an error reading the file. Please re-enter filename");
			} else {
				System.out.println("The traders file has been properly read.");
				traderPass = true;
			}

		}

		scan.close();
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("Listening on port " + PORT);
			waitForAllTradersToConnect(serverSocket);
			ScheduledExecutorService tradeSchedule = Executors.newScheduledThreadPool(broker.size());
			CountDownLatch tradeLatch = new CountDownLatch(tradeMap.size());

			sendOutTrades(tradeMap, tradeSchedule, tradeLatch);
			tradeLatch.await();

			tradeSchedule.shutdown();
			if (!tradeSchedule.awaitTermination(60, TimeUnit.SECONDS)) {
				tradeSchedule.shutdownNow();
			}

			printIncompleteTrades(incompleteTrades);
			broadcast("ALL_TRADES_FINISHED");
			waitForFinish();
			System.out.println("Service finished. Exiting program.");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("Server shutdown interrupted.");
		}
	}

	private static void waitForFinish() {
		Set<Socket> completedClients = new HashSet<>();

		while (completedClients.size() < clientWriterMap.size()) {
			for (Socket clientSocket : new HashSet<>(clientWriterMap.keySet())) {
				if (!completedClients.contains(clientSocket)) {
					try {
						BufferedReader in = clientReadersMap.get(clientSocket);
						String responseString = in.readLine();
						if ("COMPLETE".equals(responseString)) {
							completedClients.add(clientSocket);
						}
					} catch (IOException e) {
						System.err.println(
								"Error reading completion status from socket: " + clientSocket + ": " + e.getMessage());
						completedClients.add(clientSocket);
					}
				}
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}

	private static void printIncompleteTrades(ConcurrentLinkedQueue<TradeOrders> incompleteTrades2) {

		timestamp.format(new Date(System.currentTimeMillis() - startDate.getTime()));
		StringBuilder incompleteBuilder = new StringBuilder(
				timestamp.format(new Date(System.currentTimeMillis() - startDate.getTime())) + "Incomplete Trades:");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date currentDate = new Date();

		if (incompleteTrades.isEmpty()) {
			incompleteBuilder.append(" NONE.");
		} else {
			for (TradeOrders trade : incompleteTrades) {
				String formattedDate = dateFormat.format(currentDate);
				incompleteBuilder.append(String.format(" (%s, %s, %d, %s)", trade.getTradeTime(), trade.getTicker(),
						trade.getAmount(), formattedDate)).append(";");
			}
		}
		broadcast(incompleteBuilder.toString());
	}

	private static void sendOutTrades(TreeMap<Integer, List<TradeOrders>> tradeMap,
			ScheduledExecutorService tradeSchedule, CountDownLatch latch) throws InterruptedException {
		broadcast("START_TIMER");
		startTime = System.currentTimeMillis();
		startDate = new Date(startTime);

		tradeMap.forEach((timeKey, scheduledTrades) -> {
			long delay = timeKey * 1000 - (System.currentTimeMillis() - startTime);
			delay = Math.max(delay, 0);

			tradeSchedule.schedule(() -> {
				
				for (TradeOrders trade : scheduledTrades) {
					try {
						updateClientAvailbility();
					} catch (IOException e) {
						e.printStackTrace();
					}

					boolean tradeExecuted = false;
					boolean avalible = false;
					boolean valid = false;
					for (Socket client : clientAvailbility.keySet()) {
					if (clientAvailbility.get(client)) {
						avalible = true;
						if(trade.getAmount() < 0) {
							execute(client, trade);
							tradeExecuted = true;
							break;
						}
						if (validTrade(client, trade)) {
							valid = true;
							execute(client, trade);
							tradeExecuted = true;
							break;
						}
					}
					}

					if (!tradeExecuted) {
						System.out.println(
								"Failed to execute trade for " + trade.getTicker() + " at " + timeKey + " seconds.");
						System.out.println(
								"avalible = " + avalible + " valid = " + valid);
						incompleteTrades.add(trade);
					}

					latch.countDown();
				}

			}, delay, TimeUnit.MILLISECONDS);
		});

		tradeSchedule.shutdown();
		try {
			if (!tradeSchedule.awaitTermination(60, TimeUnit.SECONDS)) {
				tradeSchedule.shutdownNow();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static boolean validTrade(Socket socket, TradeOrders trade) {
		BrokerInfo brokerInfo = socketMap.get(socket);
		try {
			if ( brokerInfo.getBalance() >= trade.getPrice()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void execute(Socket socket, TradeOrders trade) {

		BrokerInfo brokerInfo = socketMap.get(socket);

		if (brokerInfo == null) {
			System.out.println("No broker information found for the socket: " + socket);
			return;
		}

		PrintWriter writer = clientWriterMap.get(socket);
		String ticker = trade.getTicker();
		String amount = (trade.getAmount() + "");
		String time = (trade.getTradeTime() + "");
		String priceString = String.valueOf(trade.getPrice());
		String tradeCall = ticker + " , " + amount + " , " + time + " , " + priceString;

		writer.println(tradeCall);
		writer.flush();
		brokerInfo.setBalance(brokerInfo.getBalance() - (trade.getAmount() * trade.getPrice()));
	}

	private static void updateClientAvailbility() throws IOException {
		for (Socket clientSocket : clientAvailbility.keySet()) {
			try {
				PrintWriter out = clientWriterMap.get(clientSocket);
				BufferedReader in = clientReadersMap.get(clientSocket);
				out.println("AVAILABILITY");
				out.flush();
				String responseString = in.readLine();
				boolean isAvailable = "TRUE".equalsIgnoreCase(responseString);
				clientAvailbility.put(clientSocket, isAvailable);
			} catch (Exception e) {
				System.err.println("Error updating availability for socket: " + clientSocket + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private static void waitForAllTradersToConnect(ServerSocket serverSocket) throws IOException {
		while (clientWriterMap.size() < broker.size()) {
			Socket client = serverSocket.accept();
			connect(client);
			if (clientWriterMap.size() < broker.size()) {
				broadcast((broker.size() - clientWriterMap.size())
						+ " more trader(s) are needed before the service can begin");
				broadcast("Waiting...");
			}
		}
		broadcast("All traders have arrived!");
		broadcast("Starting Service");
	}

	private static void broadcast(String message) {
		for (PrintWriter writer : clientWriterMap.values()) {
			writer.println(message);
			writer.flush();
		}
	}

	private static void connect(Socket client) throws IOException {
		PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
		BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		BrokerInfo brokerInfo = broker.get(clientWriterMap.size());

		clientWriterMap.put(client, writer);
		clientReadersMap.put(client, reader);
		clientAvailbility.put(client, true);
		socketMap.put(client, brokerInfo);

		writer.println(brokerInfo.getBalance() + "," + brokerInfo.getID());
	}
}