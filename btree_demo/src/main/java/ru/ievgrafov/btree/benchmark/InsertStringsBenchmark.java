package ru.ievgrafov.btree.benchmark;

import org.openjdk.jmh.annotations.*;

public class InsertStringsBenchmark extends TreeBenchmark {
  @Benchmark
  public Boolean BTree1000(BenchmarkStateStrings state) {
    return addAll(state.testValues, state.btree1000);
  }

  @Benchmark
  public Boolean BTree100(BenchmarkStateStrings state) {
    return addAll(state.testValues, state.btree100);
  }

  @Benchmark
  public Boolean BTree10(BenchmarkStateStrings state) {
    return addAll(state.testValues, state.btree10);
  }

  @Benchmark
  public Boolean BTree3(BenchmarkState state) {
    return addAll(state.testValues, state.btree3);
  }

  @Benchmark
  public Boolean JavaTree(BenchmarkStateStrings state) {
    return addAll(state.testValues, state.javaTree);
  }
}
