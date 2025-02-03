### Benchmark                                         Mode  Cnt        Score        Error  Units

```
TreeInsertExistingBenchmark.insertWithBTree10     avgt   10  4620975.652 ± 205276.672  ns/op
TreeInsertExistingBenchmark.insertWithJavaTree    avgt   10  4901666.827 ± 325141.585  ns/op

TreeInsertNewBenchmark.insertWithBTree10          avgt   10  4775421.112 ± 239930.136  ns/op
TreeInsertNewBenchmark.insertWithJavaTree         avgt   10  5584181.462 ± 468501.208  ns/op

TreeLookupNotPresentBenchmark.lookupWithBTree10   avgt   10  4953783.608 ± 253304.321  ns/op
TreeLookupNotPresentBenchmark.lookupWithJavaTree  avgt   10  5599560.006 ± 230232.163  ns/op

TreeLookupPresentBenchmark.lookupWithBTree10      avgt   10  4559264.105 ± 178897.832  ns/op
TreeLookupPresentBenchmark.lookupWithJavaTree     avgt   10  4725185.424 ± 167498.285  ns/op
```
