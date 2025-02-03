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
public class TreeLookupPresentBenchmark {

    @Benchmark
    public List<Boolean> lookupWithBTree12(BenchmarkState state) {
      List<Boolean> result = new ArrayList<Boolean>();

      IntStream.range(50000, 90000).forEach((i) -> result.contains(state.set12.add(i)));

      return result;
    }

    @Benchmark
    public List<Boolean> lookupWithBTBTree8(BenchmarkState state) {
      List<Boolean> result = new ArrayList<Boolean>();

      IntStream.range(50000, 90000).forEach((i) -> result.contains(state.set8.add(i)));

      return result;
    }

    @Benchmark
    public List<Boolean> lookupWithBTree10(BenchmarkState state) {
      List<Boolean> result = new ArrayList<Boolean>();

      IntStream.range(50000, 90000).forEach((i) -> result.contains(state.set10.add(i)));

      return result;
    }

    @Benchmark
    public List<Boolean> lookupWithJavaTree(BenchmarkState state) {
      List<Boolean> result = new ArrayList<Boolean>();

      IntStream.range(50000, 90000).forEach((i) -> result.contains(state.javaSet.add(i)));

      return result;
    }
}
