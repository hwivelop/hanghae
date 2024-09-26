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
public class ChargeUserPointServiceIntegrationTest {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private PointHistoryService pointHistoryService;

    private final long userId = 1L;
    private final long initPoint = 1L;


    @DisplayName("특정 유저의 포인트를 충전한다.")
    @Test
    void usePoint() {

        // given
        final long chargePoint = 30L;
        final long totalPoint = chargePoint + initPoint;
        final long timeMillis = System.currentTimeMillis();

        // 초기 유저 정보 저장
        userPointService.chargePointById(userId, initPoint, timeMillis);

        // when
        UserPoint chargeUserPoint = userPointService.chargePointById(userId, chargePoint, timeMillis);
        List<PointHistory> histories = pointHistoryService.getHistoriesById(userId);

        // then

        // 충전 포인트는 토탈 포인트와 같다.
        assertThat(chargeUserPoint.point()).isEqualTo(totalPoint);
        // 충전 히스토리를 조회할 수 있다.
        assertThat(histories).hasSize(2) //초기화 개수 추가
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(userId, initPoint, TransactionType.CHARGE),
                        tuple(userId, chargePoint, TransactionType.CHARGE)
                );
    }

    @DisplayName("마이너스 금액을 충전할 때 예외가 발생한다.")
    @Test
    void useMinusPoint() {

        //given
        final long chargePoint = -1L;
        final long timeMillis = System.currentTimeMillis();

        // 초기 유저 정보 저장
        userPointService.chargePointById(userId, initPoint, timeMillis);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.chargePointById(userId, chargePoint, timeMillis);
        });

        //then
        assertEquals("마이너스 금액 또는 0원을 충전할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("보유 포인트 보다 큰 포인트를 사용할 때 예외가 발생한다.")
    void chargeZeroPoint() {

        //given
        final long chargePoint = 10000L;
        final long timeMillis = System.currentTimeMillis();

        // 초기 유저 정보 저장
        userPointService.chargePointById(userId, initPoint, timeMillis);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.chargePointById(userId, chargePoint, timeMillis);
        });

        //then
        assertEquals("충전할 수 있는 최대 포인트를 초과 했습니다.", exception.getMessage());
    }
}
