package io.hhplus.tdd.point.domain

import io.hhplus.tdd.point.exception.PointOverflowException

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    fun charge(amount: Long): Long {
        // Long 타입의 범위를 넘는지 체크
        val newPoint = try {
            Math.addExact(this.point, amount)
        } catch (e: ArithmeticException) {
            throw PointOverflowException()
        }

        return newPoint
    }
}
