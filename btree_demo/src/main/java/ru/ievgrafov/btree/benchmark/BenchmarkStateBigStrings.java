package ru.ievgrafov.btree.benchmark;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import ru.ievgrafov.btree.BTreeSet;

@State(Scope.Thread)
public class BenchmarkStateBigStrings {
    public final Comparator<String> comparator = Comparator.naturalOrder();

    BTreeSet<String> btree1000;
    BTreeSet<String> btree100;
    BTreeSet<String> btree10;
    BTreeSet<String> btree3;
    TreeSet<String> javaTree;
    String[] testValues;

    final String[] words = new String[]{
      "vivamus", "lectus", "purus", "sagittis", "vitae", "iaculis", "non", "porta", "at", "velit",
      "praesent", "quis", "enim", "a", "ligula", "semper", "ultricies", "sed", "vulputate", "ipsum",
      "sed", "tempor", "luctus", "sem", "metus", "suscipit", "tellus", "vehicula", "elit", "id", "nam",
      "fringilla", "congue", "ullamcorper", "condimentum", "risus", "et", "curabitur", "dictum", "est",
      "auctor", "etiam", "lacinia", "massa", "malesuada", "mauris", "vel", "leo", "feugiat", "aliquam",
      "libero", "aenean", "magna", "mi", "quisque", "volutpat", "aliquet", "felis", "tincidunt", "eu", "ut",
      "finibus", "urna", "egestas", "augue", "mollis", "nec", "in", "tristique", "in", "hac", "habitasse",
      "platea", "dictumst", "donec", "ut"
    };

    @Setup(Level.Iteration)
    public void setup() {
      testValues = new String[10000];
      btree1000 = new BTreeSet<>(1000, comparator);
      btree100 = new BTreeSet<>(100, comparator);
      btree10 = new BTreeSet<>(10, comparator);
      btree3 = new BTreeSet<>(3, comparator);
      javaTree = new TreeSet<>(comparator);
      int testedCount = 0;
      Random generator = new Random(1);
      List<String> toBeRemoved = new ArrayList<>();

      // Prepare data
      for (int i = 1000000; i > 0; i--) {
        StringBuilder sb = new StringBuilder();

        for(int j = 0; j < 20; j++) {
          sb.append(words[generator.nextInt(0, words.length)]);
          sb.append(" ");
        }

        String value = sb.toString();
        btree1000.add(value);
        btree100.add(value);
        btree10.add(value);
        btree3.add(value);
        javaTree.add(value);

        if (generator.nextInt(0, 2) == 0) {
          toBeRemoved.add(value);
        }

        if (testedCount < 10000) {
          testValues[testedCount] = value;
          testedCount++;
        }
      }

      for(String value : toBeRemoved) {
        btree1000.remove(value);
        btree100.remove(value);
        btree10.remove(value);
        btree3.remove(value);
        javaTree.remove(value);
      }
    }
}
