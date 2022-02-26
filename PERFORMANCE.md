https://codegolf.stackexchange.com/questions/188133/bentleys-coding-challenge-k-most-frequent-words

## java results 

```
openjdk version "17.0.2" 2022-01-18 LTS
OpenJDK Runtime Environment Zulu17.32+13-CA (build 17.0.2+8-LTS)
OpenJDK 64-Bit Server VM Zulu17.32+13-CA (build 17.0.2+8-LTS, mixed mode, sharing)
```

time java -jar build/libs/trie-1.0-all.jar ../giganovel 100000  
8.33s user 0.25s system 101% cpu 8.453 total


## GraalVM CE test results
```
openjdk version "17.0.2" 2022-01-18
OpenJDK Runtime Environment GraalVM CE 22.0.0.2 (build 17.0.2+8-jvmci-22.0-b05)
OpenJDK 64-Bit Server VM GraalVM CE 22.0.0.2 (build 17.0.2+8-jvmci-22.0-b05, mixed mode, sharing)
```

time java -jar build/libs/trie-1.0-all.jar ../ulysses64 10  
0.86s user 0.03s system 85% cpu 1.038 total

time java -jar build/libs/trie-1.0-all.jar ../giganovel 100000  
7.34s user 0.30s system 104% cpu 7.279 total

## GraalVM Enterprise Edition:

```
java version "17.0.2" 2022-01-18 LTS
Java(TM) SE Runtime Environment GraalVM EE 22.0.0.2 (build 17.0.2+8-LTS-jvmci-22.0-b05)
Java HotSpot(TM) 64-Bit Server VM GraalVM EE 22.0.0.2 (build 17.0.2+8-LTS-jvmci-22.0-b05, mixed mode, sharing)
```

time java -jar build/libs/trie-1.0-all.jar ../ulysses64 10  
0.86s user 0.02s system 83% cpu 1.060 total

time java -jar build/libs/trie-1.0-all.jar ../giganovel 100000  
7.91s user 0.26s system 104% cpu 7.848 total

## GraalVM EE optimized native-image 

The PGO options require the enterprise edition of GraalVM with `native-image`
Requires Oracle Login
https://www.oracle.com/downloads/graalvm-downloads.html

Compile native image with instrumentation  
`native-image --pgo-instrument -jar build/libs/trie-1.0-all.jar`

Run the app to generate the profiling data  
`./trie-1.0-all ../giganovel 100000`

This will be slow - time shows `4.50s user 0.04s system 94% cpu 4.800 total`

This command generates a file `default.iprof` that is used in the next step to generate the 
AOT optimized binary

Compile 
`$JAVA_HOME/bin/native-image --pgo -jar build/libs/trie-1.0-all.jar`

Results for optimized app:

time ./trie-1.0-all ../giganovel 100000  
4.14s user 0.26s system 96% cpu 4.582 total

time ./trie-1.0-all ../ulysses64 10  
0.35s user 0.02s system 99% cpu 0.366 total

## csharp precompiled
bin/Release/net6.0/csharptrie ../giganovel 100000  
8.16s user 0.23s system 97% cpu 8.627 total

time bin/Release/net6.0/csharptrie ../ulysses64 10  
0.65s user 0.02s system 96% cpu 0.704 total

## rust
time target/release/rust-trie ../giganovel 100000  
1.81s user 0.53s system 88% cpu 2.637 total

time target/release/rust-trie ../ulysses64 10  
0.25s user 0.05s system 99% cpu 0.302 total

## python

time python3 ./trie.py  # (giganovel)
39.68s user 0.23s system 99% cpu 40.186 total
