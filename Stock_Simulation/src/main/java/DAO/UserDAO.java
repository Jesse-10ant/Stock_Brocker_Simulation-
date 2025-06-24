package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;

public class UserDAO {

	private static final String DB_URL = "jdbc:mysql://localhost:3306/STOCKS?useSSL=false";
	private static final String USER = "root";
	private static final String PASS = "Resolute620";

	private Connection establishConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return DriverManager.getConnection(DB_URL, USER, PASS);
	}

	// Insert the new user into the database registeredUsers
	public boolean createUser(User user) throws SQLException {
		String script = "INSERT INTO registered_users (username,password,email)VALUES (?,?,?)";
		try (Connection conn = establishConnection()) {
			PreparedStatement ps = conn.prepareStatement(script);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setString(3, user.getEmail());
			int changed = ps.executeUpdate();
			return changed > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Create User object from users table information
	public User getUser(String userName) {
		String script = "SELECT * FROM registered_users WHERE username = ?";
		try (Connection conn = establishConnection()) {
			PreparedStatement ps = conn.prepareStatement(script);
			ps.setString(1, userName);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				User user = new User();
				user.setUsername(result.getString("username"));
				user.setPassword(result.getString("password"));
				user.setEmail(result.getString("email"));
				user.setBalance(result.getFloat("balance"));
				return user;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	// Check if userName and password check from database
	public boolean credentialsCheck(String userName, String pass) {
		String script = "SELECT * FROM registered_users WHERE username = ?";
		try (Connection conn = establishConnection()) {
			PreparedStatement ps = conn.prepareStatement(script);
			ps.setString(1, userName);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				String password = result.getString("password");
				return password.equals(pass);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Check if username is taken
	public boolean usernameTaken(String userName) {
		String script = "SELECT COUNT(*) AS count FROM registered_users WHERE username = ?";
		try (Connection conn = establishConnection()) {
			PreparedStatement ps = conn.prepareStatement(script);
			ps.setString(1, userName);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				return result.getInt("count") > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Update users balance in the table
	public boolean updateUserBalance(User user, float balance) {
		String script = "UPDATE registered_users SET balance = ? WHERE username = ?";
		try (Connection conn = establishConnection()) {
			PreparedStatement ps = conn.prepareStatement(script);
			ps.setFloat(1, balance);
			ps.setString(2, user.getUsername());
			int changed = ps.executeUpdate();
			return changed > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean performTrade(User user2, String tick, float price, int amount) {

		boolean valid = false;
		float balance = user2.getBalance();

		if (amount < 0) {
			valid = true;
		} else {
			if (balance - (price * Math.abs(amount)) >= 0.0f) {
				valid = true;
			}
		}

		if (valid) {
			float new_balance = balance - (price * Math.abs(amount));
			user2.setBalance(new_balance);
			updateUserBalance(user2, new_balance);
			return true;

		}
		return false;
	}

	public int getUserID(String user_id) {

		int userId = -1; // Initialize to an invalid value to indicate not found
		String query = "SELECT id FROM registered_users WHERE username = ?";

		try (Connection conn = establishConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

			pstmt.setString(1, user_id);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					userId = rs.getInt("id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return userId;
	}
}
