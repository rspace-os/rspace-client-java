package com.researchspace.api.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigPropertiesReader {

    public static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

    /* returns property value from config file */
    public String getConfigProperty(String propertyName) {
        Properties prop = new Properties();
        String propertyValue = "";
    
        try (InputStream input = new FileInputStream(CONFIG_PROPERTIES_FILENAME)) {
            prop.load(input);
            propertyValue = prop.getProperty(propertyName);
        } catch ( IOException e) {
            throw new IllegalArgumentException(String.format("Property %s not found", propertyName), e);
        }
        return propertyValue;
    }

}
