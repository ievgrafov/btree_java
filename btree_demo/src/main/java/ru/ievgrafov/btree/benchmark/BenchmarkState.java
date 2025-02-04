package ru.ievgrafov.btree.benchmark;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.IntStream;

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
    TreeSet<Integer> javaTree;

    @Setup(Level.Trial)
    public void setup() {
      btree1000 = new BTreeSet<>(1000, intComparator);
      btree100 = new BTreeSet<>(100, intComparator);
      btree10 = new BTreeSet<>(10, intComparator);
      javaTree = new TreeSet<>(intComparator);

      // Prepare data
      for (int i = 100000; i > 0; i--) {
        btree1000.add(i);
        btree100.add(i);
        btree10.add(i);
        javaTree.add(i);
      }
    }
}