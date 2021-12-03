package de.pseudonymisierung.mainzelliste.client.fttp.normalization;

import de.pseudonymisierung.mainzelliste.client.fttp.util.PropertiesUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldsNormalization {

  private final Map<String, Function<Properties, FieldTransformer<String, String>>> fieldTransformersSupplier = new HashMap<>();
  private final Map<String, BiFunction<String, Properties, FieldConcatenation<String>>> fieldConcatenationsSupplier = new HashMap<>();

  private final Map<String, List<FieldTransformer<String, String>>> fieldTransformers;
  private final List<FieldConcatenation<String>> fieldConcatenations;

  public FieldsNormalization(Properties config) {
    // init field transformer suppliers
    fieldTransformersSupplier
        .put(StringFieldTransformer.class.getSimpleName(), StringFieldTransformer::new);
    // init field concatenation suppliers
    fieldConcatenationsSupplier
        .put(StringFieldConcatenation.class.getSimpleName(), StringFieldConcatenation::new);

    this.fieldTransformers = PropertiesUtils.getSubProperties(config, "field").entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey,
            p -> PropertiesUtils.getSubProperties(p.getValue(), "transformer").values().stream()
                .filter( c -> fieldTransformersSupplier.containsKey(c.getProperty("type", "").trim()))
                .map(c -> fieldTransformersSupplier.get(c.getProperty("type")).apply(c))
                .collect(Collectors.toList())));

    this.fieldConcatenations =  PropertiesUtils.getSubProperties(config, "field").entrySet().stream()
        .flatMap( f -> PropertiesUtils.getSubProperties(f.getValue(), "transformer").values().stream()
            .filter( p -> fieldConcatenationsSupplier.containsKey(p.getProperty("type", "").trim()))
            .map( p ->  fieldConcatenationsSupplier.get(p.getProperty("type")).apply( f.getKey(), p)))
        .collect(Collectors.toList());
  }

  public Map<String, String> process(Map<String, String> fields) {
    Map<String, String> result = new HashMap<>(fields);
    // transform field value
    fieldTransformers.forEach(
        (n, t) -> result.computeIfPresent(n, (fieldName, fieldValue) -> transform(t, fieldValue)));
    // concat fields
    fieldConcatenations.forEach(c -> c.concat(result));
    return result;
  }

  private String transform(List<FieldTransformer<String, String>> transformerList,
      String fieldValue) {
    String transformedField = fieldValue;
    for (FieldTransformer<String, String> transformer : transformerList) {
      transformedField = transformer.transform(transformedField);
    }
    return transformedField;
  }
}
