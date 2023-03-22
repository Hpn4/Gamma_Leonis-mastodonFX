module eus.ehu.gleonis.gleonismastodonfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.google.gson;
    requires java.desktop;


    opens eus.ehu.gleonis.gleonismastodonfx to javafx.fxml;
    exports eus.ehu.gleonis.gleonismastodonfx;

    opens eus.ehu.gleonis.gleonismastodonfx.api to com.google.gson;
    exports eus.ehu.gleonis.gleonismastodonfx.api;

    opens eus.ehu.gleonis.gleonismastodonfx.api.apistruct to com.google.gson;
    exports eus.ehu.gleonis.gleonismastodonfx.api.apistruct;
}