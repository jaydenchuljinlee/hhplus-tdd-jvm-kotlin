package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.TransactionType
import io.hhplus.tdd.point.domain.req.UserPointRequest
import io.hhplus.tdd.point.exception.NotEnoughPointsException
import io.hhplus.tdd.point.exception.PointOverflowException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class PointServiceTest {
    private lateinit var userPointTable: UserPointTable
    private lateinit var userPointHistoryTable: PointHistoryTable
    private lateinit var pointService: PointServiceImpl

    @BeforeEach
    fun before() {
        userPointTable = UserPointTable()
        val userPoint = userPointTable.insertOrUpdate(0, 1)

        val now = System.currentTimeMillis()

        userPointHistoryTable = PointHistoryTable()
        userPointHistoryTable.insert(userPoint.id, userPoint.point, TransactionType.CHARGE, now)

        pointService = PointServiceImpl(userPointTable, userPointHistoryTable)
    }

    @DisplayName("UserPoint가 등록되지 않은 사용자의 포인트는 0이다")
    @Test
    fun `UserPoint가 등록되지 않은 사용자의 포인트는 0이다`() {
        val notExistUserPoint = pointService.getUserPoint(999)

        assertEquals(notExistUserPoint.point, 0)
    }

    @DisplayName("UserPoint가 등록되지 않은 사용자의 히스토리는 존재하지 않는다")
    @Test
    fun `UserPoint가 등록되지 않은 사용자의 히스토리는 존재하지 않는다`() {
        val notExistUserPointHistories = pointService.getPointHistories(999)

        assertEquals(notExistUserPointHistories.size, 0)
    }

    @DisplayName("현재 등록된 User의 UserPoint는 1이다")
    @Test
    fun `현재 등록된 User의 UserPoint는 1이다`() {
        val notExistUserPoint = pointService.getUserPoint(0)

        assertEquals(notExistUserPoint.point, 1)
    }

    @DisplayName("UserPoint가 등록된 사용자의 히스토리는 존재한다")
    @Test
    fun `UserPoint가 등록된 사용자의 히스토리는 존재한다`() {
        val notExistUserPointHistories = pointService.getPointHistories(0)

        assertEquals(notExistUserPointHistories.isNotEmpty(), true)
    }

    @Test
    fun `포인트 타입 범위 이상의 충전 금액 요청 실패`() {
        // given
        val userPoint = userPointTable.selectById(0)

        // when
        val exception = assertThrows(PointOverflowException::class.java) {
            pointService.charge(UserPointRequest.of(userPoint.id, Long.MAX_VALUE))
        }

        // then
        assertEquals("포인트의 범위가 지원하는 범위를 넘어섰습니다.", exception.message)
    }

    @DisplayName("보유한 포인트 초과 사용으로 인한 포인트 사용 실패")
    @Test
    fun `보유한 포인트 초과 사용으로 인한 포인트 사용 실패`() {
        // given
        val userPoint = userPointTable.selectById(0)

        // when
        val exception = assertThrows(NotEnoughPointsException::class.java) {
            pointService.use(UserPointRequest.of(userPoint.id, 2))
        }

        // then
        assertEquals("포인트가 부족합니다.", exception.message)
    }
}