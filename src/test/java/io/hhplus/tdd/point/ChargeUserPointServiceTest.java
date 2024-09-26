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
        when(userPointRepository.findById(userId)).thenReturn(userPointMock);
        when(pointHistoryService.getHistoriesById(userId)).thenReturn(List.of(pointHistoryMock));

        /**
         * todo whee : userPoint 서비스 안의 history 서비스의 로직이 잘 저장됐는지 이렇게 확인하는게 맞나..?
         */
        userPointService.chargePointById(userId, chargePoint, timeMillis);
        List<PointHistory> history1 = pointHistoryService.getHistoriesById(userId);

        UserPoint userPoint2 = userPointService.chargePointById(userId, chargePoint, timeMillis);
        List<PointHistory> history2 = pointHistoryService.getHistoriesById(userId);

        List<PointHistory> histories = List.of(history1.get(0), history2.get(0));


        //then
        // 충전된 포인트는 토탈 포인트와 같다.
        assertThat(userPoint2.point()).isEqualTo(totalPoint);
        // 충전된 히스토리를 조회할 수 있다.
        assertThat(histories).hasSize(2)
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(userId, chargePoint, TransactionType.CHARGE),
                        tuple(userId, chargePoint, TransactionType.CHARGE)
                );
    }

    @Test
    @DisplayName("포인트 충전 시 존재하지 않는 유저의 포인트를 충전하면 예외가 발생한다.")
    void chargeUnknownId() {

        //given
        final long userId = 1L;
        final long chargePoint = 10000L;
        final long timeMillis = System.currentTimeMillis();

        when(userPointRepository.findById(userId)).thenThrow(new IllegalArgumentException("유저 아이디가 존재하지 않습니다."));

        //when
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

        when(userPointRepository.findById(userId)).thenReturn(userPointMock);

        //when
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

        when(userPointRepository.findById(userId)).thenReturn(userPointMock);

        //when
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
        final long zeroChargePoint = 0L;
        final long timeMillis = System.currentTimeMillis();

        UserPoint userPointMock = new UserPoint(userId, originalPoint, timeMillis);

        when(userPointRepository.findById(userId)).thenReturn(userPointMock);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.chargePointById(userId, zeroChargePoint, timeMillis);
        });

        //then
        assertEquals("마이너스 금액 또는 0원을 충전할 수 없습니다.", exception.getMessage());

        // 예외로 인해 히스토리 저장 로직은 호출되지 않아야 한다.
        verify(pointHistoryService, never()).saveHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }
}