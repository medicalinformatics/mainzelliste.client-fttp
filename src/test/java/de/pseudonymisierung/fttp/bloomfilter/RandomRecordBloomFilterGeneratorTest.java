package de.pseudonymisierung.fttp.bloomfilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RandomRecordBloomFilterGeneratorTest {

  @Test
  public void testGenerateBalancedBloomFilter() {
    //init configuration
    Properties config = new Properties();
    //bloomfilter configuration
    config.put("length", "800");
    config.put("nGramLength", "2");
    config.put("randomPositionNumber", "30");
    config.put("vocabulary", "abcdefghijkalmnoprstuvmxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789");
    config.put("balanced.seed",        "154866848");
    config.put("field.firstName.seed", "487484864");
    config.put("field.lastName.seed",  "311296856");
    config.put("field.birthDate.seed", "897689196");
    config.put("field.gender.seed",    "298767897");

    //field configuration
    config.put("field.firstName.transformer.type", "StringFieldTransformer");
    config.put("field.firstName.transformer.replacement", "{\"Dr\\\\.|Dipl\\\\.\":\"\",\"é\":\"e\",\"ä\":\"ae\",\"Ä\":\"AE\",\"ö\":\"oe\",\"Ö\":\"OE\",\"ü\":\"ue\",\"Ü\":\"UE\"}");
    config.put("field.firstName.transformer.allowedChars", "[A-Z\\s]");
    config.put("field.lastName.transformer.type", "StringFieldTransformer");
    config.put("field.lastName.transformer.replacement", "{\"Dr\\\\.|Dipl\\\\.\":\"\",\"é\":\"e\",\"ä\":\"ae\",\"Ä\":\"AE\",\"ö\":\"oe\",\"Ö\":\"OE\",\"ü\":\"ue\",\"Ü\":\"UE\"}");
    config.put("field.lastName.transformer.allowedChars", "[A-Z\\s]");
//    config.put("field.birthDate.transformer.type", "StringFieldConcatenation");
//    config.put("field.birthDate.transformer.source", "geburtsjahr,geburtsmonat,geburtstag");

    RandomRecordBloomFilterGenerator generator = new RandomRecordBloomFilterGenerator(config);

    Map<String, String> idat = new HashMap<>();
    idat.put("firstName", "Herr");
    idat.put("lastName", "Mustermann");
    idat.put("birthDate", "19200101");
    idat.put("gender", "m");
    String expectedBF = "aFRAty//BwRtDednRgb/Lwx9n6wFvdPhniK3dmEr03b4kcSZ+Jw68m++9h8FO43f+HHsEpk7Zc3YiF7Q9O8Upom5zJKjzUElYlFubVjAAgJMQWCMME6Yp+wkPXjm2bBHckHVs6ZzJASI/vAub/fVVps2dfmhYrA0MGXjd/iFDIZ4bF0xscaz7bzTo+uJTwQg008EVBWnbxr/57bHC309SCdbbWSxZhi3l+neQ7g3RoGxXQVGrRzEQdmsNlw3yUxINQ65LIAcbUA=";

    String ownBF = generator.generateBalancedBloomFilter(idat).getBase64String();
    Assert.assertEquals(ownBF, expectedBF);
  }
}