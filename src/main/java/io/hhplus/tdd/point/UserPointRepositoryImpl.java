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
    public UserPoint findByIdOrThrow(Long id) {
        return Optional.of(userPointTable.selectById(id))
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디가 존재하지 않습니다."));
    }

    @Override
    public UserPoint save(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }
}
