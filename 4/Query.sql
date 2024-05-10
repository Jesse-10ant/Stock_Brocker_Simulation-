CREATE DATABASE STOCKS;

USE STOCKS;

CREATE TABLE registered_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    balance FLOAT
);

CREATE TABLE portfolio (
    trade_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    ticker VARCHAR(10),
    numStock INT,
    price FLOAT,
    FOREIGN KEY (user_id) REFERENCES registered_users(id)
);
