package com.ghpham11a.xp_springboot_mysql.repositories;

import com.ghpham11a.xp_springboot_mysql.models.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class PrimaryAccountsRepository implements AccountsRepository {

    private final JdbcTemplate jdbcTemplate;

    // You can inject JdbcTemplate via constructor injection
    public PrimaryAccountsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to convert each row of ResultSet to an Account object
    private final RowMapper<Account> accountRowMapper = new RowMapper<Account>() {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            Account account = new Account();
            account.setId(rs.getInt("Id"));
            account.setEmail(rs.getString("Email"));

            // Safely convert SQL DATE to LocalDate
            java.sql.Date dob = rs.getDate("DateOfBirth");
            if (dob != null) {
                account.setDateOfBirth(dob.toLocalDate());
            }

            account.setAccountNumber(rs.getString("AccountNumber"));
            account.setBalance(rs.getBigDecimal("Balance"));

            // Convert SQL DATETIME/TIMESTAMP to LocalDateTime
            java.sql.Timestamp createdTs = rs.getTimestamp("CreatedAt");
            if (createdTs != null) {
                account.setCreatedAt(createdTs.toLocalDateTime());
            }

            return account;
        }
    };

    /**
     * Create (INSERT) a new account
     * - Let the DB handle the CreatedAt (default CURRENT_TIMESTAMP)
     */
    public int save(Account account) {
        String sql = "INSERT INTO Accounts (Email, DateOfBirth, AccountNumber, Balance) " +
                "VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                account.getEmail(),
                account.getDateOfBirth(), // LocalDate (JDBC will handle as Date)
                account.getAccountNumber(),
                account.getBalance());
    }

    /**
     * Read all (SELECT) accounts
     */
    public List<Account> findAll() {
        String sql = "SELECT Id, Email, DateOfBirth, AccountNumber, Balance, CreatedAt FROM Accounts";
        return jdbcTemplate.query(sql, accountRowMapper);
    }

    /**
     * Read single account by ID
     */
    public Optional<Account> findById(int id) {
        String sql = "SELECT Id, Email, DateOfBirth, AccountNumber, Balance, CreatedAt " +
                "FROM Accounts WHERE Id = ?";
        List<Account> results = jdbcTemplate.query(sql, accountRowMapper, id);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }

    /**
     * Update (modify) an existing account
     */
    public int update(Account account) {
        String sql = "UPDATE Accounts " +
                "SET Email = ?, DateOfBirth = ?, AccountNumber = ?, Balance = ? " +
                "WHERE Id = ?";
        return jdbcTemplate.update(sql,
                account.getEmail(),
                account.getDateOfBirth(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getId());
    }

    /**
     * Delete an account by ID
     */
    public int deleteById(int id) {
        String sql = "DELETE FROM Accounts WHERE Id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
