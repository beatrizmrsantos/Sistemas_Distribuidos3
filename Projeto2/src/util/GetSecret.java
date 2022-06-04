package util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class GetSecret {

    private static Properties props;

    public static String get(String name){
        if(props == null){
            try (InputStream input = new FileInputStream("trab.props")) {
                props = new Properties();
                props.load(input);
            }catch (Exception e){}
        }
        return props.getProperty(name);
    }
}
