package io.hhplus.tdd.point;

import io.hhplus.tdd.history.*;
import io.hhplus.tdd.was.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargeUserPointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryService pointHistoryService;

    @InjectMocks
    private UserPointService userPointService;

    @Test
    @DisplayName("특정 유저의 포인트를 충전한다.")
    void chargePoint() {

        //given
        final long userId = 1L;
        final long originalPoint = 1L;
        final long chargePoint = 9999L;
        final long totalPoint = 10000L;
        final long timeMillis = System.currentTimeMillis();

        UserPoint userPointMock = new UserPoint(userId, originalPoint, timeMillis);
        PointHistory pointHistoryMock = new PointHistory(1L, userId, chargePoint, TransactionType.CHARGE, timeMillis);

        //when
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPointMock));
        when(pointHistoryService.saveHistory(userId, chargePoint, TransactionType.CHARGE, timeMillis)).thenReturn(pointHistoryMock);

        UserPoint userPoint = userPointService.chargePointById(userId, chargePoint, timeMillis);

        //then
        assertThat(userPoint.point()).isEqualTo(totalPoint);
    }

    @Test
    @DisplayName("포인트 충전 시 존재하지 않는 유저의 포인트를 충전하면 예외가 발생한다.")
    void chargeUnknownId() {

        //given
        final long userId = 1L;
        final long chargePoint = 10000L;
        final long timeMillis = System.currentTimeMillis();

        //when
        when(userPointRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.chargePointById(userId, chargePoint, timeMillis);
        });

        //then
        assertEquals("유저 아이디가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("포인트 충전 시 최대 포인트 이상 충전할 때 예외가 발생한다.")
    void chargeOverMaxPoint() {

        //given
        final long userId = 1L;
        final long originalPoint = 1L;
        final long overChargePoint = 10000L;
        final long timeMillis = System.currentTimeMillis();

        UserPoint userPointMock = new UserPoint(userId, originalPoint, timeMillis);

        //when
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPointMock));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.chargePointById(userId, overChargePoint, timeMillis);
        });

        //then
        assertEquals("충전할 수 있는 최대 포인트를 초과 했습니다.", exception.getMessage());

        // 예외로 인해 히스토리 저장 로직은 호출되지 않아야 한다.
        verify(pointHistoryService, never()).saveHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }

    @Test
    @DisplayName("마이너스 금액을 충전할 때 예외가 발생한다.")
    void chargeMinusPoint() {

        //given
        final long userId = 1L;
        final long originalPoint = 1L;
        final long minusChargePoint = -1L;
        final long timeMillis = System.currentTimeMillis();

        UserPoint userPointMock = new UserPoint(userId, originalPoint, timeMillis);

        //when
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPointMock));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.chargePointById(userId, minusChargePoint, timeMillis);
        });

        //then
        assertEquals("마이너스 금액 또는 0원을 충전할 수 없습니다.", exception.getMessage());

        // 예외로 인해 히스토리 저장 로직은 호출되지 않아야 한다.
        verify(pointHistoryService, never()).saveHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }

    @Test
    @DisplayName("0원을 충전할 때 예외가 발생한다.")
    void chargeZeroPoint() {

        //given
        final long userId = 1L;
        final long originalPoint = 1L;
        final long minusChargePoint = 0L;
        final long timeMillis = System.currentTimeMillis();

        UserPoint userPointMock = new UserPoint(userId, originalPoint, timeMillis);

        //when
        when(userPointRepository.findById(userId)).thenReturn(Optional.of(userPointMock));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.chargePointById(userId, minusChargePoint, timeMillis);
        });

        //then
        assertEquals("마이너스 금액 또는 0원을 충전할 수 없습니다.", exception.getMessage());

        // 예외로 인해 히스토리 저장 로직은 호출되지 않아야 한다.
        verify(pointHistoryService, never()).saveHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }
}