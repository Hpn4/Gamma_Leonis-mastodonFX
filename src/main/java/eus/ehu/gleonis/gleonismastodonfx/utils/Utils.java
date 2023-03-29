package eus.ehu.gleonis.gleonismastodonfx.utils;

import javafx.application.Platform;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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


    @FunctionalInterface
    public interface ProducerWithThrow<R> {
        R apply() throws Throwable;
    }

    /**
     * Create and run an async task using the provided function as the asynchronous operation,
     * and the callback as the success operation. Error are ignored and returned as null values.
     *
     * @param asyncOperation The asynchronous operation.
     * @param callback The success callback.
     * @param <V> The type of value produced asynchronously and provided to the callback as a result.
     */
    public static <V> void asyncTask(ProducerWithThrow<V> asyncOperation, Consumer<V> callback) {

        CompletableFuture.supplyAsync(() -> {
            try {
                return asyncOperation.apply();
            } catch (Throwable throwable) {
                return null;
            }
        }).thenAcceptAsync(v -> {
            if(callback != null)
                Platform.runLater(() -> callback.accept(v));
        });
    }
}
