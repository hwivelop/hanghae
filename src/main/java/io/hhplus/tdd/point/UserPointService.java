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
    public UserPoint getPointsByIdOrThrow(long id) {

        return userPointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디가 존재하지 않습니다."));
    }
}
