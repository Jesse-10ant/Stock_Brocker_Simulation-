package Servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import DAO.UserDAO;
import model.User;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        UserDAO dao = new UserDAO();
	     
	        JsonObject json = new JsonObject();
	        try (BufferedReader reader = request.getReader()) {
	            JsonObject requestData = JsonParser.parseReader(reader).getAsJsonObject();
	            String username = requestData.get("username").getAsString();
	            String password = requestData.get("password").getAsString(); 
	            
	            if (dao.credentialsCheck(username, password)) {
	            	User user = dao.getUser(username);
	            	HttpSession session = request.getSession();
	            	session.setAttribute("loggedUser", user);
	                json.addProperty("success", true);
	                json.addProperty("userID", user.getUsername());
	                json.addProperty("message", "Login successful.");
	            } else {
	                json.addProperty("success", false);
	                json.addProperty("message", "Invalid username or password.");
	            }
	        } catch (Exception e) {
	            json.addProperty("success", false);
	            json.addProperty("message", "Server error: " + e.getMessage());
	        }

	        try (PrintWriter out = response.getWriter()) {
	            out.print(json.toString());
	        }
	    }
}