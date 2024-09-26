package io.hhplus.tdd.point;

import io.hhplus.tdd.history.*;
import io.hhplus.tdd.was.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UseUserPointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryService pointHistoryService;

    @InjectMocks
    private UserPointService userPointService;

    @Test
    @DisplayName("특정 유저의 포인트를 사용한다.")
    void usePoint() {

        //given
        final long userId = 1L;
        final long originalPoint = 10000L;
        final long usePoint = 10000L;
        final long totalPoint = 0L;
        final long timeMillis = System.currentTimeMillis();

        UserPoint userPointMock = new UserPoint(userId, originalPoint, timeMillis);
        UserPoint resultUserPointMock = new UserPoint(userId, originalPoint, timeMillis);
        PointHistory pointHistoryMock = new PointHistory(1L, userId, usePoint, TransactionType.USE, timeMillis);

        when(userPointRepository.findByIdOrThrow(userId)).thenReturn(userPointMock);
        when(userPointRepository.save(userId, totalPoint)).thenReturn(resultUserPointMock);
        when(pointHistoryService.saveHistory(userId, usePoint, TransactionType.USE, timeMillis)).thenReturn(pointHistoryMock);

        //when
        /**
         * todo whee : userPoint 서비스 안의 history 서비스의 로직이 잘 저장됐는지 이렇게 확인하는게 맞나..?
         */
        UserPoint userPoint = userPointService.usePointById(userId, usePoint, timeMillis);
        PointHistory pointHistory = pointHistoryService.saveHistory(userId, usePoint, TransactionType.USE, timeMillis);

        //then
        // 사용된 포인트는 토탈 포인트와 같다.
        assertThat(userPoint.point()).isEqualTo(totalPoint);
        // 사용된 히스토리를 조회할 수 있다.
        assertThat(pointHistory)
                .extracting("userId", "amount", "type")
                .containsExactly(userId, usePoint, TransactionType.USE);
    }

    @Test
    @DisplayName("포인트 사용 시 존재하지 않는 유저의 포인트를 사용하면 예외가 발생한다.")
    void useUnknownId() {

        //given
        final long userId = 1L;
        final long usePoint = 10000L;
        final long timeMillis = System.currentTimeMillis();

        when(userPointRepository.findByIdOrThrow(userId)).thenThrow(new IllegalArgumentException("유저 아이디가 존재하지 않습니다."));

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.usePointById(userId, usePoint, timeMillis);
        });

        //then
        assertEquals("유저 아이디가 존재하지 않습니다.", exception.getMessage());

        // 예외로 인해 히스토리 저장 로직은 호출되지 않아야 한다.
        verify(userPointRepository, never()).save(anyLong(), anyLong());
        verify(pointHistoryService, never()).saveHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }

    @Test
    @DisplayName("마이너스 금액을 사용할 때 예외가 발생한다.")
    void useMinusPoint() {

        //given
        final long userId = 1L;
        final long originalPoint = 1L;
        final long usePoint = -1L;
        final long timeMillis = System.currentTimeMillis();

        UserPoint userPointMock = new UserPoint(userId, originalPoint, timeMillis);

        when(userPointRepository.findByIdOrThrow(userId)).thenReturn(userPointMock);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.usePointById(userId, usePoint, timeMillis);
        });

        //then
        assertEquals("차감할 포인트는 0보다 커야 합니다.", exception.getMessage());

        // 예외로 인해 히스토리 저장 로직은 호출되지 않아야 한다.
        verify(userPointRepository, never()).save(anyLong(), anyLong());
        verify(pointHistoryService, never()).saveHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }

    @Test
    @DisplayName("0원을 사용할 때 예외가 발생한다.")
    void useZeroPoint() {

        //given
        final long userId = 1L;
        final long originalPoint = 1L;
        final long usePoint = 0L;
        final long timeMillis = System.currentTimeMillis();

        UserPoint userPointMock = new UserPoint(userId, originalPoint, timeMillis);

        when(userPointRepository.findByIdOrThrow(userId)).thenReturn(userPointMock);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.usePointById(userId, usePoint, timeMillis);
        });

        //then
        assertEquals("차감할 포인트는 0보다 커야 합니다.", exception.getMessage());

        // 예외로 인해 히스토리 저장 로직은 호출되지 않아야 한다.
        verify(userPointRepository, never()).save(anyLong(), anyLong());
        verify(pointHistoryService, never()).saveHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }

    @Test
    @DisplayName("보유 포인트 보다 큰 포인트를 사용할 때 예외가 발생한다.")
    void chargeZeroPoint() {

        //given
        final long userId = 1L;
        final long originalPoint = 1L;
        final long usePoint = 2L;
        final long timeMillis = System.currentTimeMillis();

        UserPoint userPointMock = new UserPoint(userId, originalPoint, timeMillis);

        when(userPointRepository.findByIdOrThrow(userId)).thenReturn(userPointMock);

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.usePointById(userId, usePoint, timeMillis);
        });

        //then
        assertEquals("차감할 포인트가 부족합니다.", exception.getMessage());

        // 예외로 인해 히스토리 저장 로직은 호출되지 않아야 한다.
        verify(userPointRepository, never()).save(anyLong(), anyLong());
        verify(pointHistoryService, never()).saveHistory(anyLong(), anyLong(), any(TransactionType.class), anyLong());
    }
}