package de.pseudonymisierung.mainzelliste.client.fttp.normalization;

import de.pseudonymisierung.mainzelliste.client.fttp.util.PropertiesUtils;
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

  private final Map<String, FieldTransformer<String, String>> fieldTransformers;
  private final List<FieldConcatenation<String>> fieldConcatenations;

  public FieldsNormalization(Properties config) {
    // init field transformer suppliers
    fieldTransformersSupplier
        .put(StringFieldTransformer.class.getSimpleName(), StringFieldTransformer::new);
    // init field concatenation suppliers
    fieldConcatenationsSupplier
        .put(StringFieldConcatenation.class.getSimpleName(), StringFieldConcatenation::new);

    this.fieldTransformers = PropertiesUtils.getSubProperties(config, "field").entrySet()
        .stream()
        .filter(e -> fieldTransformersSupplier
            .containsKey(e.getValue().getProperty("transformer.type", "").trim()))
        .collect(Collectors.toMap(Entry::getKey,
            e -> fieldTransformersSupplier.get(e.getValue().getProperty("transformer.type"))
                .apply(e.getValue())));

    this.fieldConcatenations = PropertiesUtils.getSubProperties(config, "field").entrySet()
        .stream()
        .filter(e -> fieldConcatenationsSupplier
            .containsKey(e.getValue().getProperty("transformer.type", "").trim()))
        .map(e -> fieldConcatenationsSupplier.get(e.getValue().getProperty("transformer.type"))
            .apply(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
  }

  public Map<String, String> process(Map<String, String> fields) {
    Map<String, String> result = new HashMap<>(fields);
    // transform field value
    fieldTransformers.forEach(
        (n, t) -> result.computeIfPresent(n, (fieldName, fieldValue) -> t.transform(fieldValue)));
    // concat fields
    fieldConcatenations.forEach(c -> c.concat(result));
    return result;
  }
}
