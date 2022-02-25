https://codegolf.stackexchange.com/questions/188133/bentleys-coding-challenge-k-most-frequent-words

`openjdk version "14" 2020-03-17
OpenJDK Runtime Environment (build 14+36-1461)
OpenJDK 64-Bit Server VM (build 14+36-1461, mixed mode, sharing)`
java -jar build/libs/trie-1.0-all.jar ../../giganovel.txt 100000  18.36s user 0.35s system 101% cpu 18.363 total

OpenJDK 14 with UseJVMCICompiler
java -XX:+UnlockExperimentalVMOptions -XX:+UseJVMCICompiler -Xmx512m -Xms512m  12.99s user 0.64s system 146% cpu 9.314 total

`openjdk version "13.0.2" 2020-01-14
OpenJDK Runtime Environment Zulu13.29+9-CA (build 13.0.2+6-MTS)
OpenJDK 64-Bit Server VM Zulu13.29+9-CA (build 13.0.2+6-MTS, mixed mode, sharing)`
java -jar build/libs/trie-1.0-all.jar ../../giganovel.txt 100000  18.42s user 0.34s system 102% cpu 18.399 total

GraalVM CE test results
`openjdk version "11.0.6" 2020-01-14`
`OpenJDK Runtime Environment GraalVM CE 20.0.0 (build 11.0.6+9-jvmci-20.0-b02)`
`OpenJDK 64-Bit Server VM GraalVM CE 20.0.0 (build 11.0.6+9-jvmci-20.0-b02, mixed mode, sharing)`

java -jar build/libs/trie-1.0-all.jar ulysses64 10  1.16s user 0.10s system 138% cpu 0.912 total

java -jar build/libs/trie-1.0-all.jar ../../giganovel.txt 100000  8.60s user 0.49s system 103% cpu 8.794 total

The remaining tests use GraalVM Enterprise Edition:

`java version "11.0.6" 2020-01-14 LTS`
` Java(TM) SE Runtime Environment GraalVM EE 20.0.0 (build 11.0.6+8-LTS-jvmci-20.0-b02)`
` Java HotSpot(TM) 64-Bit Server VM GraalVM EE 20.0.0 (build 11.0.6+8-LTS-jvmci-20.0-b02, mixed mode, sharing)`

java -jar build/libs/trie-1.0-all.jar ulysses64 10  1.18s user 0.09s system 137% cpu 0.917 total

java -jar build/libs/trie-1.0-all.jar ../../giganovel.txt 100000  9.29s user 0.49s system 108% cpu 8.975 total

The PGO options require the enterprise edition of GraalVM with `native-image`
https://www.graalvm.org/docs/reference-manual/native-image/

Note: `native-image` doesn't seem to respect the PATH (it was using native-image from the community edition),
 so we prefix with $JAVA_HOME

Compile native image with instrumentation
`$JAVA_HOME/bin/native-image --pgo-instrument -jar build/libs/trie-1.0-all.jar`

Run the app to generate the profiling data
`./trie-1.0-all giganovel 100000`

This will be slow - time shows `4.50s user 0.04s system 94% cpu 4.800 total`

This command generates a file `default.iprof` that is used in the next step to generate the 
AOT optimized binary

Compile 
`$JAVA_HOME/bin/native-image --pgo -jar build/libs/trie-1.0-all.jar`

Results for optimized app:

./trie-1.0-all ulysses64 10  1.96s user 0.03s system 99% cpu 1.995 total

`time ./trie-1.0-all ulysses64 10`

time now shows `0.60s user 0.02s system 99% cpu 0.632 total`


csharp precompiled
bin/Release/netcoreapp3.1/osx.10.15-x64/publish/csharptrie ../giganovel.txt   9.37s user 0.22s system 98% cpu 9.749 total

rust
target/release/rust-trie ../giganovel.txt 100000  2.50s user 0.43s system 98% cpu 2.979 total