package de.pseudonymisierung.mainzelliste.client.fttp.bloomfilter;

import de.pseudonymisierung.mainzelliste.client.fttp.util.PropertiesUtils;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomRecordBloomFilterGenerator implements RecordBloomFilterGenerator {

  protected final char[] vocabulary;
  protected final int nGramLength;
  protected final int length;
  protected final int randomPositionNumber;
  protected final Map<String, Random> fieldSeeds;
  protected final Long balancedBFSeed;
  protected final List<Integer> balancedBFSwapIndex = new ArrayList<>();
  Map<String, Map<String, BitSet>> ngramsToBitSetTable;

  public RandomRecordBloomFilterGenerator(Properties config) {
    this.vocabulary = config.getProperty("vocabulary", "ABCDEFGHIJKLMNOPQRSTUVWXYZ .-0123456789")
        .toCharArray();
    this.nGramLength = Integer.parseInt(config.getProperty("nGramLength", "2"));
    this.length = Integer.parseInt(config.getProperty("length", "900"));
    this.randomPositionNumber = Integer.parseInt(config.getProperty("randomPositionNumber", "30"));

    this.fieldSeeds = PropertiesUtils.getSubProperties(config, "field").entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey,
            e -> new Random(Long.parseLong(e.getValue().getProperty("seed")))));

    this.balancedBFSeed = Long.parseLong(config.getProperty("balanced.seed", "0"));
    //init random indexes of balanced bloom filter
    int balancedBFLength = length * 2;
    Random balancedBFRandom = new Random(balancedBFSeed);
    IntStream.range(0, balancedBFLength)
        .forEach(i -> balancedBFSwapIndex.add(balancedBFRandom.nextInt(balancedBFLength)));
    this.ngramsToBitSetTable = new LinkedHashMap<>();

    // find all possible ngrams
    this.fieldSeeds.forEach((fieldName, seed) -> {
      //TODO refactor: find a map of n-grams
      Map<String, BitSet> bigramsToBitSet = findBigrams();
      // set ngram BitSet with 'randomPositionNumber' random bits
      bigramsToBitSet.values().forEach(bitSet ->
          IntStream.range(0, randomPositionNumber)
              .forEach(i -> bitSet.set(seed.nextInt(this.length))));
      ngramsToBitSetTable.put(fieldName, bigramsToBitSet);
    });
  }

  //TODO refactor: find a map of n-grams
  private Map<String, BitSet> findBigrams() {
    Map<String, BitSet> result = new LinkedHashMap<>();
    for (char c0 : vocabulary) {
      char[] bigramArray = new char[2];
      bigramArray[0] = c0;
      for (char c1 : vocabulary) {
        bigramArray[1] = c1;
        result.put(new String(bigramArray), new BitSet(length));
      }
    }
    return result;
  }

  public RecordBloomFilter generate(Map<String, String> idat) {
    BitSet bitSet = new BitSet(length);
    fieldSeeds.keySet().stream().filter(idat::containsKey)
        .forEach(k -> bitSet.or(convertToBitSet(k, idat.get(k))));
    return new RecordBloomFilter(bitSet, length);
  }

  private BitSet convertToBitSet(String fieldName, String fieldValue) {
    BitSet bitSet = new BitSet(this.length);
    Map<String, BitSet> ngramsBitSetMap = this.ngramsToBitSetTable.get(fieldName);

    String word = Optional.ofNullable(fieldValue).orElse("");
    BitSet emptyBitSet = new BitSet();
    char lastCharacter = ' ';
    for (char c : word.toCharArray()) {
      bitSet.or(ngramsBitSetMap.getOrDefault(String.valueOf(lastCharacter) + c, emptyBitSet));
      lastCharacter = c;
    }
    bitSet.or(ngramsBitSetMap.getOrDefault(lastCharacter + " ", emptyBitSet));

    return bitSet;
  }

  public RecordBloomFilter generateBalancedBloomFilter(Map<String, String> idat) {
    return generateBalancedBloomFilter(generate(idat));
  }

  public RecordBloomFilter generateBalancedBloomFilter(RecordBloomFilter bloomFilter) {
    BitSet originBitSet = bloomFilter.getBitSet();
    int originLength = bloomFilter.getLength();
    int newLength = originLength * 2;
    BitSet balancedBloomFilter = new BitSet(newLength);
    balancedBloomFilter.or(originBitSet);
    for (int i = 0; i < bloomFilter.getLength(); i++) {
      balancedBloomFilter.set(i + originLength, !originBitSet.get(i));
    }

    //swap bits
    int i = 0;
    for (int newIndex : balancedBFSwapIndex) {
      boolean oldValue = balancedBloomFilter.get(i);
      balancedBloomFilter.set(i, balancedBloomFilter.get(newIndex));
      balancedBloomFilter.set(newIndex, oldValue);
      i++;
    }
    return new RecordBloomFilter(balancedBloomFilter, newLength);
  }
}
