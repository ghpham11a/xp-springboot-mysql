package com.ghpham11a.xp_springboot_mysql.services;

import com.ghpham11a.xp_springboot_mysql.models.Account;
import com.ghpham11a.xp_springboot_mysql.repositories.AccountsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrimaryAccountsService implements AccountsService {

    private final AccountsRepository accountRepository;

    public PrimaryAccountsService(AccountsRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(int id) {
        return accountRepository.findById(id);
    }

    public int createAccount(Account account) {
        return accountRepository.save(account);
    }

    public int updateAccount(Account account) {
        // Could also add business logic/checks here before updating
        return accountRepository.update(account);
    }

    public int deleteAccount(int id) {
        return accountRepository.deleteById(id);
    }
}
