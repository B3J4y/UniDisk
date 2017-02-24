package de.unidisk.common;

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
    properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("./de/jbernoth/crawler/unidisk.properties"));
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
