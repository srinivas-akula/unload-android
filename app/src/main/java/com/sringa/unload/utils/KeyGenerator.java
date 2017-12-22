package com.sringa.unload.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public final class KeyGenerator {

    private static int nextId_ = 0;
    private static final int maxId_ = 100000;
    private static NumberFormat numberFormat_;
    private static DateFormat dateFormat_;

    static {
        numberFormat_ = new DecimalFormat("00000");
        dateFormat_ = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    }

    private KeyGenerator() {
        throw new AssertionError("Static utility class.");
    }

    public static synchronized String generateKey() {
        String sb = (numberFormat_.format(nextId_));
        nextId_ = (nextId_ + 1) % maxId_;
        return dateFormat_.format(new java.util.Date()) + sb;
    }
}
