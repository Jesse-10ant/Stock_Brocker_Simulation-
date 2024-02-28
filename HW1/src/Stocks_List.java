import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Stocks_List {
    private List<Stocks> data;

    public Stocks_List() {
    	data = new ArrayList<>();
    }

    public void addCompany(Stocks company) {
    	data.add(company);
    }

    public void removeCompany(String ticker) {
    	data.removeIf(company -> company.getTicker().equalsIgnoreCase(ticker));
    }

    public List<Stocks> searchByTicker(String ticker) {
        return data.stream()
                .filter(company -> company.getTicker().equalsIgnoreCase(ticker))
                .collect(Collectors.toList());
    }

    public List<Stocks> searchByExchange(String exchangeCode) {
        return data.stream()
                .filter(company -> company.getExchangeCode().equalsIgnoreCase(exchangeCode))
                .collect(Collectors.toList());
    }

    public List<Stocks> getAllCompanies() {
        return new ArrayList<>(data);
    }
    
    public void sortAlphabeticallyAtoZ() {
    	data.sort(Comparator.comparing(Stocks::getName));
    }

    public void sortAlphabeticallyZtoA() {
    	data.sort(Comparator.comparing(Stocks::getName).reversed());
    }
}
