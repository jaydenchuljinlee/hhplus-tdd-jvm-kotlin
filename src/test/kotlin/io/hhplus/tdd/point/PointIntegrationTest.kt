package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.controller.PointController
import io.hhplus.tdd.point.domain.PointLockContainer
import io.hhplus.tdd.point.domain.PointService
import io.hhplus.tdd.point.domain.PointServiceImpl
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.PointHistoryRepositoryImpl
import io.hhplus.tdd.point.repository.UserPointRepository
import io.hhplus.tdd.point.repository.UserPointRepositoryImpl
import io.hhplus.tdd.point.repository.dto.UserPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

/*
* 실제 사용하는 객체 기반으로 서로 의존성을 가지는 통합 테스트
* 목적: 동시성 테스트 간에 실제 객체들이 잘 동작하는지 확인하는 테스트입니다.
* */
class PointIntegrationTest {
    private lateinit var controller : PointController
    private lateinit var pointService: PointService
    private lateinit var userPointRepository: UserPointRepository
    private lateinit var pointHistoryRepository: PointHistoryRepository

    @BeforeEach
    fun before() {
        userPointRepository = UserPointRepositoryImpl(UserPointTable()) // 실제 객체
        pointHistoryRepository = PointHistoryRepositoryImpl(PointHistoryTable()) // 실제 객체
        pointService = PointServiceImpl(PointLockContainer(), userPointRepository, pointHistoryRepository) // 실제 객체

        controller = PointController(pointService) // 실제 객체

        // 초기 데이터 생성
        val userPoint = userPointRepository.charge(UserPoint(0, 1, System.currentTimeMillis()))
        pointHistoryRepository.charge(userPoint)
    }

    @DisplayName("같은 사용자에 대한 동시 충전 및 사용 결과의 데이터 정합성이 보장된다.")
    @Test
    fun integrationTest() {
        // given
        val firstPoint = controller.point(0)
        controller.charge(firstPoint.id, 4) // 추가로 4를 더해줌

        val secondPoint = controller.charge(1, 4) // 새로운 사용자에게 4포인트를 추가
        val thirdPoint = controller.charge(2, 4) // 3번째 사용자에게 4포인트를 추가

        // when
        val futures = listOf(
            // 사용자 0번에 대한 동시 요청 수행
            CompletableFuture.runAsync {
                Thread.sleep(10)
                controller.charge(firstPoint.id, 1)
            },
            CompletableFuture.runAsync {
                Thread.sleep(15)
                controller.use(firstPoint.id, 3)
            },
            CompletableFuture.runAsync {
                Thread.sleep(20)
                controller.charge(firstPoint.id, 1)
            },
            CompletableFuture.runAsync {
                Thread.sleep(23)
                controller.use(firstPoint.id, 2)
            },

            // 사용자 1번에 대한 동시 요청 수행. 두 요청은 사용자 0번의 두 번째 요청보다 먼저 시작된다.
            CompletableFuture.runAsync {
                Thread.sleep(13)
                controller.use(secondPoint.id, 2)
            },
            CompletableFuture.runAsync {
                Thread.sleep(14)
                controller.charge(secondPoint.id, 2)
            },

            // 사용자 2번에 대한 동시 요청 수행. 두 요청은 사용자 0번의 두 번째 요청보다 먼저 시작된다.
            CompletableFuture.runAsync {
                Thread.sleep(13)
                controller.use(thirdPoint.id, 1)
            },
            CompletableFuture.runAsync {
                Thread.sleep(14)
                controller.charge(thirdPoint.id, 2)
            },
        )

        CompletableFuture.allOf(*futures.toTypedArray()).join()

        val newFirstPoint = controller.point(firstPoint.id)
        val newSecondPoint = controller.point(secondPoint.id)
        val newThirdPoint = controller.point(thirdPoint.id)

        // then
        assertEquals(newFirstPoint.point, 2)
        assertEquals(newSecondPoint.point, 4)
        assertEquals(newThirdPoint.point, 5)
    }
}