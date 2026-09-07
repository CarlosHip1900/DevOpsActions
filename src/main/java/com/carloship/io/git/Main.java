package com.carloship.io.git;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final long TIME = System.currentTimeMillis();

    public static void main(String[] args) {
        fromList();
        fromChain();
        fromRecursive();
    }

    private static void fromChain() {
        CompletableFuture<Void> start = CompletableFuture.runAsync(() -> System.out.println("[CHAIN] Starting " + TIME));

        AtomicInteger integer = new AtomicInteger();

        for (int i = 0; i < 15; i++) {
            start.thenCompose(v -> CompletableFuture.runAsync(() -> System.out.println("[CHAIN] Adding and Get " + integer.getAndIncrement())));
        }

        start.thenAccept(_ -> {
            System.out.println("[CHAIN] OK Value = " + integer.get());
        });
    }

    private static void fromList() {
        System.out.println("[LIST] Starting " + TIME);
        List<CompletableFuture<Void>> cls = new ArrayList<>();

        AtomicInteger integer = new AtomicInteger();

        for (int i = 0; i < 15; i++) {
            cls.add(CompletableFuture.runAsync(() -> System.out.println("[LIST] Adding and Get " + integer.getAndIncrement())));
        }

        CompletableFuture.allOf(cls.toArray(new CompletableFuture[0])).thenRun(() -> {
            System.out.println("[LIST] OK Value = " + integer.get());
        });
    }

    private static void fromRecursive() {
        CompletableFuture<Void> start = CompletableFuture.runAsync(() -> System.out.println("[RECURSIVE] Starting " + TIME));

        AtomicInteger integer = new AtomicInteger();

        fromRecursiveNode(start, integer);

        start.thenAccept(_ -> System.out.println("[RECURSIVE] OK Value = " + integer.get()));
    }

    private static CompletableFuture<Void> fromRecursiveNode(
            CompletableFuture<Void> start,
            AtomicInteger integer) {

        return start.thenCompose(v ->
                CompletableFuture.runAsync(() ->
                        System.out.println(
                                "[RECURSIVE] Adding and Get " + integer.getAndIncrement()
                        )
                ).thenCompose(_ ->
                        fromRecursiveNode(start, integer)
                )
        );
    }
}
