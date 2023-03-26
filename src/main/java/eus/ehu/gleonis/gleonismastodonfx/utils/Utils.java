package eus.ehu.gleonis.gleonismastodonfx.utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class Utils {

    public static Instant ISODateToDate(String date) {
        return Instant.parse(date);
    }

    public static String getDateString(String date)
    {
        Instant instant = ISODateToDate(date);
        LocalDate realDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());
        Period d = Period.between(realDate, LocalDate.now());

        if(d.getYears() >= 1)
            return realDate.getDayOfMonth() + " " + realDate.getMonth().toString() + " " + realDate.getYear();

        if(d.getMonths() >= 1 || d.getDays() > 14)
            return realDate.getDayOfMonth() + " " + realDate.getMonth().toString();

        if(d.getDays() >= 7)
            return (d.getDays() / 7) + "w";

        if(d.getDays() >= 1)
            return d.getDays() + "d";

        Duration duration = Duration.between(instant, Instant.now());
        if(duration.toHours() >= 1)
            return duration.toHours() + "h";

        if(duration.toMinutes() >= 1)
            return duration.toMinutes() + "m";

        return "now";
    }
}
