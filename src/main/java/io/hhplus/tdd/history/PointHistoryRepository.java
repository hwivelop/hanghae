package io.hhplus.tdd.history;

import java.util.*;

public interface PointHistoryRepository {

    List<PointHistory> findAllById(long userId);
}
