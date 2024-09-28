package io.hhplus.tdd.point;

public interface UserPointRepository {

    UserPoint findByIdOrThrow(Long id);

    UserPoint save(long id, long amount);
}
