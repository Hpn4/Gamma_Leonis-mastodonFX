package eus.ehu.gleonis.gleonismastodonfx.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesManager {

    private static final String path = "config.properties";

    private static PropertiesManager instance;

    private Properties prop;

    private String clientID;

    private String clientSecret;

    private String dbUser;

    public PropertiesManager() {
        loadProperties();
    }

    public static PropertiesManager getInstance() {
        if (instance == null)
            instance = new PropertiesManager();

        return instance;
    }

    private void loadProperties() {
        prop = new Properties();

        // If the file does not exist, we create it
        Path path1 = Paths.get(path);
        if (!Files.exists(path1)) {
            try {
                Files.createFile(path1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Then we read data from the file
        try (InputStream input = new FileInputStream(path)) {
            prop.load(input);
            clientID = prop.getProperty("app.client.id");
            clientSecret = prop.getProperty("app.client.secret");
            dbUser = prop.getProperty("db.user");
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

    }

    private void saveProperties() {
        try (OutputStream output = new FileOutputStream(path)) {
            prop.store(output, null);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
        prop.setProperty("db.user", dbUser);
        saveProperties();
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setApp(String clientID, String clientSecret) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        prop.setProperty("app.client.id", clientID);
        prop.setProperty("app.client.secret", clientSecret);
        saveProperties();
    }

    public boolean empty() {
        return clientID == null || clientSecret == null || clientID.isEmpty() || clientSecret.isEmpty();
    }

}
