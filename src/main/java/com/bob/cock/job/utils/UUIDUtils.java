package com.bob.cock.job.utils;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.bob.cock.job.CronExpression;

public final class UUIDUtils {
    public static String randomUUID() {
        UUID uuid = UUID.randomUUID();
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        return (digits(mostSigBits >> 32, 8) +
            digits(mostSigBits >> 16, 4) +
            digits(mostSigBits, 4) +
            digits(leastSigBits >> 48, 4)  +
            digits(leastSigBits, 12));
    }
    
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1).toUpperCase();
    }
    
    private UUIDUtils() {
    }
    
    public static void main(String[] args) throws ParseException {
        
        CronExpression cron = new CronExpression("0/50 * * * * ?");
        System.out.println(TimeUnit.MILLISECONDS.toSeconds(cron.getNextValidTimeAfter(new Date()).getTime()));
        
        System.out.println(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        
    }
}
