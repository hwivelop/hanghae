package io.hhplus.tdd.history;

import io.hhplus.tdd.was.*;

import java.util.*;

public interface PointHistoryRepository {

    List<PointHistory> findAllById(long userId);

    PointHistory save(long userId, long chargePoint, TransactionType transactionType, long timeMillis);
}
