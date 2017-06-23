package de.unidisk.common;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by carl on 29.01.17.
 */
public class SystemProperties {
  private static SystemProperties systemProperties;
  private Properties properties;

  protected SystemProperties() throws IOException {
    properties = new Properties();
    String[] pathToProperties = {".", "de", "unidisk", "crawler", "unidisk_productiv.properties"};
    //todo jb könnte problematisch werden, mit dem Tomcat deploy
    properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(String.join(File.separator, pathToProperties)));
  }

  public static Properties getInstance() {
    if (systemProperties == null) {
      try {
        systemProperties = new SystemProperties();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return systemProperties.properties;
  }
}