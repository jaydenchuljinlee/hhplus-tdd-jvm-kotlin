package io.hhplus.tdd.point.domain.stub

import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.dto.PointHistory
import io.hhplus.tdd.point.repository.dto.TransactionType
import io.hhplus.tdd.point.repository.dto.UserPoint

class PointHistoryRepositoryStub: PointHistoryRepository {
    private val table = mutableListOf<PointHistory>()
    private var cursor: Long = 1L

    override fun getPointHistories(userId: Long): List<PointHistory> {
        return table.filter { it.userId == userId }
    }

    override fun use(userPoint: UserPoint): PointHistory {
        val history = PointHistory(
            id = cursor++,
            userId = userPoint.id,
            amount = userPoint.point,
            type = TransactionType.USE,
            timeMillis = System.currentTimeMillis(),
        )
        table.add(history)
        return history
    }

    override fun charge(userPoint: UserPoint): PointHistory {
        val history = PointHistory(
            id = cursor++,
            userId = userPoint.id,
            amount = userPoint.point,
            type = TransactionType.CHARGE,
            timeMillis = System.currentTimeMillis(),
        )
        table.add(history)
        return history
    }
}