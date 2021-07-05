package de.pseudonymisierung.fttp.normalization;

public interface FieldTransformer<I, O> {

  O transform(I input);
}
