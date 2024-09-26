package io.hhplus.tdd.history;

import io.hhplus.tdd.was.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavePointHistoryServiceTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointHistoryService pointHistoryService;

    @Test
    @DisplayName("특정 유저의 포인트 충전/이용 내역을 저장한다.")
    void saveHistory() {

        //given
        final long userId = 1L;

        long timeMillis = System.currentTimeMillis();
        long amount = 1000L;
        TransactionType type = TransactionType.CHARGE;
        PointHistory pointHistoryMock= new PointHistory(1L, userId, amount, type, timeMillis);

        when(pointHistoryRepository.save(userId, amount, type, timeMillis)).thenReturn(pointHistoryMock);

        //when
        PointHistory pointHistory = pointHistoryService.saveHistory(userId, amount, type, timeMillis);

        //then
        assertThat(pointHistory.type()).isEqualTo(type);
        assertThat(pointHistory.amount()).isEqualTo(amount);
    }
}