import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import com.google.gson.Gson;


public class Menu {
	
    public static void main(String[] arg) throws IOException, InterruptedException {
        boolean CorrectJsonFile = false;
        BankAccount account = new BankAccount();
        System.out.println("-----***** Welcome To JoesStocks *****-----");

        while (!CorrectJsonFile) {
            System.out.println("What is the name of the file containing the company information? ");
            Scanner file = new Scanner(System.in);
            String filename = file.nextLine();
            CompanyList companies = new CompanyList();

            int numberOfTrades = 0;
            TreeMap < Integer, List <Trade>> tradeSchedule = new TreeMap <> ();  //tree map insures softing of trades by time
            try {
                String parsedJson = JsonParse.parseJsonFile(filename);
                if (JsonParse.isValidJson(parsedJson)) {
                    Gson gson = new Gson();
                    companies = gson.fromJson(parsedJson, CompanyList.class);
                    CorrectJsonFile = true;
                } else {
                	file.close();
                }
                for (Company company: companies.getData()) {
                    company.initCompany(); //create proper num of semaphores for each class
                }
                System.out.println("The company file has been properly read.");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }catch(ImproperFormat format){
				System.out.println(format.getMessage());
			}finally {}


            //CSV file being parsed for the trade schedule
            // Time, Ticker, Quantity, Price
            System.out.println("What is the name of the file containing the schedule information?");
            String CSVfilename = file.nextLine();
            List < Trade > trades = new ArrayList <> (); //Holding all trades, this list will be passed to every stock broker
            try (Scanner CSVFile = new Scanner(new File(CSVfilename))) {
                CSVFile.useDelimiter(",|\\r?\\n");
                while (CSVFile.hasNextLine()) { //Time , Ticker, Quantity, Price
                    int time = CSVFile.nextInt();
                    String ticker = CSVFile.next();
                    int quantity = CSVFile.nextInt();
                    int price = CSVFile.nextInt();
                    Trade trade = new Trade(time, ticker, quantity, price);
                    tradeSchedule.computeIfAbsent(trade.getTime(), k -> new ArrayList < > ()).add(trade);
                    numberOfTrades++;
                }

            } catch (FileNotFoundException e) {
                System.out.println("CSV File was not found: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An error occurred while processing the CSV file: " + e.getMessage());
            } finally {
                System.out.println("The schedule file has been properly read");
            }



            System.out.println("What is the initial balance?");
            account.submitBalance(file.nextInt());


            //List for our trades to be performed
            List < Company > stocks = companies.getData();
            System.out.println("Starting execution of program...");

            // Scheduled executor to check the trades list every second
            //Thread pool is the number of trades we have to execute durng the day
            //Pooling all of the threads helps to simplify thread usage since a thread will always be ready to go
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numberOfTrades);
            
            //Using this to exit my loop each scheduled trade will count down on the latch 
            //When the latch is at 0 will lock the scheduler once all trades are commited
            CountDownLatch latch = new CountDownLatch(numberOfTrades);

            //Logs the starting time of the program for accurate execution of trades
            long startTime = System.currentTimeMillis();

            //every trading time that we got from CSV we will process the List of trades and schedule them all to go out at the set time 
            for (Map.Entry < Integer, List < Trade >> entry: tradeSchedule.entrySet()) {
            	
                long scheduledTime = entry.getKey(); // The key is the time that the trade should be going out 
                
                List < Trade > tradesAtThisTime = entry.getValue();

                //We are counting starting time as 0 and using it as the reference
          
                long delay= scheduledTime * 1000 - (System.currentTimeMillis() - startTime);
                if (delay < 0) {
                    delay = 0; // If the scheduled time has already passed, execute immediately
                }

                // Schedule each trade to be executed after the calculated delay
                for (Trade trade: tradesAtThisTime) {
                    scheduler.schedule(() -> {

                    Trader stockBroker = new Trader(stocks, trade, account , startTime);
					stockBroker.run(); 
					//once you are done with the trade process remove decrement the ammount of trades left to do 
                    latch.countDown(); 
                        }
                    , delay, TimeUnit.MILLISECONDS);
                }
            }          
            // Wait for all trades to complete
           	//This seemed to correct my issue of competition message being printed too early 
            latch.await(); 
            System.out.println("All trades completed for the day");
    
            //Clean up code, 
            //Insure executor is shutdown properly
            //Cklose
            scheduler.shutdownNow(); // Attempt to stop all actively executing tasks immediately
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    file.close();
                }
            }
        }
}
