package com.ghpham11a.xp_springboot_mysql.models;

import java.util.Optional;

public class AccountFetchResult {

    private final Optional<Account> account;
    private final boolean fromRedis;

    public AccountFetchResult(Optional<Account> account, boolean fromRedis) {
        this.account = account;
        this.fromRedis = fromRedis;
    }

    public Optional<Account> getAccount() {
        return account;
    }

    public boolean isFromRedis() {
        return fromRedis;
    }
}
