package ru.ievgrafov.btree.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime) // Measure average execution time
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1) // 3 warmup iterations
@Measurement(iterations = 5, time = 1) // 5 measurement iterations
@Fork(2) // Run in 2 separate JVM processes
public class TreeLookupNotPresentBenchmark {

    @Benchmark
    public List<Boolean> lookupWithBTree1000(BenchmarkState state) {
      List<Boolean> result = new ArrayList<Boolean>();

      IntStream.range(150000, 190000).forEach((i) -> result.contains(state.btree1000.add(i)));

      return result;
    }

    @Benchmark
    public List<Boolean> lookupWithBTBTree100(BenchmarkState state) {
      List<Boolean> result = new ArrayList<Boolean>();

      IntStream.range(150000, 190000).forEach((i) -> result.contains(state.btree100.add(i)));

      return result;
    }

    @Benchmark
    public List<Boolean> lookupWithBTree10(BenchmarkState state) {
      List<Boolean> result = new ArrayList<Boolean>();

      IntStream.range(150000, 190000).forEach((i) -> result.contains(state.btree10.add(i)));

      return result;
    }

    @Benchmark
    public List<Boolean> lookupWithJavaTree(BenchmarkState state) {
      List<Boolean> result = new ArrayList<Boolean>();

      IntStream.range(150000, 190000).forEach((i) -> result.contains(state.javaTree.add(i)));

      return result;
    }
}
