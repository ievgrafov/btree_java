package ru.ievgrafov.btree.benchmark;

import org.openjdk.jmh.annotations.*;

public class InsertBenchmark extends TreeBenchmark {
  @Benchmark
  public Boolean BTree1000(BenchmarkState state) {
    return addAll(state.testValues, state.btree1000);
  }

  @Benchmark
  public Boolean BTree100(BenchmarkState state) {
    return addAll(state.testValues, state.btree100);
  }

  @Benchmark
  public Boolean BTree10(BenchmarkState state) {
    return addAll(state.testValues, state.btree10);
  }

  @Benchmark
  public Boolean BTree3(BenchmarkState state) {
    return addAll(state.testValues, state.btree3);
  }

  @Benchmark
  public Boolean JavaTree(BenchmarkState state) {
    return addAll(state.testValues, state.javaTree);
  }
}
