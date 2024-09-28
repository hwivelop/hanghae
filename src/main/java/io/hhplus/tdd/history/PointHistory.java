package io.hhplus.tdd.history;

import io.hhplus.tdd.was.*;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
