package com.otd.configuration.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Getter
@AllArgsConstructor
@Setter
public class FormattedTime {
    public static String formatMinutes(double minutes) {
        if (minutes <= 0) return "0분";

        int hours = (int)minutes / 60;
        int mins = (int)minutes % 60;

        if (hours > 0 && mins > 0) {
            return hours + "시간 " + mins + "분";
        } else if (hours > 0) {
            return hours + "시간";
        } else {
            return mins + "분";
        }
    }
    public static int fetchYear() {
        LocalDate localDate = LocalDate.now();
        return localDate.getYear();
    }
    public static int fetchMonth() {
        LocalDate localDate = LocalDate.now();
        return localDate.getMonthValue();
    }
    public static int fetchDay() {
        LocalDate localDate = LocalDate.now();
        LocalDate monday = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return monday.getDayOfMonth() - 1;
    }
}
