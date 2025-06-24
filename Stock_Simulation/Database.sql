CREATE TABLE `registered_users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(256) DEFAULT NULL,
  `balance` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `emaial` (`email`)
)

CREATE TABLE `portfolio` (
  `trade_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `ticker` varchar(255) DEFAULT NULL,
  `numStock` int DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`trade_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `portfolio_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `registered_users` (`id`)
)