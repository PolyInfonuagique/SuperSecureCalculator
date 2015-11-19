package ca.polymtl.inf4410.td2.scheduler.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PropertiesReader class
 *
 * Charge la configuration pour les adresses IP des serveurs
 **/
public class PropertiesReader {
    private static final String PARAM_SERVERS = "servers";

    private Properties properties;

    /**
     * Constructeur
     * @param filename fichier de configuration correspondant
     */
    public PropertiesReader(String filename) throws IOException {
        properties = new Properties();
        File resource = new File(filename);

        InputStream propertiesStream = new FileInputStream(resource);
        properties.load(propertiesStream);

    }

    /**
     * @return Tableau d'addresse IP
     */
    public String[] getServerAddr(){
         return properties.getProperty(PARAM_SERVERS).split(",");
    }
}
