module eus.ehu.gleonis.gleonismastodonfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires okhttp3;
    requires com.google.gson;
    requires org.jsoup;
    requires okio;

    requires java.sql;
    requires org.apache.logging.log4j;

    requires java.desktop;

    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    opens eus.ehu.gleonis.gleonismastodonfx to javafx.fxml;
    exports eus.ehu.gleonis.gleonismastodonfx;

    exports eus.ehu.gleonis.gleonismastodonfx.db;
    opens eus.ehu.gleonis.gleonismastodonfx.db to org.hibernate.orm.core;

    opens eus.ehu.gleonis.gleonismastodonfx.api to com.google.gson;
    opens eus.ehu.gleonis.gleonismastodonfx.api.apistruct to com.google.gson;
    exports eus.ehu.gleonis.gleonismastodonfx.api.apistruct;
    exports eus.ehu.gleonis.gleonismastodonfx.api;

    opens eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable to javafx.fxml;
    opens eus.ehu.gleonis.gleonismastodonfx.presentation to javafx.fxml;
    exports eus.ehu.gleonis.gleonismastodonfx.presentation;
    exports eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

    opens eus.ehu.gleonis.gleonismastodonfx.utils to javafx.fxml;
    exports eus.ehu.gleonis.gleonismastodonfx.utils;

}