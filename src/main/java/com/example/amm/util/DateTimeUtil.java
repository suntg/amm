package com.example.amm.util;

import java.time.Duration;
import java.time.LocalDateTime;

import cn.hutool.core.date.LocalDateTimeUtil;

public class DateTimeUtil {

    public static boolean diffTime(LocalDateTime time, int diff) {
        Duration between = LocalDateTimeUtil.between(time, LocalDateTimeUtil.now());
        return between.getSeconds() > diff;
    }

}
