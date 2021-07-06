package de.pseudonymisierung.mainzelliste.client.fttp.normalization;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.testng.Assert;
import org.testng.annotations.Test;

public class StringFieldConcatenationTest {

  @Test
  public void testConcat() {
    //1. Test
    Properties config = new Properties();
    config.put("source", "year,month,day");
    FieldConcatenation<String> fieldConcatenation = new StringFieldConcatenation("birthDate", config);

    Map<String, String> fields = new HashMap<>();
    fields.put("year", "1900");
    fields.put("month","12");
    fields.put("day","10");
    fieldConcatenation.concat(fields);

    Assert.assertTrue(fields.containsKey("birthDate"));
    Assert.assertEquals(fields.get("birthDate"), "19001210");

    //2. Test
    config = new Properties();
    config.put("source", "year,day,month");
    fieldConcatenation = new StringFieldConcatenation("birthDate", config);

    fields = new HashMap<>();
    fields.put("year", "1900");
    fields.put("month","12");
    fields.put("day","20");
    fieldConcatenation.concat(fields);

    Assert.assertTrue(fields.containsKey("birthDate"));
    Assert.assertEquals(fields.get("birthDate"), "19002012");
  }
}