package com.carloship.io.git;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final long TIME = System.currentTimeMillis();
    private static final int MAX_ATTEMPTS = 15;

    public static void main(String[] args) throws InterruptedException {
        fromList();
        fromChain();
        fromRecursive();

        Thread.sleep(Duration.MAX);
    }

    static CompletableFuture<Integer> fromChain() {
        CompletableFuture<Void> start = CompletableFuture.runAsync(
                () -> System.out.println("[CHAIN] Starting " + TIME)
        );

        AtomicInteger integer = new AtomicInteger();

        CompletableFuture<Void> result = start;

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            result = result.thenCompose(
                    _ -> CompletableFuture.runAsync(
                            () -> System.out.println("[CHAIN] Adding and Get " + integer.getAndIncrement())
                    )
            );
        }

        return result.thenApply(_ -> integer.get());
    }

    static CompletableFuture<Integer> fromList() {
        System.out.println("[LIST] Starting " + TIME);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        AtomicInteger integer = new AtomicInteger();

        for (var anon = new Object() {
            int i = 0;
        }; anon.i < MAX_ATTEMPTS; anon.i++) {

            futures.add(
                    CompletableFuture.runAsync(() -> System.out.println("[LIST] [" + anon.i + "] Adding and Get " + integer.getAndIncrement()))
            );
        }

        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(_ -> integer.get());
    }

    static CompletableFuture<Integer> fromRecursive()
    {
        CompletableFuture<Void> start = CompletableFuture.runAsync(() -> System.out.println("[RECURSIVE] Starting " + TIME));

        AtomicInteger integer = new AtomicInteger();

        return fromRecursiveNode(start, integer, 0).thenApply(_ -> integer.get());
    }

    static CompletableFuture<Void> fromRecursiveNode(CompletableFuture<Void> start, AtomicInteger integer, int attempt)
    {
        if (attempt >= MAX_ATTEMPTS) {
            return CompletableFuture.completedFuture(null);
        }

        return start.thenCompose(
                _ -> CompletableFuture.runAsync(
                        () -> System.out.println("[RECURSIVE] Adding and Get " + integer.getAndIncrement())
                ).thenCompose(
                        _ -> fromRecursiveNode(start, integer, attempt + 1)
                )
        );
    }
}