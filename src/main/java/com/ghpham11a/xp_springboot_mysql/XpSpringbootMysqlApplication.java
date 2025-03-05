package com.ghpham11a.xp_springboot_mysql;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${spring.datasource.url}")
	public String url;

	@Value("${spring.datasource.username}")
	public String userName;

	@Value("${spring.datasource.password}")
	public String password;

	/**
	 * Simple runner that tests the Oracle DB connection on startup.
	 */
	@Bean
	CommandLineRunner testOracleConnection(DataSource dataSource) {
		System.out.println("password " + password);
		System.out.println("userName " + userName);
		System.out.println("url " + url);
		return args -> {
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
		};
	}

}
