package io.hhplus.tdd.point.domain

import io.hhplus.tdd.point.exception.NotEnoughPointsException
import io.hhplus.tdd.point.exception.PointOverflowException

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    fun use(amount: Long): Long {
        val newPoint = this.point - amount

        if (newPoint < 0) {
            throw NotEnoughPointsException()
        }

        return newPoint
    }

    fun charge(amount: Long): Long {
        // Long 타입의 범위를 넘는지 체크
        val newPoint = try {
            Math.addExact(this.point, amount)
        } catch (e: ArithmeticException) {
            throw PointOverflowException("포인트의 범위를 넘어서는 연산입니다.")
        }

        if (newPoint > PointRange.MAX) {
            throw PointOverflowException("포인트는 최대 1,000,000,000 이하로 보유할 수 있습니다.")
        }

        return newPoint
    }
}
