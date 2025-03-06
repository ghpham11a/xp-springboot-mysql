package com.ghpham11a.xp_springboot_mysql.repositories;

import com.ghpham11a.xp_springboot_mysql.models.Account;

import java.util.List;
import java.util.Optional;

public interface AccountsRepository {
    int save(Account account);
    List<Account> findAll();
    Optional<Account> findById(int id);
    int update(Account account);
    int deleteById(int id);
}
