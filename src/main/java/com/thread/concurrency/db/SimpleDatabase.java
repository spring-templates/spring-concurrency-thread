package com.thread.concurrency.db;


import org.springframework.stereotype.Component;

import java.util.HashMap;

import static java.lang.Math.random;

@Component
public final class SimpleDatabase {
    private final HashMap<Long, Account> db = new HashMap<>();

    Account balance(long id, long balance) {
        try {
            Thread.sleep((long) (random() * 300L + 100));
        } catch (InterruptedException ignored) {
        }
        var account = new Account(balance, System.currentTimeMillis(), System.nanoTime());
        db.put(id, account);
        return account;
    }

    Account balance(long id) {
        try {
            Thread.sleep((long) (random() * 300L + 100));
        } catch (InterruptedException ignored) {
        }
        var account = db.get(id);
        return account != null ? account : new Account(0, System.currentTimeMillis(), System.nanoTime());
    }
}
