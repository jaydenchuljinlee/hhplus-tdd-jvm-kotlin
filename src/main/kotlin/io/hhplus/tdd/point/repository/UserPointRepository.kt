package io.hhplus.tdd.point.repository

import io.hhplus.tdd.point.repository.dto.PointHistory
import io.hhplus.tdd.point.repository.dto.UserPoint

interface UserPointRepository {
    fun getUserPoint(userId: Long) : UserPoint
    fun charge(userPoint: UserPoint): UserPoint
    fun use(userPoint: UserPoint): UserPoint
}