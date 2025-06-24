package Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import DAO.TradeDAO;

/**
 * Servlet implementation class Sell
 */
@WebServlet("/Trade")
public class Trade extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Trade() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TradeDAO dao = new TradeDAO();
		JsonObject json = new JsonObject();
		 try (BufferedReader reader = request.getReader()) {
		        JsonObject requestData = JsonParser.parseReader(reader).getAsJsonObject();
		        String ticker = requestData.get("ticker").getAsString();
		        int amount = requestData.get("quantity").getAsInt();
		        float price = requestData.get("price").getAsFloat();
		        String userID = requestData.get("userID").getAsString();

		        boolean success = dao.performTrade(userID, ticker, amount, price);
		        if (success) {
		            json.addProperty("success", true);
		            json.addProperty("message", "Trade Complete");
		        } else {
		            json.addProperty("success", false);
		            json.addProperty("message", "Trade failed.");
		        }
		    } catch (SQLException e) {
			e.printStackTrace();
		}

		try (PrintWriter out = response.getWriter()) {
			out.print(json.toString());
		}

	}
}
