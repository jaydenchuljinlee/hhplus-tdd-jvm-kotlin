package io.hhplus.tdd.point.service

import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.domain.req.UserPointRequest
import org.springframework.stereotype.Service

@Service
class PointServiceImpl(): PointService {
    override fun getUserPoint(userId: Long): UserPoint {
        TODO("Not yet implemented")
    }

    override fun charge(userPointRequest: UserPointRequest): UserPoint {
        TODO("Not yet implemented")
    }

    override fun use(userPointRequest: UserPointRequest): UserPoint {
        TODO("Not yet implemented")
    }

    override fun getPointHistories(userId: Long): List<PointHistory> {
        TODO("Not yet implemented")
    }
}