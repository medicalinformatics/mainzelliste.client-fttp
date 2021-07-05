package de.pseudonymisierung.fttp.normalization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringFieldConcatenation implements FieldConcatenation<String> {

  private final String fieldName;
  private final List<String> fieldNames;

  public StringFieldConcatenation(String fieldName, Properties config) {
    this.fieldName = fieldName;
    this.fieldNames = Stream.of(config.getProperty("transformer.source", "").split(","))
        .map(String::trim)
        .collect(Collectors.toList());
  }

  public StringFieldConcatenation(Collection<String> fieldNames, String fieldName) {
    this.fieldName = fieldName;
    this.fieldNames = new ArrayList<>(fieldNames);
  }

  @Override
  public void concat(Map<String, String> inputs) {
    StringBuilder result = new StringBuilder();
    fieldNames.forEach(n -> result.append(inputs.remove(n)));
    inputs.put(fieldName, result.toString());
  }
}
