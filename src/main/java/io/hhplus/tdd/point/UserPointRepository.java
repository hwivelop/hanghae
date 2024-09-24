package io.hhplus.tdd.point;

import java.util.*;

public interface UserPointRepository {

    Optional<UserPoint> findById(Long id);

    UserPoint save(long id, long amount);
}
