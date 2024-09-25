package io.hhplus.tdd.point.service

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.domain.req.UserPointRequest

interface PointService {
    fun getUserPoint(userId: Long): UserPoint
    fun charge(userPointRequest: UserPointRequest): UserPoint
    fun use(userPointRequest: UserPointRequest): UserPoint

    fun getPointHistories(userId: Long): List<PointHistory>
}