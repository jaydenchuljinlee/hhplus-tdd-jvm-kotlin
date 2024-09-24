package io.hhplus.tdd.point.service

import io.hhplus.tdd.comon.SynchronizedContainer
import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.domain.PointHistory
import io.hhplus.tdd.point.domain.TransactionType
import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.domain.req.UserPointRequest
import io.hhplus.tdd.point.exception.NotEnoughPointsException
import io.hhplus.tdd.point.exception.PointOverflowException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class PointServiceImpl(
    private val userPointTable: UserPointTable,
    private val userPointHistoryTable: PointHistoryTable,
    private val pointLockContainer: PointLockContainer
): PointService {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun getUserPoint(userId: Long): UserPoint {
        return userPointTable.selectById(userId)
    }

    override fun charge(userPointRequest: UserPointRequest): UserPoint {
        userPointRequest.validate()

        val userPoint = userPointTable.selectById(userPointRequest.userId)

        // Long 타입의 범위를 넘는지 체크
        val newPoint = try {
            Math.addExact(userPoint.point, userPointRequest.amount)
        } catch (e: ArithmeticException) {
            logger.error("original: ${userPoint.point} + new: ${userPointRequest.amount} = ${userPoint.point + userPointRequest.amount}")
            throw PointOverflowException()
        }

        return pointLockContainer.withLock(userPoint.id) {
            val result = userPointTable.insertOrUpdate(userPointRequest.userId, newPoint)
            userPointHistoryTable.insert(userPointRequest.userId, userPointRequest.amount, TransactionType.CHARGE, System.currentTimeMillis())
            logger.info("포인트 충전 완료: userId: ${userPointRequest.userId}, amount: ${userPointRequest.amount}, 총 포인트: ${result.point}")
            result
        }
    }

    override fun use(userPointRequest: UserPointRequest): UserPoint {
        userPointRequest.validate()

        val userPoint = userPointTable.selectById(userPointRequest.userId)

        val newPoint = userPoint.point - userPointRequest.amount

        if (newPoint < 0) {
            logger.warn("현재 포인트: ${userPoint.point}, 사용하려는 포인트: ${userPointRequest.amount} = ${userPoint.point - userPointRequest.amount} <")
            throw NotEnoughPointsException()
        }

        return pointLockContainer.withLock(userPoint.id) {
            val result = userPointTable.insertOrUpdate(userPointRequest.userId, newPoint)
            userPointHistoryTable.insert(userPointRequest.userId, userPointRequest.amount, TransactionType.USE, System.currentTimeMillis())
            logger.info("포인트 사용 완료: userId: ${userPointRequest.userId}, amount: ${userPointRequest.amount}, 총 포인트: ${result.point}")

            result
        }
    }

    override fun getPointHistories(userId: Long): List<PointHistory> {
        return userPointHistoryTable.selectAllByUserId(userId)
    }
}