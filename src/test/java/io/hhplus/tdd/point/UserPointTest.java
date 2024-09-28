package io.hhplus.tdd.point;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserPointTest {

    @Test
    @DisplayName("인입된 충전 금액을 가진 UserPoint 를 반환한다.")
    void plusPointWithMaxPoint() {

        //given
        final long userId = 1L;
        final long point = 0L;
        final long chargePoint = 500L;
        final long resultPoint = 500L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

        //when
        UserPoint chargeUserPoint = userPoint.plusPointWithMaxPoint(chargePoint);

        //then
        assertThat(chargeUserPoint.point()).isEqualTo(resultPoint);
    }

    @Test
    @DisplayName("최대 금액을 초과한 금액을 충전하면 예외가 발상핸다.")
    void plusPointWithMaxPointWithMaxPoint() {

        //given
        final long userId = 1L;
        final long point = 10000L;
        final long chargePoint = 1L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.plusPointWithMaxPoint(chargePoint);
        });

        //then
        assertEquals("충전할 수 있는 최대 포인트를 초과 했습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("0원을 충전하면 예외가 발상핸다.")
    void plusPointWithMaxPointWithZeroPoint() {

        //given
        final long userId = 1L;
        final long point = 10000L;
        final long chargePoint = 0L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.plusPointWithMaxPoint(chargePoint);
        });

        //then
        assertEquals("마이너스 금액 또는 0원을 충전할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("마이너스 금액을 충전하면 예외가 발상핸다.")
    void plusPointWithMaxPointWithMinusPoint() {

        //given
        final long userId = 1L;
        final long point = 10000L;
        final long chargePoint = -1L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.plusPointWithMaxPoint(chargePoint);
        });

        //then
        assertEquals("마이너스 금액 또는 0원을 충전할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("인입된 사용 금액을 가진 UserPoint 를 반환한다.")
    void minusPoint() {

        //given
        final long userId = 1L;
        final long point = 1000L;
        final long usePoint = 200L;
        final long resultPoint = 800L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

        //when
        UserPoint useUserPoint = userPoint.minusPoint(usePoint);

        //then
        assertThat(useUserPoint.point()).isEqualTo(resultPoint);
    }

    @Test
    @DisplayName("보유 금액 이상 포인트 사용 시 예외가 발상핸다.")
    void minusPointWithOverPoint() {

        //given
        final long userId = 1L;
        final long point = 10L;
        final long usePoint = 11L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.minusPoint(usePoint);
        });

        //then
        assertEquals("차감할 포인트가 부족합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("0원을 사용 하면 예외가 발상핸다.")
    void minusPointWithZeroPoint() {

        //given
        final long userId = 1L;
        final long point = 0L;
        final long usePoint = 0L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.minusPoint(usePoint);
        });

        //then
        assertEquals("차감할 포인트는 0보다 커야 합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("마이너스 사용 하면 예외가 발상핸다.")
    void minusPointWithMinusPoint() {

        //given
        final long userId = 1L;
        final long point = 10000L;
        final long usePoint = -1L;
        UserPoint userPoint = new UserPoint(userId, point, System.currentTimeMillis());

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPoint.minusPoint(usePoint);
        });

        //then
        assertEquals("차감할 포인트는 0보다 커야 합니다.", exception.getMessage());
    }
}