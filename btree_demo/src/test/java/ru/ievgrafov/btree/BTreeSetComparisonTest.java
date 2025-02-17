package ru.ievgrafov.btree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import org.junit.Test;

public class BTreeSetComparisonTest {
  private final Comparator<Integer> intComparator = Comparator.naturalOrder();

  final String[] words = new String[] {
      "vivamus", "lectus", "purus", "sagittis", "vitae", "iaculis", "non", "porta", "at", "velit",
      "praesent", "quis", "enim", "a", "ligula", "semper", "ultricies", "sed", "vulputate", "ipsum",
      "sed", "tempor", "luctus", "sem", "metus", "suscipit", "tellus", "vehicula", "elit", "id", "nam",
      "fringilla", "congue", "ullamcorper", "condimentum", "risus", "et", "curabitur", "dictum", "est",
      "auctor", "etiam", "lacinia", "massa", "malesuada", "mauris", "vel", "leo", "feugiat", "aliquam",
      "libero", "aenean", "magna", "mi", "quisque", "volutpat", "aliquet", "felis", "tincidunt", "eu", "ut",
      "finibus", "urna", "egestas", "augue", "mollis", "nec", "in", "tristique", "in", "hac", "habitasse",
      "platea", "dictumst", "donec", "ut"
  };

  @Test
  public void testBTreeBehavesLikeTreeSet() {
    BTreeSet<Integer> set = new BTreeSet<>(30, intComparator);
    TreeSet<Integer> javaSet = new TreeSet<>(intComparator);
    Random generator = new Random(1);
    int iterations = 500000;

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations * 2);
      assertEquals("BTreeSet and TreeSet should respond same way on insert", javaSet.add(value), set.add(value));
    }

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations * 2);

      assertEquals("BTreeSet and TreeSet should respond same way on remove", javaSet.remove(value), set.remove(value));
    }

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations * 2);
      assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaSet.contains(value),
          set.contains(value));
    }

    assertArrayEquals("BTreeSet and TreeSet should be converted to same array", javaSet.toArray(), set.toArray());
  }

  @Test
  public void testBTreeIteratorBehavesLikeTreeSetIterator() {
    BTreeSet<Integer> set = new BTreeSet<>(30, intComparator);
    TreeSet<Integer> javaSet = new TreeSet<>(intComparator);
    Random generator = new Random(1);
    int iterations = 500000;

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations * 2);
      assertEquals("BTreeSet and TreeSet should respond same way on insert", javaSet.add(value), set.add(value));
    }

    Iterator<Integer> setIterator = set.iterator();
    Iterator<Integer> javaSetIterator = javaSet.iterator();

    while (setIterator.hasNext() && javaSetIterator.hasNext()) {
      assertEquals("BTreeSet and TreeSet should iterate over same keys", setIterator.next(), javaSetIterator.next());

      if (generator.nextInt(0, 500) > 0) {
        setIterator.remove();
        javaSetIterator.remove();
      }
    }
    System.out.println("Set size: " + javaSet.size() + "; BTree set size: " + set.size());

    set.valuesCountByLevel().forEach((level, size) -> System.out.println(size + " values on level " + level));
    set.nodesCountByLevel().forEach((level, size) -> System.out.println(size + " nodes on level " + level));

    assertEquals("BTreeSet and TreeSet should stop iterating at the same moment", setIterator.hasNext(),
        javaSetIterator.hasNext());
    assertArrayEquals("BTreeSet and TreeSet should be converted to same array", javaSet.toArray(), set.toArray());

    set.reset();
    System.out.println("Optimal nodes and values on levels:");

    set.valuesCountByLevel().forEach((level, size) -> System.out.println(size + " values on level " + level));
    set.nodesCountByLevel().forEach((level, size) -> System.out.println(size + " nodes on level " + level));
  }

  @Test
  public void testBTreeBehavesLikeTreeSetWithFactor1() {
    BTreeSet<Integer> set = new BTreeSet<>(1, intComparator);
    TreeSet<Integer> javaSet = new TreeSet<>(intComparator);
    Random generator = new Random(1);
    int iterations = 500000;

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations);
      assertEquals("BTreeSet and TreeSet should respond same way on insert", javaSet.add(value), set.add(value));
    }

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations);
      assertEquals("BTreeSet and TreeSet should respond same way on remove", javaSet.remove(value), set.remove(value));
    }

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations);
      assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaSet.contains(value),
          set.contains(value));
    }

    assertArrayEquals("BTreeSet and TreeSet should be converted to same array", javaSet.toArray(), set.toArray());
  }

  @Test
  public void testBTreeBehavesLikeTreeSetAfterCompleteCleanUp() {
    BTreeSet<Integer> set = new BTreeSet<>(30, intComparator);
    TreeSet<Integer> javaSet = new TreeSet<>(intComparator);
    Random generator = new Random(1);
    int iterations = 500000;
    List<Integer> valuesToRemove = new ArrayList<>();

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations * 2);
      javaSet.add(value);
      set.add(value);
      valuesToRemove.add(value);
    }
    assertArrayEquals("BTreeSet and TreeSet should be converted to same array", javaSet.toArray(), set.toArray());

    valuesToRemove.forEach((value) -> {
      javaSet.remove(value);
      set.remove(value);
      assertEquals(
          "BTreeSet and TreeSet shouldn't respond true to contains after removal",
          javaSet.contains(value),
          set.contains(value));
    });

    assertEquals("BTreeSet should be empty after all keys removed", 0, set.size());

    System.out.println("Set size: " + javaSet.size() + "; BTree set size: " + set.size());
    Map<Integer, Integer> sizes = set.valuesCountByLevel();

    sizes.forEach((level, size) -> System.out.println(size + " values on level " + level));

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations * 2);
      assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaSet.contains(value),
          set.contains(value));
    }

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations * 2);
      assertEquals("BTreeSet and TreeSet should respond same way on insert", javaSet.add(value), set.add(value));
    }

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations * 2);
      assertEquals("BTreeSet and TreeSet should respond same way on remove", javaSet.remove(value), set.remove(value));
    }

    assertEquals("BTreeSet and TreeSet should have same size after all ops", javaSet.size(), set.size());
    assertArrayEquals("BTreeSet and TreeSet should be converted to same array", javaSet.toArray(), set.toArray());
  }

  @Test
  public void testSparseTreeBehavesLikeTreeSet() {
    BTreeSet<Integer> set = new BTreeSet<>(30, intComparator);
    TreeSet<Integer> javaSet = new TreeSet<>(intComparator);
    Random generator = new Random(1);
    int targetSize = 100000;
    List<Integer> toBeRemoved = new ArrayList<>();

    while (javaSet.size() < targetSize) {
      for (int i = 0; i < 1000000; i++) {
        Integer value = generator.nextInt(0, targetSize * 10);
        javaSet.add(value);
        set.add(value);

        if (generator.nextInt(0, 5) != 0) {
          toBeRemoved.add(value);
        }
      }

      toBeRemoved.forEach((value) -> {
        javaSet.remove(value);
        set.remove(value);
      });
      toBeRemoved.clear();
    }
    assertEquals("BTreeSet and TreeSet should have same size after all ops", javaSet.size(), set.size());
    assertArrayEquals("BTreeSet and TreeSet should be converted to same array", javaSet.toArray(), set.toArray());

    System.out.println("Set size: " + javaSet.size() + "; BTree set size: " + set.size());

    set.valuesCountByLevel().forEach((level, size) -> System.out.println(size + " values on level " + level));
    set.nodesCountByLevel().forEach((level, size) -> System.out.println(size + " nodes on level " + level));
  }

  @Test
  public void testComparesCountOnLookup() {
    CountingComparatorWrapper<Integer> setComparator = new CountingComparatorWrapper<>(intComparator);
    CountingComparatorWrapper<Integer> javaSetComparator = new CountingComparatorWrapper<>(intComparator);
    BTreeSet<Integer> set = new BTreeSet<>(10, setComparator);
    TreeSet<Integer> javaSet = new TreeSet<>(javaSetComparator);

    Random generator = new Random(1);
    int[] targetSizes = { 10000, 100000, 1000000, 10000000 };
    List<Integer> toBeRemoved = new ArrayList<>();
    List<Integer> toBeTested = new ArrayList<>();
    int targetTestSize = 10000;

    for (int targetSize : targetSizes) {
      toBeTested.clear();
      toBeRemoved.clear();
      set.clear();
      javaSet.clear();

      for (int i = 0; i < targetSize; i++) {
        Integer value = generator.nextInt(0, targetSize * 2);
        if (toBeTested.size() < targetTestSize) {
          toBeTested.add(value);
        }

        javaSet.add(value);
        set.add(value);

        if (generator.nextInt(0, 5) != 0) {
          toBeRemoved.add(value);
        }
      }

      setComparator.resetCompareCount();
      javaSetComparator.resetCompareCount();

      toBeTested.forEach((value) -> {
        assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaSet.contains(value),
            set.contains(value));
      });
      assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set.size());

      System.out
          .println("Compares before removal; current size: " + set.size() + "; test values size: " + toBeTested.size());
      System.out.println("Comparisons count for BTree: " + setComparator.getCompareCount());
      System.out.println("Comparisons count for Java Set: " + javaSetComparator.getCompareCount());

      toBeRemoved.forEach((value) -> {
        javaSet.remove(value);
        set.remove(value);
      });

      setComparator.resetCompareCount();
      javaSetComparator.resetCompareCount();

      toBeTested.forEach((value) -> {
        assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaSet.contains(value),
            set.contains(value));
      });
      assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set.size());

      System.out
          .println("Compares after removal; current size: " + set.size() + "; test values size: " + toBeTested.size());
      System.out.println("Comparisons count for BTree: " + setComparator.getCompareCount());
      System.out.println("Comparisons count for Java Set: " + javaSetComparator.getCompareCount());
      System.out.println();
      System.out.println();
    }

  }

  @Test
  public void testComparesCountOnLookupRandomStrings() {
    CountingComparatorWrapper<String> setComparator = new CountingComparatorWrapper<String>(Comparator.naturalOrder());
    CountingComparatorWrapper<String> javaSetComparator = new CountingComparatorWrapper<String>(
        Comparator.naturalOrder());
    BTreeSet<String> set = new BTreeSet<>(100, setComparator);
    TreeSet<String> javaSet = new TreeSet<>(javaSetComparator);

    Random generator = new Random(1);
    int[] targetSizes = { 10000, 100000, 1000000, 10000000 };
    List<String> toBeRemoved = new ArrayList<>();
    List<String> toBeTested = new ArrayList<>();
    int targetTestSize = 10000;

    for (int targetSize : targetSizes) {
      toBeTested.clear();
      toBeRemoved.clear();
      set.clear();
      javaSet.clear();

      for (int i = 0; i < targetSize; i++) {
        String value = generateRandomString(generator);

        javaSet.add(value);
        set.add(value);

        if (generator.nextInt(0, 50) == 0) {
          toBeRemoved.add(value);
          if (toBeTested.size() < targetTestSize) {
            toBeTested.add(value);
          }
        }
      }

      setComparator.resetCompareCount();
      javaSetComparator.resetCompareCount();

      toBeTested.forEach((value) -> {
        assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaSet.contains(value),
            set.contains(value));
      });
      assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set.size());

      System.out
          .println("Compares before removal; current size: " + set.size() + "; test values size: " + toBeTested.size());
      System.out.println("Comparisons count for BTree: " + setComparator.getCompareCount());
      System.out.println("Comparisons count for Java Set: " + javaSetComparator.getCompareCount());

      toBeRemoved.forEach((value) -> {
        javaSet.remove(value);
        set.remove(value);
      });

      setComparator.resetCompareCount();
      javaSetComparator.resetCompareCount();

      toBeTested.forEach((value) -> {
        assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaSet.contains(value),
            set.contains(value));
      });
      assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set.size());

      System.out
          .println("Compares after removal; current size: " + set.size() + "; test values size: " + toBeTested.size());
      System.out.println("Comparisons count for BTree: " + setComparator.getCompareCount());
      System.out.println("Comparisons count for Java Set: " + javaSetComparator.getCompareCount());
      System.out.println();
      System.out.println();
    }
  }

  @Test
  public void testComparesCountInBenchmarkLikeData() {
    CountingComparatorWrapper<Integer> set3Comp = new CountingComparatorWrapper<>(intComparator);
    CountingComparatorWrapper<Integer> set10Comp = new CountingComparatorWrapper<>(intComparator);
    CountingComparatorWrapper<Integer> set100Comp = new CountingComparatorWrapper<>(intComparator);
    CountingComparatorWrapper<Integer> set1000Comp = new CountingComparatorWrapper<>(intComparator);
    CountingComparatorWrapper<Integer> javaSetComparator = new CountingComparatorWrapper<>(intComparator);
    BTreeSet<Integer> set3 = new BTreeSet<>(3, set3Comp);
    BTreeSet<Integer> set10 = new BTreeSet<>(10, set10Comp);
    BTreeSet<Integer> set100 = new BTreeSet<>(100, set100Comp);
    BTreeSet<Integer> set1000 = new BTreeSet<>(1000, set1000Comp);
    TreeSet<Integer> javaSet = new TreeSet<>(javaSetComparator);

    Random generator = new Random(1);
    int iterations = 10000000;
    List<Integer> toBeTested = new ArrayList<>();
    int targetTestSize = 10000;

    for (int i = 0; i < iterations; i++) {
      Integer value = generator.nextInt(0, iterations);
      if (toBeTested.size() < targetTestSize) {
        toBeTested.add(value);
      }

      javaSet.add(value);
      set3.add(value);
      set10.add(value);
      set100.add(value);
      set1000.add(value);
    }
    for (int i = 0; i < iterations / 2; i++) {
      Integer value = generator.nextInt(0, iterations);

      javaSet.remove(value);
      set3.remove(value);
      set10.remove(value);
      set100.remove(value);
      set1000.remove(value);
    }

    set3Comp.resetCompareCount();
    set10Comp.resetCompareCount();
    set100Comp.resetCompareCount();
    set1000Comp.resetCompareCount();
    javaSetComparator.resetCompareCount();

    toBeTested.forEach((value) -> {
      boolean set3Result = set3.contains(value);
      boolean set10Result = set10.contains(value);
      boolean set100Result = set100.contains(value);
      boolean set1000Result = set1000.contains(value);
      boolean javaResult = javaSet.contains(value);
      assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaResult, set3Result);
      assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaResult, set10Result);
      assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaResult, set100Result);
      assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaResult, set1000Result);
    });
    assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set3.size());
    assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set10.size());
    assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set100.size());
    assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set1000.size());

    System.out.println(
        "Compares after removal; current size: " + javaSet.size() + "; test values size: " + toBeTested.size());
    System.out.println("Comparisons count for BTree(factor 3): " + set3Comp.getCompareCount());
    System.out.println("Comparisons count for BTree(factor 10): " + set10Comp.getCompareCount());
    System.out.println("Comparisons count for BTree(factor 100): " + set100Comp.getCompareCount());
    System.out.println("Comparisons count for BTree(factor 1000): " + set1000Comp.getCompareCount());
    System.out.println("Comparisons count for Java Set: " + javaSetComparator.getCompareCount());
    System.out.println();
    System.out.println();
  }

  @Test
  public void testComparesCountInBenchmarkLikeDataString() {
    CountingComparatorWrapper<String> set3Comp = new CountingComparatorWrapper<String>(Comparator.naturalOrder());
    CountingComparatorWrapper<String> set10Comp = new CountingComparatorWrapper<String>(Comparator.naturalOrder());
    CountingComparatorWrapper<String> set100Comp = new CountingComparatorWrapper<String>(Comparator.naturalOrder());
    CountingComparatorWrapper<String> set1000Comp = new CountingComparatorWrapper<String>(Comparator.naturalOrder());
    CountingComparatorWrapper<String> javaSetComparator = new CountingComparatorWrapper<String>(Comparator.naturalOrder());
    BTreeSet<String> set3 = new BTreeSet<>(3, set3Comp);
    BTreeSet<String> set10 = new BTreeSet<>(10, set10Comp);
    BTreeSet<String> set100 = new BTreeSet<>(100, set100Comp);
    BTreeSet<String> set1000 = new BTreeSet<>(1000, set1000Comp);
    TreeSet<String> javaSet = new TreeSet<>(javaSetComparator);

    Random generator = new Random(1);
    List<Integer> targetIterations = List.of(10000, 1000000, 10000000);
    List<String> toBeTested = new ArrayList<>();
    List<String> toBeRemoved = new ArrayList<>();
    int targetTestSize = 10000;

    for (int iterations: targetIterations) {
      for (int i = 0; i < iterations; i++) {
        String value = generateRandomString(generator);
        if (toBeTested.size() < targetTestSize) {
          toBeTested.add(value);
        }
        if (generator.nextInt(2) == 0) {
          toBeRemoved.add(value);
        }

        javaSet.add(value);
        set3.add(value);
        set10.add(value);
        set100.add(value);
        set1000.add(value);
      }

      for(String value: toBeRemoved) {
        javaSet.remove(value);
        set3.remove(value);
        set10.remove(value);
        set100.remove(value);
        set1000.remove(value);
      }

      set3Comp.resetCompareCount();
      set10Comp.resetCompareCount();
      set100Comp.resetCompareCount();
      set1000Comp.resetCompareCount();
      javaSetComparator.resetCompareCount();

      assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set3.size());
      assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set10.size());
      assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set100.size());
      assertEquals("BTreeSet and TreeSet should have same size", javaSet.size(), set1000.size());

      for(String value: toBeTested) {
        boolean set3Result = set3.contains(value);
        boolean set10Result = set10.contains(value);
        boolean set100Result = set100.contains(value);
        boolean set1000Result = set1000.contains(value);
        boolean javaResult = javaSet.contains(value);
        assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaResult, set3Result);
        assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaResult, set10Result);
        assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaResult, set100Result);
        assertEquals("BTreeSet and TreeSet should respond same way on lookup", javaResult, set1000Result);
      }

      System.out.println(
          "Comparisons made to find " + toBeTested.size() + " keys in tree of " + javaSet.size() + "keys.");
      System.out.println("Comparisons count for BTree(factor 3): " + set3Comp.getCompareCount() + "; average "
          + (double) set3Comp.getCompareCount() / toBeTested.size());
      System.out.println("Comparisons count for BTree(factor 10): " + set10Comp.getCompareCount() + "; average "
      + (double) set10Comp.getCompareCount() / toBeTested.size());
      System.out.println("Comparisons count for BTree(factor 100): " + set100Comp.getCompareCount() + "; average "
      + (double) set100Comp.getCompareCount() / toBeTested.size());
      System.out.println("Comparisons count for BTree(factor 1000): " + set1000Comp.getCompareCount() + "; average "
      + (double) set1000Comp.getCompareCount() / toBeTested.size());
      System.out.println("Comparisons count for Java Set: " + javaSetComparator.getCompareCount() + "; average "
      + (double) javaSetComparator.getCompareCount() / toBeTested.size());
      System.out.println();
      System.out.println();
    }
  }

  private String generateRandomString(Random generator) {
    StringBuilder sb = new StringBuilder();

    for (int j = 0; j < 20; j++) {
      sb.append(words[generator.nextInt(0, words.length)]);
      sb.append(" ");
    }

    return sb.toString();
  }
}
