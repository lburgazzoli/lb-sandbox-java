package com.github.lburgazzoli.sandbox.logging.logback;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private final static class RunnableLogger implements Runnable {
        private final Logger logger;
        private final int runs;
        private final String fmt;
        private final String fmtBase = " > val1={}, val2={}, val3={}";

        public RunnableLogger(int runs, int pad, String loggerName) {
            this.logger = LoggerFactory.getLogger(loggerName);
            this.runs = runs;
            this.fmt = StringUtils.rightPad(fmtBase, pad + fmtBase.length(), "X");
        }

        @Override
        public void run() {
            for (int i = 0; i < this.runs; i++) {
                this.logger.info(fmt,i, i * 7,i / 16);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final int RUNS = 1000000;
        final int THREADS = 10;

        for (int size : new int[]{ 64 }) {
            final ExecutorService es = Executors.newFixedThreadPool(THREADS);
            final long start = System.nanoTime();

            for (int t = 0; t < THREADS; t++) {
                es.submit(new RunnableLogger(RUNS, size, "perf-plain-vanilla-async"));
            }

            es.shutdown();
            es.awaitTermination(5, TimeUnit.MINUTES);

            final long time = System.nanoTime() - start;

            System.out.printf("MT (runs=%d, min size=%03d, elapsed=%.3f ms)): took an average of %.3f us per entry\n",
                    RUNS,
                    size,
                    time / 1e6,
                    time / 1e3 / (RUNS * THREADS)
            );
        }
    }
}
