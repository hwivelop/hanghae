package io.hhplus.tdd.point;

import io.hhplus.tdd.database.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointTable userPointTable;

    @Override
    public Optional<UserPoint> findById(Long id) {
        return Optional.of(userPointTable.selectById(id));
    }

    @Override
    public UserPoint save(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }
}
