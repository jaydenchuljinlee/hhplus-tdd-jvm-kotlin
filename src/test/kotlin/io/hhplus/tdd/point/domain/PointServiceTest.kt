package io.hhplus.tdd.point.domain

import io.hhplus.tdd.point.controller.dto.UserPointRequest
import io.hhplus.tdd.point.domain.stub.PointHistoryRepositoryStub
import io.hhplus.tdd.point.domain.stub.UserPointRepositoryStub
import io.hhplus.tdd.point.exception.NotEnoughPointsException
import io.hhplus.tdd.point.exception.PointOverflowException
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import io.hhplus.tdd.point.repository.dto.UserPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture

class PointServiceTest {
    private lateinit var userPointLockContainer: PointLockContainer
    private lateinit var userPointRepository: UserPointRepository
    private lateinit var pointHistoryRepository: PointHistoryRepository

    private lateinit var pointService: PointService

    @BeforeEach
    fun before() {
        userPointLockContainer = PointLockContainer()
        userPointRepository = UserPointRepositoryStub()
        pointHistoryRepository = PointHistoryRepositoryStub()

        // 초기 데이터 생성
        val userPoint = userPointRepository.charge(UserPoint(0, 1, System.currentTimeMillis()))

        pointHistoryRepository.charge(userPoint)

        pointService = PointServiceImpl(userPointLockContainer, userPointRepository, pointHistoryRepository)
    }

    @DisplayName("UserPoint가 등록되지 않은 사용자의 포인트는 0이다")
    @Test
    fun shouldReturnZeroForUnregisteredUserPoint() {
        val notExistUserPoint = pointService.getUserPoint(999)

        assertEquals(notExistUserPoint.point, 0)
    }

    @DisplayName("UserPoint가 등록되지 않은 사용자의 히스토리는 존재하지 않는다")
    @Test
    fun shouldReturnNoHistoryForUnregisteredUserPoint() {
        val notExistUserPointHistories = pointService.getPointHistories(999)

        assertEquals(notExistUserPointHistories.size, 0)
    }

    @DisplayName("현재 등록된 User의 UserPoint는 1이다")
    @Test
    fun shouldReturnUserPointAsOneForRegisteredUser() {
        val notExistUserPoint = pointService.getUserPoint(0)

        assertEquals(notExistUserPoint.point, 1)
    }

    @DisplayName("UserPoint가 등록된 사용자의 히스토리는 존재한다")
    @Test
    fun `UserPoint가 등록된 사용자의 히스토리는 존재한다`() {
        val notExistUserPointHistories = pointService.getPointHistories(0)

        assertEquals(notExistUserPointHistories.isNotEmpty(), true)
    }

    @DisplayName("포인트 타입 범위 이상의 충전 금액 요청 실패")
    @Test
    fun failsWhenChargeExceedsPointLimit() {
        // given
        val userPoint = userPointRepository.getUserPoint(0)
        // val userPoint = userPointTable.selectById(0)

        // when
        val exception = assertThrows(PointOverflowException::class.java) {
            pointService.charge(UserPointRequest.of(userPoint.id, Long.MAX_VALUE))
        }

        // then
        assertEquals("포인트의 범위를 넘어서는 연산입니다.", exception.message)
    }

    @DisplayName("보유한 포인트 초과 사용으로 인한 포인트 사용 실패")
    @Test
    fun shouldFailWhenUsingMoreThanAvailablePoints() {
        // given
        val userPoint = userPointRepository.getUserPoint(0)
        // val userPoint = userPointTable.selectById(0)

        // when
        val exception = assertThrows(NotEnoughPointsException::class.java) {
            pointService.use(UserPointRequest.of(userPoint.id, 2))
        }

        // then
        assertEquals("포인트가 부족합니다.", exception.message)
    }

    @DisplayName("같은 사용자에 대한 동시 충전 및 사용 결과의 데이터 정합성이 보장된다.")
    @Test
    fun concurrentChargeAndUseEnsuresDataIntegrity() {
        // given
        val firstPoint = pointService.getUserPoint(0)
        // val firstPoint = userPointTable.selectById(0)
        pointService.charge(UserPointRequest.of(firstPoint.id, 4)) // 추가로 4를 더해줌

        val secondPoint = pointService.charge(UserPointRequest.of(1, 4)) // 새로운 사용자에게 4포인트를 추가
        val thirdPoint = pointService.charge(UserPointRequest.of(2, 4)) // 3번째 사용자에게 4포인트를 추가

        // when
        val futures = listOf(
            // 사용자 0번에 대한 동시 요청 수행
            CompletableFuture.runAsync {
                Thread.sleep(10)
                pointService.charge(UserPointRequest.of(firstPoint.id, 1))
            },
            CompletableFuture.runAsync {
                Thread.sleep(15)
                pointService.use(UserPointRequest.of(firstPoint.id, 3))
            },
            CompletableFuture.runAsync {
                Thread.sleep(20)
                pointService.charge(UserPointRequest.of(firstPoint.id, 1))
            },
            CompletableFuture.runAsync {
                Thread.sleep(23)
                pointService.use(UserPointRequest.of(firstPoint.id, 2))
            },

            // 사용자 1번에 대한 동시 요청 수행. 두 요청은 사용자 0번의 두 번째 요청보다 먼저 시작된다.
            CompletableFuture.runAsync {
                Thread.sleep(13)
                pointService.use(UserPointRequest.of(secondPoint.id, 2))
            },
            CompletableFuture.runAsync {
                Thread.sleep(14)
                pointService.charge(UserPointRequest.of(secondPoint.id, 2))
            },

            // 사용자 2번에 대한 동시 요청 수행. 두 요청은 사용자 0번의 두 번째 요청보다 먼저 시작된다.
            CompletableFuture.runAsync {
                Thread.sleep(13)
                pointService.use(UserPointRequest.of(thirdPoint.id, 1))
            },
            CompletableFuture.runAsync {
                Thread.sleep(14)
                pointService.charge(UserPointRequest.of(thirdPoint.id, 2))
            },
        )

        CompletableFuture.allOf(*futures.toTypedArray()).join()

        val newFirstPoint = pointService.getUserPoint(firstPoint.id)
        val newSecondPoint = pointService.getUserPoint(secondPoint.id)
        val newThirdPoint = pointService.getUserPoint(thirdPoint.id)

        // then
        assertEquals(newFirstPoint.point, 2)
        assertEquals(newSecondPoint.point, 4)
        assertEquals(newThirdPoint.point, 5)
    }

}
