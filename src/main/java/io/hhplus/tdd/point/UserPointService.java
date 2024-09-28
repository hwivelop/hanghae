package io.hhplus.tdd.point;

import io.hhplus.tdd.history.*;
import io.hhplus.tdd.was.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.concurrent.locks.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryService pointHistoryService;

    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     * 특정 유저의 포인트를 조회하는 기능
     */
    public UserPoint getPointsById(long id) {

        return userPointRepository.findByIdOrThrow(id);
    }

    /**
     * 특정 유저의 포인트를 충전하는 기능
     */
    public UserPoint chargePointById(long id, long amount, long updateMillis) {

        lock.lock();

        try {
            // 유저 정보 조회
            UserPoint userPoint = this.getPointsById(id);

            UserPoint chargedUserPoint = userPoint.plusPointWithMaxPoint(amount);

            userPointRepository.save(id, chargedUserPoint.point());
            pointHistoryService.saveHistory(id, amount, TransactionType.CHARGE, updateMillis);

            return chargedUserPoint;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 특정 유저의 포인트를 사용하는 기능
     */
    public UserPoint usePointById(long id, long amount, long updateMillis) {

        lock.lock();

        try {
            UserPoint userPoint = this.getPointsById(id);

            UserPoint usedUserPoint = userPoint.minusPoint(amount);

            userPointRepository.save(id, usedUserPoint.point());
            pointHistoryService.saveHistory(id, amount, TransactionType.USE, updateMillis);

            return usedUserPoint;
        } finally {
            lock.unlock();
        }
    }
}
