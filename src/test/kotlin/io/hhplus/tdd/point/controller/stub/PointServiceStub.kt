package io.hhplus.tdd.point.controller.stub

import io.hhplus.tdd.point.controller.dto.UserPointRequest
import io.hhplus.tdd.point.domain.PointService
import io.hhplus.tdd.point.repository.dto.PointHistory
import io.hhplus.tdd.point.repository.dto.TransactionType
import io.hhplus.tdd.point.repository.dto.UserPoint


/*
* PointService의 동작에 대한 Stub 객체입니다.
* Repository의 Stub과는 다르게, 서비스의 로직을 모르기 때문에 직접 데이터를 수정해야 합니다.
* */
class PointServiceStub: PointService {
    private val table = HashMap<Long, UserPoint>()
    private val histories = mutableListOf<PointHistory>()
    private var cursor: Long = 1L
    
    override fun getUserPoint(userId: Long): UserPoint {
        return table[userId] ?: UserPoint(id = userId, point = 0, updateMillis = System.currentTimeMillis())
    }

    override fun charge(userPointRequest: UserPointRequest): UserPoint {
        val newPoint = table[userPointRequest.userId]?: UserPoint(id = userPointRequest.userId, point = 0, updateMillis = System.currentTimeMillis())
        newPoint.charge(userPointRequest.amount)
        table[userPointRequest.userId] = newPoint

        val history = PointHistory(
                id = cursor++,
                userId = userPointRequest.userId,
                amount = userPointRequest.amount,
                type = TransactionType.CHARGE,
                timeMillis = System.currentTimeMillis(),
        )
        histories.add(history)
        return newPoint
    }

    override fun use(userPointRequest: UserPointRequest): UserPoint {
        val newPoint = table[userPointRequest.userId]?: UserPoint(id = userPointRequest.userId, point = 0, updateMillis = System.currentTimeMillis())
        newPoint.use(userPointRequest.amount)
        table[userPointRequest.userId] = newPoint

        val history = PointHistory(
            id = cursor++,
            userId = userPointRequest.userId,
            amount = userPointRequest.amount,
            type = TransactionType.USE,
            timeMillis = System.currentTimeMillis(),
        )
        histories.add(history)
        return newPoint
    }

    override fun getPointHistories(userId: Long): List<PointHistory> {
        return histories.filter { it.userId == userId }
    }
}