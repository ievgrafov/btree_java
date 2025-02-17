package ru.ievgrafov.btree.benchmark;

import org.openjdk.jmh.annotations.*;

public class LookupStringsBenchmark extends TreeBenchmark {
  @Benchmark
  public Boolean BTree1000(BenchmarkStateStrings state) {
    return checkContainsAll(state.testValues, state.btree1000);
  }

  @Benchmark
  public Boolean BTree100(BenchmarkStateStrings state) {
    return checkContainsAll(state.testValues, state.btree100);
  }

  @Benchmark
  public Boolean BTree10(BenchmarkStateStrings state) {
    return checkContainsAll(state.testValues, state.btree10);
  }

  @Benchmark
  public Boolean BTree3(BenchmarkStateStrings state) {
    return checkContainsAll(state.testValues, state.btree3);
  }

  @Benchmark
  public Boolean JavaTree(BenchmarkStateStrings state) {
    return checkContainsAll(state.testValues, state.javaTree);
  }



  // @Benchmark
  // public Boolean BigBTree1000(BenchmarkStateBigStrings state) {
  //   return checkContainsAll(state.testValues, state.btree1000);
  // }

  // @Benchmark
  // public Boolean BigBTree100(BenchmarkStateBigStrings state) {
  //   return checkContainsAll(state.testValues, state.btree100);
  // }

  // @Benchmark
  // public Boolean BigBTree10(BenchmarkStateBigStrings state) {
  //   return checkContainsAll(state.testValues, state.btree10);
  // }

  // @Benchmark
  // public Boolean BigBTree3(BenchmarkStateBigStrings state) {
  //   return checkContainsAll(state.testValues, state.btree3);
  // }

  // @Benchmark
  // public Boolean BigJavaTree(BenchmarkStateBigStrings state) {
  //   return checkContainsAll(state.testValues, state.javaTree);
  // }
}
