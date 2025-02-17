package ru.ievgrafov.btree.benchmark;

import java.util.Set;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = TreeBenchmark.WARMUP_ITERATIONS, time = 1)
@Measurement(iterations = TreeBenchmark.ITERATIONS, time = 1)
@Fork(TreeBenchmark.FORK)
public class TreeBenchmark {
  public static final int WARMUP_ITERATIONS = 3;
  public static final int ITERATIONS = 10;
  public static final int FORK = 10;

  public <T> boolean addAll(T[] values, Set<T> container) {
    Boolean result = true;
    int listSize = values.length;

    for (int i = 0; i < listSize; i++) {
      result = result == container.add(values[i]);
    }

    return result;
  }

  public <T> boolean checkContainsAll(T[] values, Set<T> container) {
    Boolean result = true;
    int listSize = values.length;

    for (int i = 0; i < listSize; i++) {
      result = result == container.contains(values[i]);
    }

    return result;
  }

  public <T> boolean removeAll(T[] values, Set<T> container) {
    Boolean result = true;
    int listSize = values.length;

    for (int i = 0; i < listSize; i++) {
      result = result == container.remove(values[i]);
    }

    return result;
  }
}
