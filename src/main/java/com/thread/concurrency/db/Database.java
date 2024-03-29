package com.thread.concurrency.db;

public interface Database {
    // deposit balance: 일급 함수 스타일
    Account balance(long id, long balance);

    // initial balance
    Account balance(long id);
}
