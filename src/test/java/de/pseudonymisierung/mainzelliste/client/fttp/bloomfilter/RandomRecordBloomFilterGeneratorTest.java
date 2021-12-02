package de.pseudonymisierung.mainzelliste.client.fttp.bloomfilter;

import de.pseudonymisierung.mainzelliste.client.fttp.normalization.FieldsNormalization;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RandomRecordBloomFilterGeneratorTest {

  @Test
  public void testGenerateBalancedBloomFilter() {
    //bloomfilter configuration
    Properties bloomFilterConfig = new Properties();
    bloomFilterConfig.put("length", "800");
    bloomFilterConfig.put("nGramLength", "2");
    bloomFilterConfig.put("randomPositionNumber", "30");
    bloomFilterConfig.put("vocabulary", "abcdefghijkalmnoprstuvmxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789");
    bloomFilterConfig.put("balanced.seed",        "154866848");
    bloomFilterConfig.put("field.firstName.seed", "487484864");
    bloomFilterConfig.put("field.lastName.seed",  "311296856");
    bloomFilterConfig.put("field.birthDate.seed", "897689196");
    bloomFilterConfig.put("field.gender.seed",    "298767897");

    //fields normalization config.
    Properties normalizationConfig = new Properties();
    normalizationConfig.put("field.firstName.transformer.0.type", "StringFieldTransformer");
    normalizationConfig.put("field.firstName.transformer.0.replacement", "{\"Dr\\\\.|Dipl\\\\.\":\"\",\"é\":\"e\",\"ä\":\"ae\",\"Ä\":\"AE\",\"ö\":\"oe\",\"Ö\":\"OE\",\"ü\":\"ue\",\"Ü\":\"UE\"}");
    normalizationConfig.put("field.firstName.transformer.0.upperCase", "true");
    normalizationConfig.put("field.firstName.transformer.0.trim", "true");
    normalizationConfig.put("field.firstName.transformer.1.type", "StringFieldTransformer");
    normalizationConfig.put("field.firstName.transformer.1.allowedChars", "[A-Z\\s]");
    normalizationConfig.put("field.lastName.transformer.0.type", "StringFieldTransformer");
    normalizationConfig.put("field.lastName.transformer.0.replacement", "{\"Dr\\\\.|Dipl\\\\.\":\"\",\"é\":\"e\",\"ä\":\"ae\",\"Ä\":\"AE\",\"ö\":\"oe\",\"Ö\":\"OE\",\"ü\":\"ue\",\"Ü\":\"UE\"}");
    normalizationConfig.put("field.lastName.transformer.0.upperCase", "true");
    normalizationConfig.put("field.lastName.transformer.0.trim", "true");
    normalizationConfig.put("field.lastName.transformer.1.type", "StringFieldTransformer");
    normalizationConfig.put("field.lastName.transformer.1.allowedChars", "[A-Z\\s]");
    //concat year, month and day field values in birthDate field
    //normalizationConfig.put("field.birthDate.transformer.0.type", "StringFieldConcatenation");
    //normalizationConfig.put("field.birthDate.transformer.0.source", "year,month,day");

    //init filed normalization
    FieldsNormalization fieldsNormalization = new FieldsNormalization(normalizationConfig);
    //init bloom filter generator
    RandomRecordBloomFilterGenerator bloomFilterGenerator = new RandomRecordBloomFilterGenerator(bloomFilterConfig);

    //prepare idat fields
    Map<String, String> fields = new HashMap<>();
    fields.put("firstName", "Herr");
    fields.put("lastName", "Mustermann");
    fields.put("birthDate", "19200101");
    fields.put("gender", "m");
    String expectedBF = "aFRAty//BwRtDednRgb/Lwx9n6wFvdPhniK3dmEr03b4kcSZ+Jw68m++9h8FO43f+HHsEpk7Zc3YiF7Q9O8Upom5zJKjzUElYlFubVjAAgJMQWCMME6Yp+wkPXjm2bBHckHVs6ZzJASI/vAub/fVVps2dfmhYrA0MGXjd/iFDIZ4bF0xscaz7bzTo+uJTwQg008EVBWnbxr/57bHC309SCdbbWSxZhi3l+neQ7g3RoGxXQVGrRzEQdmsNlw3yUxINQ65LIAcbUA=";

    //normalize fields
    Map<String, String> normalizedFields = fieldsNormalization.process(fields);

    //generate bloom filter
    String bloomFilter = bloomFilterGenerator.generateBalancedBloomFilter(normalizedFields).getBase64String();
    Assert.assertEquals(bloomFilter, expectedBF);
  }
}