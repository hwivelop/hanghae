package io.hhplus.tdd.point;

import io.hhplus.tdd.history.*;
import io.hhplus.tdd.was.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UseUserPointServiceIntegrationTest {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private PointHistoryService pointHistoryService;


    private final long initPoint = 1000L;


    @DisplayName("특정 유저의 포인트를 사용한다.")
    @Test
    void usePoint() {

        final long userId = 1L;

        // given
        final long usePoint = 10L;
        final long totalPoint = initPoint - usePoint;
        final long timeMillis = System.currentTimeMillis();

        userPointService.chargePointById(userId, initPoint, timeMillis);

        // when
        UserPoint useUserPoint = userPointService.usePointById(userId, usePoint, timeMillis);
        List<PointHistory> histories = pointHistoryService.getHistoriesById(userId);

        // then
        // 사용된 포인트는 토탈 포인트와 같다.
        assertThat(useUserPoint.point()).isEqualTo(totalPoint);

        // 사용된 히스토리를 조회할 수 있다.
        assertThat(histories).hasSize(2)
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(userId, initPoint, TransactionType.CHARGE), // 초기화
                        tuple(userId, usePoint, TransactionType.USE)
                );
    }

    @DisplayName("마이너스 금액을 사용할 때 예외가 발생한다.")
    @Test
    void useMinusPoint() {
        //given
        final long userId = 2L;
        final long usePoint = -2L;
        final long timeMillis = System.currentTimeMillis();

        userPointService.chargePointById(userId, initPoint, timeMillis);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.usePointById(userId, usePoint, timeMillis);
        });

        //then
        assertEquals("차감할 포인트는 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("보유 포인트 보다 큰 포인트를 사용할 때 예외가 발생한다.")
    void chargeZeroPoint() {

        //given
        final long userId = 3L;
        final long usePoint = 10000L;
        final long timeMillis = System.currentTimeMillis();

        userPointService.chargePointById(userId, initPoint, timeMillis);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.usePointById(userId, usePoint, timeMillis);
        });

        //then
        assertEquals("차감할 포인트가 부족합니다.", exception.getMessage());
    }
}
