package com.ghpham11a.xp_springboot_mysql;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
	CommandLineRunner testOracleConnection(DataSource dataSource) {
		return args -> {
			System.out.println("Attempting to connect to Oracle DB...");

			try (Connection connection = dataSource.getConnection()) {
				if (connection.isValid(5)) {
					System.out.println("SUCCESS: Connection to Oracle DB is valid!");
				} else {
					System.out.println("WARNING: Connection to Oracle DB is not valid.");
				}
			} catch (SQLException ex) {
				System.err.println("ERROR: Failed to connect to Oracle DB.");
				ex.printStackTrace();
			}
		};
	}

}
