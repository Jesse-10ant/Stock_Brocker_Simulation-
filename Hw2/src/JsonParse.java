import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import com.google.gson.Gson;

public class JsonParse {


    //**LIFTED FROM ASSIGMENT 1 AS PER ASSIGNMENT DETAIL**//
    //Reads file into buffer 
    //Buffer appends to new string line
    //Line added to back of StringBuilder
    //Final StringBuilder product returned 
    static String parseJsonFile(String filename) throws IOException {
        StringBuilder company_builder = new StringBuilder();
        try (BufferedReader buff = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = buff.readLine()) != null) {
                company_builder.append(line);
            }
        } catch (IOException e) {
            e.getMessage();

        }
        return company_builder.toString();

    }

    static Boolean isValidJson(String parsedJson) throws ImproperFormat {
        Gson gson = new Gson();
        try {
            CompanyList companines = gson.fromJson(parsedJson, CompanyList.class);
            if (!parsedJson.trim().startsWith("{") || !parsedJson.trim().endsWith("}")) {
                return false;
            } else if (!parsedJson.contains(" \"data\": [")) {
                return false;
            } else if (companines == null) {
                return false;
            }

            //Broken into separate if else to increase readability 
            for (int i = 0; i < companines.getData().size(); i++) {
                Company company = companines.getData().get(i);
                if (company.getTicker() == null || company.getTicker().isEmpty()) {
                    throw new ImproperFormat("Improper format ticker ");
                }
                if (company.getBrokers() <= 0) {
                    throw new ImproperFormat("Improper format brokers ");
                }
            }

        } catch (Exception e) {
            return false;
        } finally {}
        return true;
    }
}

class ImproperFormat extends RuntimeException {
    private static final long serialVersionUID = 1;
    public ImproperFormat(String message) {
        super(message);
    }


}