package de.pseudonymisierung.fttp.normalization;

import java.util.Map;

public interface FieldConcatenation<I> {

  public void concat(Map<String, I> inputs);
}
