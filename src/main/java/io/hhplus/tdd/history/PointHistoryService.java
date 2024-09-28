package io.hhplus.tdd.history;

import io.hhplus.tdd.was.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회하는 기능
     */
    public List<PointHistory> getHistoriesById(long id) {

        return pointHistoryRepository.findAllById(id);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역을 저장하는 기능
     */
    public PointHistory saveHistory(
            long userId,
            long chargePoint,
            TransactionType transactionType,
            long timeMillis
    ) {

        return pointHistoryRepository.save(userId, chargePoint, transactionType, timeMillis);
    }
}