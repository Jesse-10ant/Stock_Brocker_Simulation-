This project simulates a basic stock trading platform where users can register, log in, and manage a virtual stock portfolio. It combines a Java-based backend with a modern web frontend and integrates live stock data from an external API.

Overview
Users can:

Register and authenticate

Simulate buying and selling stocks

View portfolio performance and trade history

See real-time stock prices

Technologies Used
Frontend: HTML, CSS, JavaScript

Backend: Java Servlets (JSP)

Database: MySQL

External API: Finnhub for live stock quotes

Libraries:

Gson (for JSON parsing)

MySQL JDBC Driver

How It Works
The backend stores user data and processes trade logic.

The frontend sends and receives data via HTTP (Servlets).

Live stock quotes are fetched client-side using Finnhub's public API.

Setup
Import Database.sql into MySQL.

Configure and run the project in a servlet container (Tomcat).

Open the app in your browser via localhost.

