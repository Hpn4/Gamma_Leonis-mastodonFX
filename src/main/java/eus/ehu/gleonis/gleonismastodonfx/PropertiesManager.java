package eus.ehu.gleonis.gleonismastodonfx;

import java.io.*;
import java.util.Properties;

public class PropertiesManager {

    private static final String path = "config.properties";

    private static PropertiesManager instance;

    private Properties prop;

    private String token;

    private String clientID;

    private String clientSecret;

    public PropertiesManager() {
        loadProperties();
    }

    public static PropertiesManager getInstance() {
        if (instance == null)
            instance = new PropertiesManager();

        return instance;
    }

    private void loadProperties() {
        // Load from config.properties
        prop = new Properties();
        try (InputStream input = new FileInputStream(path)) {
            prop.load(input);
            clientID = prop.getProperty("app.client.id");
            clientSecret = prop.getProperty("app.client.secret");
            token = prop.getProperty("token");
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

    public String getToken() {
        return token;
    }

    /**
     * Sets the token and saves it to the properties file
     *
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
        prop.setProperty("token", token);
        saveProperties();
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

}
