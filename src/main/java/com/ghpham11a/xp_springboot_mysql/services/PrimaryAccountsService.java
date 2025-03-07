package com.ghpham11a.xp_springboot_mysql.services;

import com.ghpham11a.xp_springboot_mysql.models.Account;
import com.ghpham11a.xp_springboot_mysql.models.AccountFetchResult;
import com.ghpham11a.xp_springboot_mysql.repositories.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PrimaryAccountsService implements AccountsService {

    private final AccountsRepository accountRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PrimaryAccountsService(
            AccountsRepository accountRepository,
            RedisTemplate<String, Object> redisTemplate,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.accountRepository = accountRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Account> getAllAccounts() {
        // For "getAll", we might not always cache the entire list
        // (especially if it’s large). But you could if you wanted.
        return accountRepository.findAll();
    }

    public AccountFetchResult getAccountById(int id) {
        String key = "account:" + id;
        Account cachedAccount = (Account) redisTemplate.opsForValue().get(key);

        // 1) If found in Redis, return result with flag = true
        if (cachedAccount != null) {
            return new AccountFetchResult(Optional.of(cachedAccount), true);
        }

        // 2) Otherwise, fetch from DB
        Optional<Account> dbAccount = accountRepository.findById(id);

        // 3) Cache and return
        dbAccount.ifPresent(account ->
                redisTemplate.opsForValue().set(key, account, 10, TimeUnit.MINUTES)
        );

        return new AccountFetchResult(dbAccount, false);
    }

    public int createAccount(Account account) {
        // 1. Save to DB
        int id = accountRepository.save(account);

        // 2. Cache it in Redis
        String key = "account:" + id;
        redisTemplate.opsForValue().set(key, account, 10, TimeUnit.MINUTES);

        // 3. Publish an event/message to Kafka
        //    e.g., "account_created" topic or something similar
        kafkaTemplate.send("account_created", String.valueOf(id), account);

        return id;
    }

    public int updateAccount(Account account) {
        // 1. Update in DB
        int rows = accountRepository.update(account);
        if (rows > 0) {
            // 2. Update in Redis
            String key = "account:" + account.getId();
            redisTemplate.opsForValue().set(key, account, 10, TimeUnit.MINUTES);

            // 3. Publish update event
            kafkaTemplate.send("account_updated", String.valueOf(account.getId()), account);
        }
        return rows;
    }

    public int deleteAccount(int id) {
        // 1. Delete from DB
        int rows = accountRepository.deleteById(id);

        if (rows > 0) {
            // 2. Remove from Redis
            String key = "account:" + id;
            redisTemplate.delete(key);

            // 3. Publish deletion event
            kafkaTemplate.send("account_deleted", String.valueOf(id), "Deleted");
        }

        return rows;
    }
}
