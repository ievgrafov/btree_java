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
public class BenchmarkStateStrings {
    private final Comparator<String> stringComparator = Comparator.naturalOrder();

    BTreeSet<String> btree1000;
    BTreeSet<String> btree100;
    BTreeSet<String> btree10;
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
      btree1000 = new BTreeSet<>(1000, stringComparator);
      btree100 = new BTreeSet<>(100, stringComparator);
      btree10 = new BTreeSet<>(10, stringComparator);
      javaTree = new TreeSet<>(stringComparator);
      int missingCount = 0;
      int presentCount = 0;
      Random generator = new Random(1);

      // Prepare data
      for (int i = 100000; i > 0; i--) {
        StringBuilder sb = new StringBuilder();

        for(int j = 0; j < 20; j++) {
          sb.append(words[generator.nextInt(0, words.length)]);
          sb.append(" ");
        }

        String value = sb.toString();

        if (generator.nextInt(0, 5) == 0) {
          if (missingCount < 5000) {
            testValues[missingCount + presentCount] = value;
            missingCount++;
          }
        } else {
          btree1000.add(value);
          btree100.add(value);
          btree10.add(value);
          javaTree.add(value);

          if (presentCount < 5000) {
            testValues[missingCount + presentCount] = value;
            presentCount++;
          }
        }
      }
    }
}
