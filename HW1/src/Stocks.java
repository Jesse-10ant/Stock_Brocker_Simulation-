import java.util.StringTokenizer;

public class Stocks {
	private String name;
	private String ticker;
	private String startDate;
	private String description;
	private String exchangeCode;
	
	//Stock constructor 
	public Stocks(String name2, String ticker2, String description2, String startDate2, String exchangeCode2) {
		name = name2;
		ticker = ticker2;
		description = description2;
		startDate = startDate2;
		exchangeCode = exchangeCode2;
		
	}
	
	//Getters and Setters
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getExchangeCode() {
		return exchangeCode;
	}
	public void setExchangeCode(String exchangeCode) {
		this.exchangeCode = exchangeCode;
	}
	public void printStockFull() {
		String name = getName();
		String startDate = getStartDate();
		String ticker = getTicker();
		String exchange = getExchangeCode()	;
		String description = getDescription();
		
		System.out.println(name + ", symbol " + ticker + ", started on "
				+ startDate + ", listed on " + exchange + ", "); 
		
		//Wasn't sure if we were allowed to use Apache word wrap 
		//Created a function to essentially track the number of words printed 
		//All lines for description are indented and have a max width of 8 words
		//Uses String Tokenizer to track number of words printed releasing one at a time 
		//Once count reaches 8 adds a newline character and indents new line 
		StringTokenizer strTok = new StringTokenizer(description);
		int maxWidth = 8;
		int wordCount = 0;
        StringBuilder formattedString = new StringBuilder("     "); 
		 while (strTok.hasMoreTokens()) {
	            String word = strTok.nextToken();
	            formattedString.append(word).append(" ");
	            wordCount++;
	            if (wordCount >= maxWidth) {
	                formattedString.append("\n     ");
	                wordCount = 0;
	            }
	        }
	        System.out.println(formattedString.toString());
		
	}
}

