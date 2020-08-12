package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

    private String filePath = "props.properties";

    private Properties props;

    //region Singleton code
    private static PropertiesHelper instance;
    public static PropertiesHelper getInstance() {
        if(instance == null) {
            instance = new PropertiesHelper();
        }
        return instance;
    }
    //endregion

    private PropertiesHelper() {
        this.props = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            props.load(input);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String property) {
        return this.props.getProperty(property);
    }

}
