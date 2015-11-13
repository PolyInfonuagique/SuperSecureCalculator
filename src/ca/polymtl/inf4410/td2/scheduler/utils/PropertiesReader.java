package ca.polymtl.inf4410.td2.scheduler.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private static final String PARAM_SERVERS = "servers";

    private Properties properties;
    public PropertiesReader(String filename) {
        properties = new Properties();
        File resource = new File(filename);

        try {
            InputStream propertiesStream = new FileInputStream(resource);
            properties.load(propertiesStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getServerAddr(){
         return properties.getProperty(PARAM_SERVERS).split(",");
    }
}
