package eus.ehu.gleonis.gleonismastodonfx.utils;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class Utils {

    private static final ResourceBundle translation = ResourceBundle.getBundle("translation", Locale.getDefault());

    public static FXMLLoader loadAndTranslate(URL path) {
        return new FXMLLoader(path, translation);
    }

    public static String getTranslation(String key) {
        return translation.getString(key);
    }

    public static Instant ISODateToDate(String date) {
        return Instant.parse(date);
    }

    public static String getDateString(String date) {
        Instant instant = ISODateToDate(date);
        LocalDate realDate = LocalDate.ofInstant(instant, ZoneId.systemDefault());
        Period d = Period.between(realDate, LocalDate.now());

        if (d.getYears() >= 1)
            return realDate.getDayOfMonth() + " " + realDate.getMonth().toString() + " " + realDate.getYear();

        if (d.getMonths() >= 1 || d.getDays() > 14) {
            String month = realDate.getMonth().toString();
            return realDate.getDayOfMonth() + " " + month.charAt(0) + month.substring(1).toLowerCase();
        }

        if (d.getDays() >= 7)
            return (d.getDays() / 7) + "w";

        if (d.getDays() >= 1)
            return d.getDays() + "d";

        Duration duration = Duration.between(instant, Instant.now());
        if (duration.toHours() >= 1)
            return duration.toHours() + "h";

        if (duration.toMinutes() >= 1)
            return duration.toMinutes() + "m";

        return "now";
    }

    /**
     * Create and run an async task using the provided function as the asynchronous operation,
     * and the callback as the success operation. Error are ignored and returned as null values.
     *
     * @param asyncOperation The asynchronous operation.
     * @param callback       The success callback.
     * @param <V>            The type of value produced asynchronously and provided to the callback as a result.
     */
    public static <V> void asyncTask(ProducerWithThrow<V> asyncOperation, Consumer<V> callback) {

        CompletableFuture.supplyAsync(() -> {
            try {
                return asyncOperation.apply();
            } catch (Throwable throwable) {
                return null;
            }
        }).thenAcceptAsync(v -> {
            if (callback != null)
                Platform.runLater(() -> callback.accept(v));
        });
    }

    public static <S, T> void mapByValue(
            ObservableList<S> sourceList,
            ObservableList<T> targetList,
            Function<S, T> mapper) {
        Objects.requireNonNull(sourceList);
        Objects.requireNonNull(targetList);
        Objects.requireNonNull(mapper);
        targetList.clear();

        Map<S, T> sourceToTargetMap = new HashMap<>();
        // Populate targetList by sourceList and mapper
        for (S s : sourceList) {
            T t = mapper.apply(s);
            targetList.add(t);
            sourceToTargetMap.put(s, t);
        }

        // Listen to changes in sourceList and update targetList accordingly
        ListChangeListener<S> sourceListener = c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); i++) {
                        int j = c.getPermutation(i);
                        S s = sourceList.get(j);
                        T t = sourceToTargetMap.get(s);
                        targetList.set(i, t);
                    }
                } else {
                    for (S s : c.getRemoved()) {
                        T t = sourceToTargetMap.get(s);
                        targetList.remove(t);
                        sourceToTargetMap.remove(s);
                    }

                    int i = c.getFrom();
                    for (S s : c.getAddedSubList()) {
                        T t = mapper.apply(s);
                        targetList.add(i, t);
                        sourceToTargetMap.put(s, t);
                        i += 1;
                    }
                }
            }
        };

        sourceList.addListener(sourceListener);
        // Store the listener in targetList to prevent GC
        // The listener should be active as long as targetList exists
        targetList.addListener((InvalidationListener) iv ->
        {
            Object[] refs = {sourceListener,};
            Objects.requireNonNull(refs);
        });
    }

    public static Image loadImage(String url) {
        if (url.toLowerCase().endsWith(".webp")) {
            try {
                URL u = new URL(url);
                InputStream in = u.openStream();
                BufferedInputStream bis = new BufferedInputStream(new DataInputStream(in));

                BufferedImage bf = ImageIO.read(bis);

                // return SwingFXUtils.toFXImage(image, null);

                WritableImage wr = new WritableImage(bf.getWidth(), bf.getHeight());
                PixelWriter pw = wr.getPixelWriter();
                for (int x = 0; x < bf.getWidth(); x++)
                    for (int y = 0; y < bf.getHeight(); y++)
                        pw.setArgb(x, y, bf.getRGB(x, y));

                in.close();
                return wr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Image(url);
    }

    public static void redirectToAccount(String accId, Node... nodes) {
        EventHandler<? super MouseEvent> handler = event -> MainApplication.getInstance().requestShowAccount(accId);

        for (Node node : nodes) node.setOnMouseClicked(handler);
    }

    @FunctionalInterface
    public interface ProducerWithThrow<R> {
        R apply() throws Throwable;
    }
}
