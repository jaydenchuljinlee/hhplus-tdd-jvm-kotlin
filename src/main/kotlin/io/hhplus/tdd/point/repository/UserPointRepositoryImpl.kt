package io.hhplus.tdd.point.repository

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.repository.dto.UserPoint
import org.springframework.stereotype.Repository

@Repository
class UserPointRepositoryImpl(
    private val userPointTable: UserPointTable
): UserPointRepository {
    override fun getUserPoint(userId: Long): UserPoint {
        return userPointTable.selectById(userId)
    }

    override fun charge(userPoint: UserPoint): UserPoint {
        return userPointTable.insertOrUpdate(userPoint.id, userPoint.point)
    }

    override fun use(userPoint: UserPoint): UserPoint {
        return userPointTable.insertOrUpdate(userPoint.id, userPoint.point)
    }
}