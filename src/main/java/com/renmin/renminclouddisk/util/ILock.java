package com.renmin.renminclouddisk.util;

public interface ILock {
    boolean tryLock(long timeoutSec);
    void unlock();
}
