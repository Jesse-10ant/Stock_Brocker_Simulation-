package Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import DAO.TradeDAO;
import model.StockModel;

/**
 * Servlet implementation class UserStocks
 */
@WebServlet("/UserStocks")
public class UserStocks extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserStocks() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TradeDAO dao = new TradeDAO();
		JsonObject json = new JsonObject();
		 try (BufferedReader reader = request.getReader()) {
		        JsonObject requestData = JsonParser.parseReader(reader).getAsJsonObject();
		        String userID = requestData.get("userID").getAsString();
		        List<StockModel> usersStocks = dao.getAllStocksForUser(userID);
		        if (usersStocks != null && !usersStocks.isEmpty()) {
		        	JsonArray stockArray = new JsonArray();
		            for (StockModel stock : usersStocks) {
		                JsonObject stockJson = new JsonObject();
		                System.out.println("In servlet");
		                System.out.println(stock.getTicker());
		                System.out.println(stock.getNumStock());
		                System.out.println(stock.getPrice());

		                stockJson.addProperty("ticker", stock.getTicker());
		                stockJson.addProperty("numStock", stock.getNumStock());
		                stockJson.addProperty("price", stock.getPrice());
		                stockArray.add(stockJson);
		            }
		            json.addProperty("success", true);
		            json.add("stocks", stockArray);
		            json.addProperty("message", "Stocks retrieved successfully.");
		        } else {
		            json.addProperty("success", false);
		            json.addProperty("message", "No stocks found");
		        }
		    } catch (Exception e) {
		        json.addProperty("success", false);
		        json.addProperty("message", "Server error: " + e.getMessage());
		        e.printStackTrace();
		    }

		  response.setContentType("application/json");
		    try (PrintWriter out = response.getWriter()) {
		        out.print(json.toString()); // Convert JsonObject to String
		        out.flush();
		    }
		}
}