package de.unidisk.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by carl on 29.01.17.
 */
public class SystemProperties {
  private static SystemProperties systemProperties;
  private Properties properties;
  private String[] possiblePaths = new String[]{
          "/development/UniDisk/crawler/src/main/webapp/WEB-INF/classes/unidisk_productive.properties"};
  private String productiveFile = "unidisk_productive.properties";
  private String versionedFile = "unidisk.properties";

  protected SystemProperties() throws IOException {
    properties = new Properties();
    InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(productiveFile);
    for (int i = 0; i < possiblePaths.length; i++) {
      if (resourceAsStream != null) {
        break;
      }
      resourceAsStream = new FileInputStream(new File(possiblePaths[i]));
    }
    properties.load(resourceAsStream);
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
