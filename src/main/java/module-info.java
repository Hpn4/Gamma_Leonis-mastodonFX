module eus.ehu.gleonis.gleonismastodonfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.google.gson;
    requires java.desktop;


    opens eus.ehu.gleonis.gleonismastodonfx to javafx.fxml;
    exports eus.ehu.gleonis.gleonismastodonfx;
}