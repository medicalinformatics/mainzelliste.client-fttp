package de.pseudonymisierung.mainzelliste.client.fttp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesUtils {

  private PropertiesUtils(){}

  /**
   * Property key must contain at least two point : prefix.[var].suffix
   *
   * @param properties properties intance
   * @param prefix     property key prefix
   * @return a map with [var] as key and a Properties instance map the suffix with the corresponding
   * value
   */
  public static Map<String, Properties> getSubProperties(Properties properties, String prefix) {
    Map<String, Properties> subPropertiesMap = new HashMap<>();
    Pattern subPropertiesRegEx = Pattern.compile("^" + prefix + "\\.(\\w+)\\.(.+)");
    for (Entry<Object, Object> entry : properties.entrySet()) {
      Matcher matcher = subPropertiesRegEx.matcher((String) entry.getKey());
      if (!matcher.find()) {
        continue;
      }
      subPropertiesMap.compute(matcher.group(1),
          (newKey, newProperties) -> {
            Properties p = Optional.ofNullable(newProperties).orElseGet(Properties::new);
            p.setProperty(matcher.group(2), (String) entry.getValue());
            return p;
          });
    }
    return subPropertiesMap;
  }
}
