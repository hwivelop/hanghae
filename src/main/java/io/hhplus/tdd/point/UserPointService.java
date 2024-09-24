package io.hhplus.tdd.point;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPointService {

    private final UserPointRepository userPointRepository;

    /**
     * 특정 유저의 포인트를 조회하는 기능
     */
    public UserPoint getPointsById(long id) {

        return userPointRepository.findById(id);
    }
}
