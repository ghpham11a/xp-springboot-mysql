package com.ghpham11a.xp_springboot_mysql.services;

import com.ghpham11a.xp_springboot_mysql.models.Account;

import java.util.List;
import java.util.Optional;

public interface AccountsService {
    List<Account> getAllAccounts();

    Optional<Account> getAccountById(int id);

    int createAccount(Account account);

    int updateAccount(Account account);

    int deleteAccount(int id);
}
