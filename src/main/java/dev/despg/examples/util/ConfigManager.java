package dev.despg.examples.util;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfigManager
{

    private static volatile ConfigManager instance;
    private Map<String, String> configMap = new HashMap<>();
    private static final String CONFIG_FILE = "config.properties";
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());

    private ConfigManager()
    {
        loadProperties();
    }

    private void loadProperties()
    {
        Properties properties = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE))
        {
            if (is == null)
            {
                throw new IOException(CONFIG_FILE + " not found in the classpath");
            }
            properties.load(is);
            for (String key : properties.stringPropertyNames())
            {
                configMap.put(key, properties.getProperty(key));
            }
        } catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Error reading the properties file.", e);
        }
    }

    public static ConfigManager getInstance()
    {
        if (instance == null)
        {
            synchronized (ConfigManager.class)
            {
                if (instance == null)
                {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    public String getProperty(String key)
    {
        return configMap.get(key);
    }

    public void setProperty(String key, String value)
    {
        configMap.put(key, value);
    }
}
