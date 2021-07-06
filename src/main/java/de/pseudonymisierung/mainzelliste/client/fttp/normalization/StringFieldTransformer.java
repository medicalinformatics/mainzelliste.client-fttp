package de.pseudonymisierung.mainzelliste.client.fttp.normalization;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class StringFieldTransformer implements FieldTransformer<String, String> {

  private Map<String, String> replacements = new HashMap<>();
  private String allowedChars = "";

  private List<UnaryOperator<String>> transformationSteps = new LinkedList<>();

  public StringFieldTransformer(Properties config) {
    this(config.getProperty("replacement", ""),
        config.getProperty("allowedChars", ""),
        Boolean.parseBoolean(config.getProperty("upperCase", "")),
        Boolean.parseBoolean(config.getProperty("trim", "")));
  }

  public StringFieldTransformer(String replacementConfig, String allowedCharsConfig,
      boolean upperCase, boolean trim) {
    if (!replacementConfig.trim().isEmpty()) {
      this.replacements = new Gson().fromJson(replacementConfig.trim(),
          new TypeToken<HashMap<String, String>>() {
          }.getType());
      if (!replacements.isEmpty()) {
        transformationSteps.add(this::replaceString);
      }
    }

    if (!allowedCharsConfig.trim().isEmpty()) {
      this.allowedChars = allowedCharsConfig.trim();
      if (!allowedChars.isEmpty()) {
        transformationSteps.add(this::restrictChars);
      }
    }

    if (upperCase) {
      transformationSteps.add(String::toUpperCase);
    }

    if (trim) {
      transformationSteps.add(String::trim);
    }
  }

  @Override
  public String transform(String input) {
    String result = input;
    for (UnaryOperator<String> transformationStep : transformationSteps) {
      result = transformationStep.apply(result);
    }
    return result;
  }

  private String replaceString(String input) {
    String result = input;
    for (Entry<String, String> entry : replacements.entrySet()) {
      result = result.replaceAll(entry.getKey(), entry.getValue());
    }
    return result;
  }

  private String restrictChars(String input) {
    StringBuilder resultStringBuilder = new StringBuilder();
    for (Character c : input.toCharArray()) {
      String currentCharacter = c.toString();
      if (Pattern.matches(allowedChars, currentCharacter)) {
        resultStringBuilder.append(currentCharacter);
      }
    }
    return resultStringBuilder.toString();
  }
}
