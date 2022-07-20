package by.urbel.docgenerator.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Random;

public class Randomizer {
    public static Random random = new Random();

    public static int getInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static String getStrNumber(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(9));
        }
        return builder.toString();
    }

    public static LocalDate getDate() {
        int month = getInt(1, 12);
        int maxDays = month == 2 ? 28 : 30;
        int day = getInt(1, maxDays);
        return LocalDate.of(
                LocalDate.now().getYear() - getInt(1, 2),
                month,
                day
        );
    }

    public static LocalDate getDate(int minYear) {
        int month = getInt(1, 12);
        int maxDays = month == 2 ? 28 : 30;
        int day = getInt(1, maxDays);
        int year = getInt(minYear, LocalDate.now().getYear() - 16);
        return LocalDate.of(
                year,
                month,
                day
        );
    }

    public static LocalDate getDate(LocalDate from, int plusMonth, int plusDays) {
        return from.plus(Period.of(
                0,
                plusMonth,
                plusDays
        ));
    }

    public static <T> T getItem(List<T> list) {
        if (!list.isEmpty()) {
            return list.get(random.nextInt(list.size()));
        } else {
            throw new RuntimeException("Provided list is empty");
        }
    }
}
