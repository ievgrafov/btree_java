package ru.ievgrafov.btree.benchmark;

import org.openjdk.jmh.annotations.*;

public class LookupBenchmark extends TreeBenchmark {
  @Benchmark
  public Boolean BTree1000(BenchmarkState state) {
    return checkContainsAll(state.testValues, state.btree1000);
  }

  @Benchmark
  public Boolean BTree100(BenchmarkState state) {
    return checkContainsAll(state.testValues, state.btree100);
  }

  @Benchmark
  public Boolean BTree10(BenchmarkState state) {
    return checkContainsAll(state.testValues, state.btree10);
  }

  @Benchmark
  public Boolean BTree3(BenchmarkState state) {
    return checkContainsAll(state.testValues, state.btree3);
  }

  @Benchmark
  public Boolean JavaTree(BenchmarkState state) {
    return checkContainsAll(state.testValues, state.javaTree);
  }


  // @Benchmark
  // public Boolean BigBTree1000(BenchmarkStateBig state) {
  //   return checkContainsAll(state.testValues, state.btree1000);
  // }

  // @Benchmark
  // public Boolean BigBTree100(BenchmarkStateBig state) {
  //   return checkContainsAll(state.testValues, state.btree100);
  // }

  // @Benchmark
  // public Boolean BigBTree10(BenchmarkStateBig state) {
  //   return checkContainsAll(state.testValues, state.btree10);
  // }

  // @Benchmark
  // public Boolean BigBTree1(BenchmarkStateBig state) {
  //   return checkContainsAll(state.testValues, state.btree3);
  // }

  // @Benchmark
  // public Boolean BigJavaTree(BenchmarkStateBig state) {
  //   return checkContainsAll(state.testValues, state.javaTree);
  // }


  // @Benchmark
  // public Boolean HugeBTree1000(BenchmarkStateHuge state) {
  //   return checkContainsAll(state.testValues, state.btree1000);
  // }

  // @Benchmark
  // public Boolean HugeBTree100(BenchmarkStateHuge state) {
  //   return checkContainsAll(state.testValues, state.btree100);
  // }

  // @Benchmark
  // public Boolean HugeBTree10(BenchmarkStateHuge state) {
  //   return checkContainsAll(state.testValues, state.btree10);
  // }

  // @Benchmark
  // public Boolean HugeBTree1(BenchmarkStateHuge state) {
  //   return checkContainsAll(state.testValues, state.btree3);
  // }

  // @Benchmark
  // public Boolean HugeJavaTree(BenchmarkStateHuge state) {
  //   return checkContainsAll(state.testValues, state.javaTree);
  // }
}
