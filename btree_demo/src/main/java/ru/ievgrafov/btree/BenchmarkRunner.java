package ru.ievgrafov.btree;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import ru.ievgrafov.btree.benchmark.LookupStringsBenchmark;
import ru.ievgrafov.btree.benchmark.RemoveStringsBenchmark;
import ru.ievgrafov.btree.benchmark.InsertStringsBenchmark;
import ru.ievgrafov.btree.benchmark.LookupBenchmark;


public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                // .include("ru.ievgrafov.btree.benchmark.*")
                // .include(LookupStringsBenchmark.class.getSimpleName())
                .include(LookupBenchmark.class.getSimpleName())
                // .include(RemoveStringsBenchmark.class.getSimpleName())
                // .include(InsertStringsBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
