package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.StockModel;

public class TradeDAO {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/STOCKS?useSSL=false";
	private static final String USER = "root";
	private static final String PASS = "Resolute620";

	// Insert the new user into the database registeredUsers
	public boolean performTrade(String user_id, String tick, int quantity, float price) throws SQLException {
		UserDAO dao = new UserDAO();
		int id = dao.getUserID(user_id);
		String script = "INSERT INTO portfolio (user_id,ticker,numStock,price)VALUES (?,?,?,?)";
		try (Connection conn = establishConnection()) {
			PreparedStatement ps = conn.prepareStatement(script);
			ps.setInt(1, id);
			ps.setString(2, tick);
			ps.setInt(3, quantity);
			ps.setFloat(4, price);
			int changed = ps.executeUpdate();
			return changed > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Get how many is owned of a certain stock
	public int getStockAmount(String user_id, String ticker) {
		UserDAO dao = new UserDAO();
		int id = dao.getUserID(user_id);
		String query = "SELECT SUM(numStock) AS total_stocks FROM portfolio WHERE user_id = ? AND ticker = ?";
		int total_stocks = 0;
		try (Connection conn = establishConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, id);
			pstmt.setString(2, ticker);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					total_stocks += rs.getInt("total_stocks");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return total_stocks;
	}

	// Get how much has been spent on a certain stock
	public double getTotalCost(String user_id, String ticker) {
		UserDAO dao = new UserDAO();
		int id = dao.getUserID(user_id);

		double total = 0.0;
		String query = "SELECT SUM(numStock * price) AS total FROM portfolio WHERE user_id = ? AND ticker = ?";

		try (Connection conn = establishConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, id);
			pstmt.setString(2, ticker);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					total = rs.getDouble("total");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace(); 
		}

		return total;
	}

	public List<StockModel> getAllStocksForUser(String user_id) {
		List<StockModel> stocks = new ArrayList<>();
		UserDAO dao = new UserDAO();
		int id = dao.getUserID(user_id);
		String query = "SELECT ticker, SUM(numStock) AS total_stocks, AVG(price) AS average_price FROM portfolio WHERE user_id = ? GROUP BY ticker";

		try (Connection conn = establishConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setInt(1, id);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
				    String ticker = rs.getString("ticker");
				    int numStock = rs.getInt("total_stocks"); 
				    double price = rs.getDouble("average_price"); 
				    StockModel stock = new StockModel(ticker, numStock, price);
				    stocks.add(stock);
	                System.out.println("In DAO");
				    System.out.println(stock.getAvgPrice());
				    System.out.println(stock.getNumStock());
				    System.out.println(stock.getTicker());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return stocks;
	}

	private Connection establishConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(DB_URL, USER, PASS);
	}
}
