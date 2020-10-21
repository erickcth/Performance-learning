package com.perforamnce;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * // sysctl -a | grep cacheline
 * // hw.cachelinesize: 64
 * Each cache line has 64 byte in size
 *
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
public class FalseSharing {

    private static int opsIteration = 10000;

    @Benchmark
    public static void testFalseSharingPerformance() throws InterruptedException {
        final SimpleStructure s1 = new SimpleStructure();
        final SimpleStructure s2 = new SimpleStructure();

        final CountDownLatch latch = new CountDownLatch(2);

        new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < opsIteration; i++) {
                    s1.n += i;
                }
                latch.countDown();
            }
        }.run();;
        new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < opsIteration; i++) {
                    s2.n += i;
                }
                latch.countDown();
            }
        }.run();
        latch.await();
    }

    @Benchmark
    public static void testFalseSharingPaddedPerformance() throws InterruptedException {
        final PaddedStructure s1 = new PaddedStructure();
        final PaddedStructure s2 = new PaddedStructure();

        final CountDownLatch latch = new CountDownLatch(2);

        new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < opsIteration; i++) {
                    s1.n++;
                }
                latch.countDown();
            }
        }.run();;
        new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < opsIteration; i++) {
                    s2.n++;
                }
                latch.countDown();
            }
        }.run();
        latch.await();
    }

    static class SimpleStructure {
        long n;

        public SimpleStructure() {
        }

    }

    static class PaddedStructure {

//        @Contended // internal annotation on java8 and 9
        long n;
        long useless1, useless2, useless3, useless4, useless5, useless6, useless7 = 1L; // offset for 64 bytes cacheline

        public PaddedStructure() {
        }

        /**
         * Man, Java has Dead code elimination
         * because Java removed unused variable thats why it wasn't padding
         * @return
         */
        public long preventDeadCodeElimination() {
            return useless1 + useless2 + useless3 + useless4 + useless5 + useless6 + useless7;
        }

    }

}