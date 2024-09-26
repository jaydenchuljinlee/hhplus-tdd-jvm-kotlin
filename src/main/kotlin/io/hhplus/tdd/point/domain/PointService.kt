package io.hhplus.tdd.point.domain

import io.hhplus.tdd.point.controller.dto.UserPointRequest
import io.hhplus.tdd.point.repository.dto.PointHistory
import io.hhplus.tdd.point.repository.dto.UserPoint

interface PointService {
    fun getUserPoint(userId: Long): UserPoint
    fun charge(userPointRequest: UserPointRequest): UserPoint
    fun use(userPointRequest: UserPointRequest): UserPoint

    fun getPointHistories(userId: Long): List<PointHistory>
}