package com.carloship.io.git;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    /**
     * Verifica se a execução baseada em uma lista de tarefas assíncronas
     * é concluída corretamente e realiza exatamente 15 operações.
     *
     * O valor esperado ao final da execução é 15.
     */
    @Test
    void shouldCompleteListWith15Operations() {
        CompletableFuture<Integer> result = Main.fromList();

        assertEquals(15, result.join());
    }

    /**
     * Verifica se a execução baseada em uma cadeia de tarefas assíncronas
     * é concluída corretamente e realiza exatamente 15 operações.
     *
     * O valor esperado ao final da execução é 15.
     */
    @Test
    void shouldCompleteChainWith15Operations() {
        CompletableFuture<Integer> result = Main.fromChain();

        assertEquals(15, result.join());
    }

    /**
     * Verifica se a execução recursiva de tarefas assíncronas é concluída
     * corretamente após atingir o limite máximo de 15 operações.
     *
     * O valor esperado ao final da execução é 15.
     */
    @Test
    void shouldCompleteRecursiveWith15Operations() {
        CompletableFuture<Integer> result = Main.fromRecursive();

        assertEquals(15, result.join());
    }

    /**
     * Verifica se a execução recursiva é interrompida quando o número
     * máximo de tentativas já foi atingido.
     *
     * Ao iniciar com a tentativa 15, nenhuma nova operação deve ser
     * executada e o contador deve permanecer com o valor zero.
     */
    @Test
    void recursiveShouldStopAt15Attempts() {
        AtomicInteger counter = new AtomicInteger();

        CompletableFuture<Void> result =
                Main.fromRecursiveNode(
                        CompletableFuture.completedFuture(null),
                        counter,
                        15
                );

        assertEquals(0, counter.get());

        result.join();
    }

    /**
     * Verifica se a execução recursiva realiza todas as 15 operações
     * quando iniciada a partir da tentativa inicial, de valor zero.
     *
     * O valor esperado ao final da execução é 15.
     */
    @Test
    void recursiveShouldExecuteWhenAttemptIsBelow15() {
        AtomicInteger counter = new AtomicInteger();

        CompletableFuture<Void> result =
                Main.fromRecursiveNode(
                        CompletableFuture.completedFuture(null),
                        counter,
                        0
                );

        result.join();

        assertEquals(15, counter.get());
    }
}