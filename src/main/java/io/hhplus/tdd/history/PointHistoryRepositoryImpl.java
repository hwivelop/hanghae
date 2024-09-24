package io.hhplus.tdd.history;

import io.hhplus.tdd.database.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    @Override
    public List<PointHistory> findAllById(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
