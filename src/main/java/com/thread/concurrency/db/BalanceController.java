package com.thread.concurrency.db;

public interface BalanceController {
    Account balance(long id);

    Account deposit(long id, long amount);

    Account withdraw(long id, long amount);
}
