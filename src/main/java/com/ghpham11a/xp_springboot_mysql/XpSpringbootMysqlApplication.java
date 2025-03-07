package com.ghpham11a.xp_springboot_mysql;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class XpSpringbootMysqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(XpSpringbootMysqlApplication.class, args);
	}

	/**
	 * Simple runner that tests the Oracle DB connection on startup.
	 */
	@Bean
	CommandLineRunner testConnections(DataSource dataSource,
									  RedisTemplate<String, Object> redisTemplate,
									  KafkaTemplate<String, Object> kafkaTemplate) {
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
				var redisConn = redisTemplate.getConnectionFactory().getConnection();
				String result = redisConn.ping() == null ? null : new String(redisConn.ping());
				if ("PONG".equals(result)) {
					System.out.println("SUCCESS: Connected to Redis! PING -> " + result);
				} else {
					System.out.println("WARNING: Redis PING returned: " + result);
				}
				redisConn.close();
			} catch (Exception ex) {
				System.err.println("ERROR: Failed to connect to Redis.");
				ex.printStackTrace();
			}

			// Test Kafka
			System.out.println("Attempting to send a test message to Kafka...");
			try {
				// Using 'healthcheck-topic' as an example; make sure this topic exists or is auto-created.
				var future = kafkaTemplate.send("accounts-topic", "Health check message!");

				// Wait up to 5 seconds for send to complete
				var sendResult = future.get(5, TimeUnit.SECONDS);
				RecordMetadata metadata = sendResult.getRecordMetadata();
				System.out.printf("SUCCESS: Sent test message to Kafka topic=%s partition=%d offset=%d%n",
						metadata.topic(), metadata.partition(), metadata.offset());
			} catch (Exception ex) {
				System.err.println("ERROR: Failed to send message to Kafka.");
				ex.printStackTrace();
			}
		};
	}

}
