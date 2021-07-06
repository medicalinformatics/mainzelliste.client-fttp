# Mainzellist.Client fTTP Library


## Usage
```java
// define how idat fields will be normalized 
Properties normalizationConfig = new Properties();
normalizationConfig.put("field.firstName.transformer.0.type", "StringFieldTransformer");
normalizationConfig.put("field.firstName.transformer.0.replacement", "{\"Dr\\\\.|Dipl\\\\.\":\"\",\"é\":\"e\",\"ä\":\"ae\",\"Ä\":\"AE\",\"ö\":\"oe\",\"Ö\":\"OE\",\"ü\":\"ue\",\"Ü\":\"UE\"}");
normalizationConfig.put("field.firstName.transformer.0.upperCase", "true");
normalizationConfig.put("field.firstName.transformer.0.trim", "true");
normalizationConfig.put("field.firstName.transformer.1.allowedChars", "[A-Z\\s]");
normalizationConfig.put("field.lastName.transformer.0.type", "StringFieldTransformer");
normalizationConfig.put("field.lastName.transformer.0.replacement", "{\"Dr\\\\.|Dipl\\\\.\":\"\",\"é\":\"e\",\"ä\":\"ae\",\"Ä\":\"AE\",\"ö\":\"oe\",\"Ö\":\"OE\",\"ü\":\"ue\",\"Ü\":\"UE\"}");
normalizationConfig.put("field.lastName.transformer.0.upperCase", "true");
normalizationConfig.put("field.lastName.transformer.0.trim", "true");
normalizationConfig.put("field.lastName.transformer.1.allowedChars", "[A-Z\\s]");

// init field normalization
FieldsNormalization fieldsNormalization = new FieldsNormalization(normalizationConfig);

//define bloom filter configuration
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

//init bloom filter generator
RandomRecordBloomFilterGenerator bloomFilterGenerator = new RandomRecordBloomFilterGenerator(bloomFilterConfig);

// prepare idat fields
Map<String, String> fields = new HashMap<>();
fields.put("firstName", "Herr");
fields.put("lastName", "Mustermann");
fields.put("birthDate", "19200101");
fields.put("gender", "m");

// normalize fields
Map<String, String> normalizedFields = fieldsNormalization.process(fields);

// generate bloom filter
String bloomFilter = bloomFilterGenerator.generateBalancedBloomFilter(normalizedFields).getBase64String();
```

## Build

Use maven to build the jar:

```
mvn clean install
```

## License

Copyright 2020 The Samply Development Community

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
