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
public class BenchmarkState {
    private final Comparator<Integer> intComparator = Comparator.naturalOrder();

    BTreeSet<Integer> btree1000;
    BTreeSet<Integer> btree100;
    BTreeSet<Integer> btree10;
    BTreeSet<Integer> btree1;
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
      btree1 = new BTreeSet<>(1, intComparator);
      javaTree = new TreeSet<>(intComparator);

      // Prepare data
      for (int j = 100000; j > 0; j--) {
        int value = generator.nextInt(0, 100000);

        btree1000.add(value);
        btree100.add(value);
        btree10.add(value);
        btree1.add(value);
        javaTree.add(value);

        if (testValuesCount < 10000) {
          testValues[testValuesCount] = value;
          testValuesCount++;
        }
      }

      for (int j = 50000; j > 0; j--) {
        int value = generator.nextInt(0, 100000);

        btree1000.remove(value);
        btree100.remove(value);
        btree10.remove(value);
        btree1.remove(value);
        javaTree.remove(value);
      }
    }
}
