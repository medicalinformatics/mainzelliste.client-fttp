package de.pseudonymisierung.fttp.bloomfilter;

import java.util.Base64;
import java.util.BitSet;

public class RecordBloomFilter {

  private final BitSet bitSet;
  private final int length;

  public RecordBloomFilter(BitSet bitSet, int length) {
    this.bitSet = bitSet;
    this.length = length;
  }

  public String getBase64String() {
    return Base64.getEncoder().encodeToString(bitSet.toByteArray());
  }

  public BitSet getBitSet() {
    return bitSet;
  }

  public int getLength() {
    return length;
  }
}
