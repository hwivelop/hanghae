package io.hhplus.tdd.history;

import io.hhplus.tdd.was.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPointHistoryServiceTest {

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointHistoryService pointHistoryService;


    @Test
    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회한다.")
    void findPointById() {

        //given
        final long userId = 1L;

        PointHistory pointHistory1 = new PointHistory(1L, userId, 1000L, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory pointHistory2 = new PointHistory(1L, userId, 500L, TransactionType.USE, System.currentTimeMillis());

        List<PointHistory> pointHistories = List.of(pointHistory1, pointHistory2);

        when(pointHistoryRepository.findAllById(userId)).thenReturn(pointHistories);

        //when
        List<PointHistory> histories = pointHistoryService.getHistoriesById(userId);

        //then
        assertThat(histories).isNotNull();
        assertThat(histories).hasSize(2)
                .extracting("userId", "amount", "type")
                .containsExactlyInAnyOrder(
                        tuple(userId, 1000L, TransactionType.CHARGE),
                        tuple(userId, 500L, TransactionType.USE)
                );
    }
}