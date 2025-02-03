package ru.ievgrafov.btree.benchmark;

import java.util.Comparator;
import java.util.TreeSet;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import ru.ievgrafov.btree.BTreeSet;

@State(Scope.Thread)
public class BenchmarkState {
    private final Comparator<Integer> intComparator = Comparator.naturalOrder();

    BTreeSet<Integer> set12;
    BTreeSet<Integer> set8;
    BTreeSet<Integer> set10;
    TreeSet<Integer> javaSet;

    @Setup(Level.Trial)
    public void setup() {
      set12 = new BTreeSet<>(12, intComparator);
      set8 = new BTreeSet<>(8, intComparator);
      set10 = new BTreeSet<>(10, intComparator);
      javaSet = new TreeSet<>(intComparator);

      // Prepare data
      for (int i = 100000; i > 0; i--) {
        set12.add(i);
        set8.add(i);
        set10.add(i);
        javaSet.add(i);
      }
    }
}