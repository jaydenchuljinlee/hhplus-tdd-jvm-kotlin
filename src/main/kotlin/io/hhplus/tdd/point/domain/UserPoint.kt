package io.hhplus.tdd.point.domain

import io.hhplus.tdd.point.exception.NotEnoughPointsException
import io.hhplus.tdd.point.exception.PointOverflowException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun use(amount: Long): Long {
        val newPoint = this.point - amount

        if (newPoint < 0) {
            logger.warn("현재 포인트: ${this.point}, 사용하려는 포인트: $amount = ${this.point - amount} <")
            throw NotEnoughPointsException()
        }

        return newPoint
    }

    fun charge(amount: Long): Long {
        // Long 타입의 범위를 넘는지 체크
        val newPoint = try {
            Math.addExact(this.point, amount)
        } catch (e: ArithmeticException) {
            logger.error("original: ${this.point} + new: $amount = ${this.point + amount}")
            throw PointOverflowException()
        }

        return newPoint
    }
}
