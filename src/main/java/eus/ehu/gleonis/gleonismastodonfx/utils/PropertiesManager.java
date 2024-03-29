package eus.ehu.gleonis.gleonismastodonfx.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertiesManager {

    private static String path = "config.properties";

    private static PropertiesManager instance;

    private Properties prop;

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

        // Get some system specific paths
        String fileSep = System.getProperty("file.separator");
        String home = System.getProperty("user.home") + fileSep + ".glmastodonfx";

        // Create the empty directory for config if it does not exist
        Path prentDir = Paths.get(home);
        if (!Files.exists(prentDir)) {
            try {
                Files.createDirectory(prentDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        path = home + fileSep + path;

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
}
