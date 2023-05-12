package ru.rerumu.coub_loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class Configuration {
    private final Properties properties;

    public Configuration() throws IOException {
//        String confPath = System.getProperty( "conf_path" );
//        FileInputStream is = new FileInputStream(confPath);
//        InputStreamReader inputStreamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        Map<String, String> env = System.getenv();
        properties = new Properties();
//        properties.load(inputStreamReader);
        for(String envName: env.keySet()){
            properties.setProperty(envName,env.get(envName));
        }

    }

    public String getProperty(String name) {
        String res = properties.getProperty(name);
        if (res == null){
            throw new IllegalArgumentException();
        }
        return res;
    }
}
