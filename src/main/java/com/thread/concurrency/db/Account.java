package com.thread.concurrency.db;

public record Account(long balance, long updateMilli, long updateNano) {
}
