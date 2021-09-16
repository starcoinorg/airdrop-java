package org.starcoin.airdrop.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeUtils {

    public static long getBeijingLocalDataTimeMilliseconds(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        Instant instant = localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant();
        return instant.toEpochMilli();
    }

    public static Date dateFromEpochMilliseconds(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return Date.from(instant);
    }

}
