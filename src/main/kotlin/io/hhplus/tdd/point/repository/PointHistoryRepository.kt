package io.hhplus.tdd.point.repository

import io.hhplus.tdd.point.repository.dto.PointHistory
import io.hhplus.tdd.point.repository.dto.UserPoint

interface PointHistoryRepository {
    fun getPointHistories(userId: Long): List<PointHistory>
    fun use(userPoint: UserPoint): PointHistory
    fun charge(userPoint: UserPoint): PointHistory
}