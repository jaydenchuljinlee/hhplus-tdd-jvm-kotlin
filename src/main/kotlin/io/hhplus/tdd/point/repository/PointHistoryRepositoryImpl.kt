package io.hhplus.tdd.point.repository

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.repository.dto.PointHistory
import io.hhplus.tdd.point.repository.dto.TransactionType
import io.hhplus.tdd.point.repository.dto.UserPoint
import org.springframework.stereotype.Repository

@Repository
class PointHistoryRepositoryImpl(
    private val pointHistoryTable: PointHistoryTable
): PointHistoryRepository {
    override fun getPointHistories(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }

    override fun use(userPoint: UserPoint): PointHistory {
        return pointHistoryTable.insert(userPoint.id, userPoint.point, TransactionType.USE, System.currentTimeMillis())
    }

    override fun charge(userPoint: UserPoint): PointHistory {
        return pointHistoryTable.insert(userPoint.id, userPoint.point, TransactionType.CHARGE, System.currentTimeMillis())
    }
}