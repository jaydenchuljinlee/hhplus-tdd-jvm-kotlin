package io.hhplus.tdd.point.domain.stub

import io.hhplus.tdd.point.repository.UserPointRepository
import io.hhplus.tdd.point.repository.dto.UserPoint

class UserPointRepositoryStub : UserPointRepository {
    private val table = HashMap<Long, UserPoint>()

    override fun getUserPoint(userId: Long): UserPoint {
        return table[userId] ?: UserPoint(id = userId, point = 0, updateMillis = System.currentTimeMillis())
    }

    override fun charge(userPoint: UserPoint): UserPoint {
        val newPoint = UserPoint(id = userPoint.id, point = userPoint.point, updateMillis = System.currentTimeMillis())
        table[userPoint.id] = newPoint
        return newPoint
    }

    override fun use(userPoint: UserPoint): UserPoint {
        val newPoint = UserPoint(id = userPoint.id, point = userPoint.point, updateMillis = System.currentTimeMillis())
        table[userPoint.id] = newPoint
        return newPoint
    }
}