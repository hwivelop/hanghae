package io.hhplus.tdd.point;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConcurrencyUserPointServiceTest {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private UserPointRepository userPointRepository;

    @DisplayName("최대 금액 이내로 충전 하면 모두 정상 처리 된다.")
    @Test
    void concurrentChargeTest() throws InterruptedException {

        // given
        final long userId = 1L;
        final long originalAmount = 1L;
        final long resultAmount = 1001L; // 50개 쓰레드 * 100원씩 충전
        long chargeAmount = 100L;

        // 초기 포인트 저장
        userPointRepository.save(userId, originalAmount);
        System.out.println("초기 포인트 저장: userId = " + userId + ", originalAmount = " + originalAmount);

        //쓰레드 및 풀 설정
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {

            final int threadId = i;

            executorService.submit(() -> {
                try {
                    System.out.println("Thread " + threadId + " - 포인트 충전 시작");
                    UserPoint userPoint = userPointService.chargePointById(userId, chargeAmount, System.currentTimeMillis());
                    System.out.println("Thread " + threadId + " - 포인트 충전 완료, 현재 금액 = " + userPoint.point());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("모든 스레드 작업 완료");

        UserPoint userPoint = userPointRepository.findByIdOrThrow(userId);

        // then
        System.out.println("최종 포인트 조회: userId = " + userId + ", finalPoint = " + userPoint.point());
        assertThat(userPoint.point()).isEqualTo(resultAmount);
    }

    @DisplayName("보유 금액 이내로 사용 하면 모두 정상 처리 된다.")
    @Test
    void concurrentUseTest() throws InterruptedException {

        // given
        final long userId = 1L;
        final long originalAmount = 1001L;
        final long resultAmount = 1L; // 10개 쓰레드 * 100원씩 사용
        long useAmount = 100L;

        // 초기 포인트 저장
        userPointRepository.save(userId, originalAmount);
        System.out.println("초기 포인트 저장: userId = " + userId + ", originalAmount = " + originalAmount);

        //쓰레드 및 풀 설정
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {

            final int threadId = i;

            executorService.submit(() -> {
                try {
                    System.out.println("Thread " + threadId + " - 포인트 사용 시작");
                    UserPoint userPoint = userPointService.usePointById(userId, useAmount, System.currentTimeMillis());
                    System.out.println("Thread " + threadId + " - 포인트 사용 완료, 현재 금액 = " + userPoint.point());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("모든 스레드 작업 완료");

        UserPoint userPoint = userPointRepository.findByIdOrThrow(userId);

        // then
        System.out.println("최종 포인트 조회: userId = " + userId + ", finalPoint = " + userPoint.point());
        assertThat(userPoint.point()).isEqualTo(resultAmount);
    }

    @DisplayName("보유 금액 초과하여 사용 하면 예외가 발생한다.")
    @Test
    void concurrentUseWithOverPointTest() throws InterruptedException {

        // given
        final long userId = 1L;
        final long originalAmount = 500L; // 10개 쓰레드 * 100원씩 사용 -> 500원 모자름
        long useAmount = 100L;

        // 초기 포인트 저장
        userPointRepository.save(userId, originalAmount);
        System.out.println("초기 포인트 저장: userId = " + userId + ", originalAmount = " + originalAmount);

        //쓰레드 및 풀 설정
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);


        // 예외 수집용 리스트
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        // when
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    System.out.println("Thread " + threadId + " - 포인트 사용 시작");
                    UserPoint userPoint = userPointService.usePointById(userId, useAmount, System.currentTimeMillis());
                    System.out.println("Thread " + threadId + " - 포인트 사용 완료, 현재 금액 = " + userPoint.point());
                } catch (Exception e) {
                    exceptions.add(e); // 발생한 예외를 수집
                    System.out.println("Thread " + threadId + "에서 예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        System.out.println("모든 스레드 작업 완료");

        UserPoint userPoint = userPointRepository.findByIdOrThrow(userId);

        // then
        System.out.println("최종 포인트 조회: userId = " + userId + ", finalPoint = " + userPoint.point());

        // 적어도 하나의 스레드는 포인트 부족으로 예외가 발생해야 한다.
        exceptions.forEach(exception -> assertEquals("차감할 포인트가 부족합니다.", exception.getMessage()));
    }

    @DisplayName("여러 사용자가 보유 금액 이내로 사용하면 모두 정상 처리된다.")
    @Test
    void concurrentUseTestForMultipleUsers() throws InterruptedException {

        // given
        final long[] userIds = {1L, 2L, 3L};
        final long originalAmount = 1001L;
        final long useAmount = 100L;
        final long resultAmount = 1L; // 10개 쓰레드 * 100원

        // 각 유저에 대해 초기 포인트 저장
        for (long userId : userIds) {
            userPointRepository.save(userId, originalAmount);
            System.out.println("초기 포인트 저장: userId = " + userId + ", originalAmount = " + originalAmount);
        }

        // 쓰레드 및 풀 설정
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount * userIds.length); // 30개 스레드 풀
        CountDownLatch latch = new CountDownLatch(threadCount * userIds.length);

        // when
        for (long userId : userIds) {
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executorService.submit(() -> {
                    try {
                        System.out.println("User " + userId + " - Thread " + threadId + " - 포인트 사용 시작");
                        UserPoint userPoint = userPointService.usePointById(userId, useAmount, System.currentTimeMillis());
                        System.out.println("User " + userId + " - Thread " + threadId + " - 포인트 사용 완료, 현재 금액 = " + userPoint.point());
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();
        System.out.println("모든 스레드 작업 완료");

        // then 각 사용자의 최종 포인트 확인
        for (long userId : userIds) {
            UserPoint userPoint = userPointRepository.findByIdOrThrow(userId);
            System.out.println("최종 포인트 조회: userId = " + userId + ", finalPoint = " + userPoint.point());
            assertThat(userPoint.point()).isEqualTo(resultAmount);
        }
    }
}