package de.pseudonymisierung.mainzelliste.client.fttp.normalization;

public interface FieldTransformer<I, O> {

  O transform(I input);
}
