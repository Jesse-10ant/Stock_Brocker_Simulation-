package Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import DAO.TradeDAO;

/**
 * Servlet implementation class StockInfo
 */
@WebServlet("/StockInfo")
public class StockInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StockInfo() {
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
		        String ticker = requestData.get("ticker").getAsString();
		        String user_id = requestData.get("user_id").getAsString();
		       double total_spent = dao.getTotalCost(user_id, ticker);
		       int  number_owned = dao.getStockAmount(user_id, ticker);
		       json.addProperty("total_spent", total_spent);
		       json.addProperty("number_owned", number_owned);
		 }catch(Exception e) {
			 e.printStackTrace();
			 json.addProperty("success", false);
		     json.addProperty("message", "An error occurred: " + e.getMessage());
		 }
		 response.setContentType("application/json");
		    try (PrintWriter out = response.getWriter()) {
		        out.print(json.toString());
		    }
	}

}
