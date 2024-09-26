package io.hhplus.tdd.point;

import java.util.*;

public interface UserPointRepository {

    UserPoint findById(Long id);

    UserPoint save(long id, long amount);
}
