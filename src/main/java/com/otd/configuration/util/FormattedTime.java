package com.otd.configuration.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
}
