module eus.ehu.gleonis.gleonismastodonfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.google.gson;
    requires java.desktop;
    requires javafx.web;
    requires org.jsoup;


    opens eus.ehu.gleonis.gleonismastodonfx to javafx.fxml;
    exports eus.ehu.gleonis.gleonismastodonfx;

    opens eus.ehu.gleonis.gleonismastodonfx.api to com.google.gson;
    exports eus.ehu.gleonis.gleonismastodonfx.api;
    exports eus.ehu.gleonis.gleonismastodonfx.utils;

    opens eus.ehu.gleonis.gleonismastodonfx.api.apistruct to com.google.gson;
    exports eus.ehu.gleonis.gleonismastodonfx.api.apistruct;
    exports eus.ehu.gleonis.gleonismastodonfx.presentation;
    opens eus.ehu.gleonis.gleonismastodonfx.presentation to javafx.fxml;
    exports eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;
    opens eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable to javafx.fxml;
}