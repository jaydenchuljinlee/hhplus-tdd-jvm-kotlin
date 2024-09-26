package io.hhplus.tdd.point.repository.dto

import io.hhplus.tdd.point.exception.NotEnoughPointsException
import io.hhplus.tdd.point.exception.PointOverflowException

data class UserPoint(
    val id: Long,
    var point: Long,
    val updateMillis: Long,
) {
    companion object {
        const val MIN = 1_000L
        const val MAX = 1_000_000_000L
    }

    fun use(amount: Long) {
        val newPoint = this.point - amount

        if (newPoint < 0) {
            throw NotEnoughPointsException()
        }

        this.point = newPoint
    }

    fun charge(amount: Long) {
        // Long 타입의 범위를 넘는지 체크
        val newPoint = try {
            Math.addExact(this.point, amount)
        } catch (e: ArithmeticException) {
            throw PointOverflowException("포인트의 범위를 넘어서는 연산입니다.")
        }

        if (newPoint > MAX) {
            throw PointOverflowException("포인트는 최대 1,000,000,000 이하로 보유할 수 있습니다.")
        }

        this.point = newPoint
    }
}
