package io.hhplus.tdd.was;

import io.hhplus.tdd.history.*;
import io.hhplus.tdd.point.*;
import lombok.*;
import org.slf4j.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointService userPointService;
    private final PointHistoryService pointHistoryService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        log.info("user id = {}", id);

        return userPointService.getPointsById(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        log.info("user id = {}", id);

        return pointHistoryService.getHistoriesById(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        log.info("user id = {}, amount = {}", id, amount);

        return userPointService.chargePointById(id, amount, System.currentTimeMillis());
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        log.info("user id = {}, amount = {}", id, amount);

        return userPointService.usePointById(id, amount, System.currentTimeMillis());
    }
}
