package ca.polymtl.inf4410.td2.scheduler.utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private static final String PARAM_SERVERS = "servers";

    private Properties properties;
    public PropertiesReader(String filename) {
        properties = new Properties();

        InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream(filename);
        try {
            properties.load(propertiesStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getServerAddr(){
         return properties.getProperty(PARAM_SERVERS).split(",");
    }
}
