package Servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import DAO.UserDAO;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;



@WebServlet("/Register")
public class Register extends HttpServlet {

	private static final long serialVersionUID = 1L;

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

		 response.setContentType("application/json");
	     response.setCharacterEncoding("UTF-8");
	     UserDAO dao = new UserDAO();   
	     User user = new User();
	        JsonObject json = new JsonObject();
	        try (BufferedReader reader = request.getReader()) {
	            JsonObject requestData = JsonParser.parseReader(reader).getAsJsonObject();
	            String username = requestData.get("username").getAsString();
	            String password = requestData.get("password").getAsString();
	            String email = requestData.get("email").getAsString();
	            user.setBalance(5000.0f);
	            user.setEmail(email);
	            user.setUsername(username);
	            user.setPassword(password);
	            
	            if(dao.usernameTaken(username)) {
	            	 json.addProperty("success", false);
		                json.addProperty("message", "Username take");
	            }
	            else if (dao.createUser(user)) {
	                json.addProperty("success", true);
	                json.addProperty("message", "Registration successful.");
	            } else {
	                json.addProperty("success", false);
	                json.addProperty("message", "Registration failed.");
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