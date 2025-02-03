package ru.ievgrafov.btree;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include("ru.ievgrafov.btree.benchmark.*")
                .build();
        new Runner(opt).run();
    }
}