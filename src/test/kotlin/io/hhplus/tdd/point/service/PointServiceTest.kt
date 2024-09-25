package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class PointServiceTest {
    private lateinit var pointService: PointServiceImpl

    @DisplayName("UserPoint가 등록되지 않은 사용자의 포인트는 0이다")
    @Test
    fun `UserPoint가 등록되지 않은 사용자의 포인트는 0이다`() {
        val notExistUserPoint = pointService.getUserPoint(999)

        assertEquals(notExistUserPoint.point, 0)
    }

}