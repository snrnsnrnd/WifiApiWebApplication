package com.jspark.zerobase_project01;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTime {

    public static String getNow() {
        return LocalDate.now().toString() + "T" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
