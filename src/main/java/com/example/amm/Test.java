package com.example.amm;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.example.amm.constant.BusinessType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Test {

    public static void main(String[] args) {
        String dateStr = "2023-04-16T09:23:56";
        DateTime dt = DateUtil.parse(dateStr);

        // Date对象转换为LocalDateTime
        LocalDateTime time = LocalDateTimeUtil.of(dt);
        Duration between = LocalDateTimeUtil.between(time, LocalDateTimeUtil.now());
        System.out.println(24 * 3600);

        System.out.println(between.getSeconds());

        System.out.println(BusinessType.AUTO_TASK);
    }
}
