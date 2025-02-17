package ru.ievgrafov.btree.benchmark;

import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import ru.ievgrafov.btree.BTreeSet;

@State(Scope.Thread)
public class BenchmarkStateHuge {
    public final Comparator<Integer> comparator = Comparator.naturalOrder();

    final int targetSize = 5000000;
    BTreeSet<Integer> btree1000;
    BTreeSet<Integer> btree100;
    BTreeSet<Integer> btree10;
    BTreeSet<Integer> btree3;
    TreeSet<Integer> javaTree;
    Integer[] testValues;

    @Setup(Level.Iteration)
    public void setup() {
      Random generator = new Random(3);
      testValues = new Integer[10000];
      int testValuesCount = 0;

      btree1000 = new BTreeSet<>(1000, comparator);
      btree100 = new BTreeSet<>(100, comparator);
      btree10 = new BTreeSet<>(10, comparator);
      btree3 = new BTreeSet<>(3, comparator);
      javaTree = new TreeSet<>(comparator);

      // Prepare data
      for (int j = targetSize * 2; j > 0; j--) {
        int value = generator.nextInt(0, targetSize * 2);

        btree1000.add(value);
        btree100.add(value);
        btree10.add(value);
        btree3.add(value);
        javaTree.add(value);

        if (testValuesCount < 10000) {
          testValues[testValuesCount] = value;
          testValuesCount++;
        }
      }

      for (int j = targetSize; j > 0; j--) {
        int value = generator.nextInt(0, targetSize * 2);

        btree1000.remove(value);
        btree100.remove(value);
        btree10.remove(value);
        btree3.remove(value);
        javaTree.remove(value);
      }
    }
}
