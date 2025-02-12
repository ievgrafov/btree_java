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
public class BenchmarkStateBig {
    private final Comparator<Integer> intComparator = Comparator.naturalOrder();

    final int targetSize = 500000;
    BTreeSet<Integer> btree1000;
    BTreeSet<Integer> btree100;
    BTreeSet<Integer> btree10;
    BTreeSet<Integer> btree2;
    TreeSet<Integer> javaTree;
    Integer[] testValues;

    @Setup(Level.Iteration)
    public void setup() {
      Random generator = new Random(3);
      testValues = new Integer[10000];
      int testValuesCount = 0;

      btree1000 = new BTreeSet<>(1000, intComparator);
      btree100 = new BTreeSet<>(100, intComparator);
      btree10 = new BTreeSet<>(10, intComparator);
      btree2 = new BTreeSet<>(2, intComparator);
      javaTree = new TreeSet<>(intComparator);

      // Prepare data
      for (int j = targetSize * 2; j > 0; j--) {
        int value = generator.nextInt(0, targetSize * 2);

        btree1000.add(value);
        btree100.add(value);
        btree10.add(value);
        btree2.add(value);
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
        btree2.remove(value);
        javaTree.remove(value);
      }

      // btree1000.resetComparisons();
      // btree100.resetComparisons();
      // btree10.resetComparisons();
      // btree2.resetComparisons();
    }
}
