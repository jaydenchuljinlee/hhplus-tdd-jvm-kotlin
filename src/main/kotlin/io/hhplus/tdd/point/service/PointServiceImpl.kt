package io.hhplus.tdd.point.service

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.domain.req.UserPointRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PointServiceImpl(
    private val userPointTable: UserPointTable,
    private val userPointHistoryTable: PointHistoryTable,
): PointService {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun getUserPoint(userId: Long): UserPoint {
        return userPointTable.selectById(userId)
    }

    override fun charge(userPointRequest: UserPointRequest): UserPoint {
        val userPoint = userPointTable.selectById(userPointRequest.userId)

        logger.warn("포인트 충전 요청: 사용자 ${userPointRequest.userId} 현재 포인트: ${userPoint.point}, 충전하려는 포인트: ${userPointRequest.amount}")

        val newPoint = userPoint.charge(userPointRequest.amount)
        val result = userPointTable.insertOrUpdate(userPointRequest.userId, newPoint)

        userPointHistoryTable.insert(userPointRequest.userId, userPointRequest.amount, TransactionType.CHARGE, System.currentTimeMillis())
        logger.info("포인트 충전 완료: userId: ${userPointRequest.userId}, amount: ${userPointRequest.amount}, 총 포인트: ${result.point}")
        return result
    }

    override fun use(userPointRequest: UserPointRequest): UserPoint {
        val userPoint = userPointTable.selectById(userPointRequest.userId)

        logger.warn("포인트 사용 요청: 사용자 ${userPointRequest.userId} 현재 포인트: ${userPoint.point}, 사용하려는 포인트: ${userPointRequest.amount}")

        val newPoint = userPoint.use(userPointRequest.amount)
        val result = userPointTable.insertOrUpdate(userPointRequest.userId, newPoint)

        userPointHistoryTable.insert(userPointRequest.userId, userPointRequest.amount, TransactionType.USE, System.currentTimeMillis())
        logger.info("포인트 사용 완료: userId: ${userPointRequest.userId}, amount: ${userPointRequest.amount}, 총 포인트: ${result.point}")

        return result
    }

    override fun getPointHistories(userId: Long): List<PointHistory> {
        return userPointHistoryTable.selectAllByUserId(userId)
    }
}