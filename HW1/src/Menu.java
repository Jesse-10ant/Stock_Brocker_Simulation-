import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;


//Creating custom exception to handle cases where the JSON is not properly formatted
//Missing data information // JSON not titled data // wrong date format 
class ImproperFormat extends Exception{
	private static final long serialVersionUID = 1L;
	
	private String filename;
	public ImproperFormat(String message, String filename) {
		super(message);
		this.filename = filename;
	}
	
	public String getMessage() {
		return filename + " is not formatted properly";
	}
}



public class Menu {
	 
	public static void main(String[] args) {
		Stocks_List stockList = null; //contains all stocks from JSON file as an accessible list
		Boolean correctFile = false; //flag for indicating when file has been accepted
		String filename = ""; //holds name of JSON file to be parsed
		
		//While the user has not entered a file that meets all requirements
		//Formated correctly // Exists //No IO exceptions
		//Continue asking for new file
		while(!correctFile) {
			Scanner fileScanner = new Scanner(System.in);
			System.out.print("What is the name of the company file?");
			filename = fileScanner.nextLine();
			try 
			{	
				//Sends parsed JSON file through validation function, 
				//If JSON file is not found or has formatting errors 
				//user will be reprompted for file name
				String parsedJson = parseJsonFile(filename);
				if(!isValidJson(parsedJson)){
					throw new ImproperFormat("Improper format ",filename);
				}
					Gson gson = new Gson();
					stockList = gson.fromJson(parsedJson,Stocks_List.class);
					correctFile = true;
			}
			catch(FileNotFoundException fnfe) {
				System.out.println("The file " + filename + " could not be found");
			}
			catch(IOException e) 
			{
				System.out.println(e.getMessage());
			}
			catch(ImproperFormat format){
				System.out.println(format.getMessage());
			}
			finally{
				
				
			}
			
		}
		System.out.println("The file has been properly read.");
		
		//While loop menu continuously until 7(quit) is pressed
		while(true){	
			int choice = 0; // Holds users input
			System.out.println("1) Display all public companies");
			System.out.println("2) Search for a stock (by ticker)");
			System.out.println("3) Search for all stocks on an exchange");
			System.out.println("4) Add a new company/stocks");
			System.out.println("5) Remove a company");
			System.out.println("6) Sort companies");
			System.out.println("7) Exit");
			System.out.println("What would you like to do?");
			Scanner menuInput = new Scanner(System.in);
			choice = menuInput.nextInt();
			if(choice == 1) { 
				//Print all stocks in the stockList 
				//output all information provided for them 
				displayAllStocks(stockList);
			
			}else if (choice == 2) {
				//Searches stockList for company represented with the inputed ticker symbol
				//Prompted until stock with that symbol is found
				searchByTicker(menuInput, stockList);
			
			}else if (choice == 3) {
				//Looks for exchange that is inputed 
				//Lists all stocks on that exchange
				//Only NYSE and NASDAQ supported 
				searchForExchange(menuInput, stockList);
			
			}else if(choice == 4) {
				//Adds new stock to stockList 
				//Requires name,ticker,exchange, startDate, description 
				addNewStock(menuInput,stockList);
			
			}else if(choice == 5) {
				//Removes stock from stockList
				removeCompany(menuInput,stockList);
			
			}else if(choice == 6) {
				//Can be sorted (1)A-Z or (2)Z-A 
				sortCompanies(menuInput, stockList);
		
			}else if(choice == 7) {
				
				System.out.println("1) Yes");
				System.out.println("2) No");
				System.out.println("Would you like to save your edits?");
				choice = menuInput.nextInt();
				if(choice == 1) {
					if(save(filename,stockList)) {
						System.out.println("Your edits have been saved to " + filename);
					}else {
						System.out.println("There was an error saving your edits to " + filename);
					}
				}
				System.out.print("Thank you for using my program!");
				menuInput.close();
				System.exit(0);
				menuInput.close();

			}
		}	
	}

	//Reads file into buffer 
	//Buffer appends to new string line
	//Line added to back of StringBuilder
	//Final StringBuilder product returned 
	private static String parseJsonFile(String filename) throws IOException{
		StringBuilder stockArrayBuilder = new StringBuilder();
		try(BufferedReader buff = new BufferedReader(new FileReader(filename))){
			String line;
			while((line = buff.readLine()) != null) {
				stockArrayBuilder.append(line);
			}
		}
		return stockArrayBuilder.toString();

	}
	
	// Validation function
	//Checks to insure json is labeled data 
	//Checks to insure each json object has name,ticker,startDate,exchange,startDate
	//Returns false else 
	private static Boolean isValidJson(String parsedJson) throws ImproperFormat{
		
		Gson gson = new Gson();
		try {
			Stocks_List stock_list = gson.fromJson(parsedJson, Stocks_List.class);
			if(!parsedJson.trim().startsWith("{") || !parsedJson.trim().endsWith("}")) {
				return false;
			}else if(!parsedJson.contains(" \"data\": [")){
				return false;
			}else if(stock_list == null || stock_list.getAllCompanies() == null) {
				return false;
			}
			
			//Broken into separate if else to increase readability 
			for(Stocks company : stock_list.getAllCompanies()) {
				//name
				if(company.getName() == null || company.getName().isEmpty()) {
					throw new ImproperFormat("Improper format ","name");
				//description
				}else if (company.getDescription() == null || company.getDescription().isEmpty()) {
					throw new ImproperFormat("Improper format "," description");
				//exchangeCode
				}else if(company.getExchangeCode() == null || company.getExchangeCode().isEmpty()) {
					throw new ImproperFormat("Improper format "," exchange code");
				//Ticker
				}else if(company.getTicker() == null || company.getTicker().isEmpty()) {
					throw new ImproperFormat("Improper format "," ticker ");
				//startDate
				}else if(company.getStartDate() == null || company.getStartDate().isEmpty() || !isValidDate(company.getStartDate())) {
					throw new ImproperFormat("Improper format "," date");
				}
			}
		}
		catch(JsonSyntaxException e) {
			return false;
		}
		return true;
	}

	private static void displayAllStocks(Stocks_List stock_list) {
		List<Stocks> allCompanies = stock_list.getAllCompanies();
		allCompanies.forEach(company->{ 
			company.printStockFull();
		});
	}

	private static void searchByTicker(Scanner scanner,Stocks_List stock_list) {
		System.out.print("What is the ticker of the company you would like to search for?");
		String ticker = scanner.next();
		List<Stocks> foundCompanies = stock_list.searchByTicker(ticker);
		foundCompanies.forEach(company->{
			String name = company.getName();
			String startDate = company.getStartDate();
			String exchange = company.getExchangeCode()	;
			
			System.out.print(name + ", symbol " + ", started on" + startDate 
					+ ", listed on " + exchange);
		});
	}
	
	private static void searchForExchange(Scanner scanner,Stocks_List stock_list) {
		System.out.print("What Stock Exchange would you like to search for?");
		String exchange = scanner.next();
		
		//As per assignmet documentation only NASDAQ and NYSE are recognized as valid exchanges
		if(!exchange.equalsIgnoreCase("NASDAQ") && !exchange.equalsIgnoreCase("NYSE")) {
			System.out.println("No exchange named " + exchange + " found.");
		}else {
			List<Stocks> stocks = stock_list.searchByExchange(exchange);
			if(stocks.isEmpty()) {
				System.out.println("No companies found on the " + exchange + " exchange");
			}else {
				String foundResults = stocks.stream()
                        .map(stock -> stock.getTicker()) 
                        .collect(Collectors.joining(", ")); // Joining the tickers with a comma and a space

				System.out.println(foundResults + " found on the " + exchange + " exchange.");
			}
		}
		
	}
	private static void addNewStock(Scanner scanner, Stocks_List stock_list) {
		
		String name,ticker,description,startDate,exchangeCode;
		
		System.out.println("What is the name of the company you would like to add? ");
		name = scanner.next();
		
		System.out.println("Enter ticker symbol");
		ticker = scanner.next();
		
		System.out.println("Enter stock description: ");
		description = scanner.next();
		
		System.out.println("Enter start date (YYYY-MM-DD:");
		startDate = scanner.next();
		boolean validDate = isValidDate(startDate);
		while(!validDate) {
			System.out.println("Date is in wrong format");
			System.out.println("Enter start date (YYYY-MM-DD:");
			startDate = scanner.next();
			validDate = isValidDate(startDate);
		}
		
		
		System.out.println("Enter exchange code (NASDAQ/NYSE)");
		exchangeCode = scanner.next().toUpperCase();
		while(!exchangeCode.equals("NASDAQ") && !exchangeCode.equals("NYSE")) {
			System.out.println("Only NASDAQ or NYSE accepted as valid exchange");
			System.out.println("Enter exchange code (NASDAQ/NYSE)");
			exchangeCode = scanner.next().toUpperCase();
			
		}
		
		Stocks newStock = new Stocks(
				name,
				ticker,
				description,
				startDate, 
				exchangeCode);
		stock_list.addCompany(newStock);
		System.out.println("There is now a new entry for:");
		newStock.printStockFull();
	}
	
	private static void removeCompany(Scanner scanner,Stocks_List stock_list) {
	    System.out.print("Enter ticker of company to remove: ");
	    String ticker = scanner.next();
	    stock_list.removeCompany(ticker);
	}
	
	
	private static void sortCompanies(Scanner scanner, Stocks_List stock_list) {
		
		int choice = 0;
		while(choice != 1 && choice != 2) {
			System.out.println("1) A to Z");
			System.out.println("2) Z to A");
			choice = scanner.nextInt();
		}
		
		if(choice == 1) {
			stock_list.sortAlphabeticallyAtoZ();
			System.out.println("Your companies are now sorted from in alphabetical order (A-Z).");
		}else {
			
			stock_list.sortAlphabeticallyZtoA();
			System.out.println("Your companies are now sorted from in alphabetical order (Z-A).");

			
			
		}
	}
	
	//Throws a ParseException from SimpleDateFormat that returns false if date does not follow 
	//YYYY-MM-DD 
	//*MM must be 0 < MM < 13, DD must be 0 < DD < 31*
	private static boolean isValidDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-mm-dd");
		dateFormat.setLenient(false);
		try { 
			dateFormat.parse(date);
			String[] brokenUpDate = date.split("-");
			int month = Integer.parseInt(brokenUpDate[1]);
			int day = Integer.parseInt(brokenUpDate[2]);
			int year = Integer.parseInt(brokenUpDate[0]);
			
			if(year > 2024 || year <= 0) {
				return false;
			}else if(month > 12 || month <= 0) {
				return false;
			}else if(day > 31 || day <= 0) {
				return false;
			}
			return true;
		}
		catch(ParseException e) { //If exception was thrown date must be incorrectly formatted
			return false;
		}
		finally {
			
		}
		
	}
	
	//reverses the process of drawing from the JSON
	private static boolean save(String filename,Stocks_List stock_list)
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String newJsonFile = gson.toJson(stock_list);
		
		  try (FileWriter writer = new FileWriter(filename)) {
		        writer.write(newJsonFile);
		    } catch (IOException e) {
		        e.getMessage();
		    }
		
		return true;
		
	}
}
	


