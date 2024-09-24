package io.hhplus.tdd.point;

import io.hhplus.tdd.database.*;
import lombok.*;
import org.springframework.stereotype.*;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointTable userPointTable;

    @Override
    public UserPoint findById(Long id) {
        return userPointTable.selectById(id);
    }
}
