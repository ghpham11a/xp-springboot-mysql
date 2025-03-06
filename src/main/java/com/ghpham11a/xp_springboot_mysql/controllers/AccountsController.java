package com.ghpham11a.xp_springboot_mysql.controllers;

import com.ghpham11a.xp_springboot_mysql.models.Account;
import com.ghpham11a.xp_springboot_mysql.services.AccountsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountsController {

    private final AccountsService accountsService;

    public AccountsController(AccountsService accountService) {
        this.accountsService = accountService;
    }

    // GET all
    @GetMapping
    public ResponseEntity<List<Account>> getAll() {
        return ResponseEntity.ok(accountsService.getAllAccounts());
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable int id) {
        Optional<Account> accountOpt = accountsService.getAccountById(id);
        return accountOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public ResponseEntity<String> create(@RequestBody Account account) {
        int rowsAffected = accountsService.createAccount(account);
        if (rowsAffected > 0) {
            return ResponseEntity.ok("Account created successfully!");
        }
        return ResponseEntity.badRequest().body("Failed to create account.");
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody Account updatedAccount) {
        updatedAccount.setId(id);
        int rowsAffected = accountsService.updateAccount(updatedAccount);
        if (rowsAffected > 0) {
            return ResponseEntity.ok("Account updated successfully!");
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        int rowsAffected = accountsService.deleteAccount(id);
        if (rowsAffected > 0) {
            return ResponseEntity.ok("Account deleted successfully!");
        }
        return ResponseEntity.notFound().build();
    }
}
