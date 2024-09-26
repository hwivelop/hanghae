package io.hhplus.tdd.point;

import io.hhplus.tdd.history.*;
import io.hhplus.tdd.was.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryService pointHistoryService;

    /**
     * 특정 유저의 포인트를 조회하는 기능
     */
    public UserPoint getPointsByIdOrThrow(long id) {

        return userPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디가 존재하지 않습니다."));
    }

    /**
     * 특정 유저의 포인트를 충전하는 기능
     */
    public UserPoint chargePointById(long id, long amount, long updateMillis) {

        // 유저 검증
        UserPoint userPoint = this.getPointsByIdOrThrow(id);

        UserPoint chargedUserPoint = userPoint.plusPointWithMaxPoint(amount);

        userPointRepository.save(id, chargedUserPoint.point());
        pointHistoryService.saveHistory(id, amount, TransactionType.CHARGE, updateMillis);

        return chargedUserPoint;
    }

    /**
     * 특정 유저의 포인트를 사용하는 기능
     */
    public UserPoint usePointById(long id, long amount, long updateMillis) {

        // 유저 검증
        UserPoint userPoint = this.getPointsByIdOrThrow(id);

        UserPoint usedUserPoint = userPoint.minusPoint(amount);

        userPointRepository.save(id, usedUserPoint.point());
        pointHistoryService.saveHistory(id, amount, TransactionType.USE, updateMillis);

        return usedUserPoint;
    }
}
