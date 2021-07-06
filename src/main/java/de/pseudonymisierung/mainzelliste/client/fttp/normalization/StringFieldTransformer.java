package de.pseudonymisierung.mainzelliste.client.fttp.normalization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

public class StringFieldTransformer implements FieldTransformer<String, String> {

  private Map<String, String> replacements = new Gson().fromJson("{'é':'e','ä':'ae','Ä':'AE',"
      + "'ö':'oe','Ö':'OE','ü':'ue','Ü':'UE'}", new TypeToken<HashMap<String, String>>() {
  }.getType());
  private String allowedChars = "[A-Z]";

  public StringFieldTransformer(Properties config) {
    this(config.getProperty("transformer.replacement"), config.getProperty("transformer.allowedChars"));
  }

  public StringFieldTransformer(String replacementConfig, String allowedCharsConfig) {
    if (allowedCharsConfig != null && !allowedCharsConfig.trim().isEmpty()) {
      replacements = new Gson().fromJson(replacementConfig.trim(),
          new TypeToken<HashMap<String, String>>() {
          }.getType());
    }
    if (allowedCharsConfig != null && !allowedCharsConfig.trim().isEmpty()) {
      allowedChars = allowedCharsConfig.trim();
    }
  }

  @Override
  public String transform(String input) {
    String result = input;

    //replace charts
    for (Entry<String, String> entry : replacements.entrySet()) {
      result = result.replaceAll(entry.getKey(), entry.getValue());
    }
    result = result.toUpperCase();

    //remove not allowed characters
    StringBuilder resultStringBuilder = new StringBuilder();
    for (Character c : result.toCharArray()) {
      String currentCharacter = c.toString();
      if (Pattern.matches(allowedChars, currentCharacter)) {
        resultStringBuilder.append(currentCharacter);
      }
    }

    return resultStringBuilder.toString();
  }
}
