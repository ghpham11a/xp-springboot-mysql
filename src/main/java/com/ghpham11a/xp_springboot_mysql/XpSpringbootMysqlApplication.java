package com.ghpham11a.xp_springboot_mysql;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class XpSpringbootMysqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(XpSpringbootMysqlApplication.class, args);
	}

	/**
	 * Simple runner that tests the Oracle DB connection on startup.
	 */
	@Bean
	CommandLineRunner testConnections(DataSource dataSource, RedisTemplate<String, Object> redisTemplate) {
		return args -> {
			// Test MySQL
			System.out.println("Attempting to connect to MySQL DB...");
			try (Connection connection = dataSource.getConnection()) {
				if (connection.isValid(5)) {
					System.out.println("SUCCESS: Connection to MySQL DB is valid!");
				} else {
					System.out.println("WARNING: Connection to MySQL DB is not valid.");
				}
			} catch (SQLException ex) {
				System.err.println("ERROR: Failed to connect to MySQL DB.");
				ex.printStackTrace();
			}

			// Test Redis
			System.out.println("Attempting to connect to Redis...");
			try {
				// Obtain a low-level connection from RedisConnectionFactory
				var connection = redisTemplate.getConnectionFactory().getConnection();
				// Use 'PING' to check if Redis is responding
				String result = connection.ping() == null ? null : new String(connection.ping());

				if ("PONG".equals(result)) {
					System.out.println("SUCCESS: Connected to Redis! PING -> " + result);
				} else {
					System.out.println("WARNING: Redis PING returned: " + result);
				}

				// Always close the connection when you're done
				connection.close();
			} catch (Exception ex) {
				System.err.println("ERROR: Failed to connect to Redis.");
				ex.printStackTrace();
			}
		};
	}

}
