package io.hhplus.tdd.point;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserPointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @InjectMocks
    private UserPointService userPointService;


    @Test
    @DisplayName("유저의 포인트를 조회한다.")
    void findPointById() {

        //given
        final long userId = 1L;
        final long point = 1000L;
        UserPoint userPointMock = new UserPoint(userId, point, System.currentTimeMillis());

        when(userPointRepository.findByIdOrThrow(userId)).thenReturn(userPointMock);

        //when
        UserPoint userpoint = userPointService.getPointsById(userId);

        //then
        assertThat(userpoint).isNotNull();
        // 저장된 포인트와 조건의 포인트는 같다.
        assertThat(userpoint.point()).isEqualTo(point);
    }


    @Test
    @DisplayName("존재하지 않는 유저의 포인트를 조회하면 예외가 발생한다.")
    void findPointByUnknownId() {

        //given
        final long userId = 1L;

        // 임의로 유저 포인트가 없다는 상황으로 예외 발생
        when(userPointRepository.findByIdOrThrow(userId)).thenThrow(new IllegalArgumentException("유저 아이디가 존재하지 않습니다."));

        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userPointService.getPointsById(userId);
        });

        //then
        assertEquals("유저 아이디가 존재하지 않습니다.", exception.getMessage());
    }
}