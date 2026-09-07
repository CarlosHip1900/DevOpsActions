package com.carloship.io.git;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final long TIME = System.currentTimeMillis();

    public static void main(String[] args)
            throws InterruptedException
    {
        fromList();
        fromChain();
        fromRecursive();

        Thread.sleep(Duration.MAX);
    }

    private static void fromChain()
    {
        CompletableFuture<Void> start = CompletableFuture.runAsync(() -> System.out.println("[CHAIN] Starting " + TIME));

        AtomicInteger integer = new AtomicInteger();

        for (int i = 0; i < 15; i++) {
            start.thenCompose(_ -> CompletableFuture.runAsync(() -> System.out.println("[CHAIN] Adding and Get " + integer.getAndIncrement())));
        }

        start.thenAccept(_ -> System.out.println("[CHAIN] OK Value = " + integer.get()));
    }

    private static void fromList()
    {
        System.out.println("[LIST] Starting " + TIME);
        List<CompletableFuture<Void>> cls = new ArrayList<>();

        AtomicInteger integer = new AtomicInteger();

        for (var anon = new Object() {
            int i = 0;
        }; anon.i < 15; anon.i++) {
            cls.add(CompletableFuture.runAsync(() -> System.out.println("[LIST] [" + anon.i + "] Adding and Get " + integer.getAndIncrement())));
        }

        CompletableFuture.allOf(cls.toArray(new CompletableFuture[0])).thenRun(() -> System.out.println("[LIST] OK Value = " + integer.get()));
    }

    private static void fromRecursive()
    {
        CompletableFuture<Void> start = CompletableFuture.runAsync(() -> System.out.println("[RECURSIVE] Starting " + TIME));

        AtomicInteger integer = new AtomicInteger();

        fromRecursiveNode(start, integer, 0);

        start.thenAccept(_ -> System.out.println("[RECURSIVE] OK Value = " + integer.get()));
    }

    private static CompletableFuture<Void> fromRecursiveNode
            (
                    CompletableFuture<Void> start,
                    AtomicInteger integer,
                    int attempt
            )
    {
        if (attempt >= 15) {
            return CompletableFuture.completedFuture(null);
        }

        return start.thenCompose(_ ->
                CompletableFuture.runAsync(() -> System.out.println("[RECURSIVE] Adding and Get " + integer.getAndIncrement()))
                        .thenCompose(_ -> fromRecursiveNode(start, integer, attempt + 1))
        );
    }
}
