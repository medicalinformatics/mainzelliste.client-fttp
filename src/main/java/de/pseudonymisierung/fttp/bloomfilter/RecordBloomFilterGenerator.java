package de.pseudonymisierung.fttp.bloomfilter;

import java.util.Map;

public interface RecordBloomFilterGenerator {

  RecordBloomFilter generate(Map<String, String> idat);

  RecordBloomFilter generateBalancedBloomFilter(Map<String, String> idat);

  RecordBloomFilter generateBalancedBloomFilter(RecordBloomFilter bloomFilter);
}
