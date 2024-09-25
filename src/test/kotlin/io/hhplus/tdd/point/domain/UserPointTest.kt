package io.hhplus.tdd.point.domain

import io.hhplus.tdd.point.domain.UserPoint
import io.hhplus.tdd.point.exception.NotEnoughPointsException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserPointTest {
    @DisplayName("newPoint가 0보다 작을 때 NotEnoughPointsException을 던진다")
    @Test
    fun use_throwsExceptionIfNewPointIsBelowZero() {
        val myPoint = UserPoint(0, 1, System.currentTimeMillis())

        val exception = assertThrows(NotEnoughPointsException::class.java) {
            myPoint.use(2)
        }

        assertEquals("포인트가 부족합니다.", exception.message)
    }

    @DisplayName("두 수의 합이 Long의 최대값을 넘어서게 되면, ArithmeticException이 발생한다.")
    @Test
    fun charge_throwsExceptionIfNewPointIsOverflow() {
        val amount = Long.MAX_VALUE

        val myPoint = UserPoint(0, 1, System.currentTimeMillis())

        // 두 포인트를 연산했을 때, Long의 범위를 넘어서게 되면 ArithmeticException이 발생한다.
        val exception = assertThrows(ArithmeticException::class.java) {
            Math.addExact(myPoint.point, amount)
        }

        assertEquals("long overflow", exception.message)
    }

    @DisplayName("포인트 충전 시 파라미터로 Long의 최대값이 들어오면, 포인트의 합은 음수가 된다.")
    @Test
    fun pointOverFlowTest() {
        val amount = Long.MAX_VALUE

        val myPoint = 1L

        val newPoint = myPoint + amount

        // 두 포인트의 합을 저장한 newPoint는 자동 형변환되어 Long.MIN_VALUE를 출력한다.
        assertEquals(newPoint, Long.MIN_VALUE)

        // 두 포인트의 값을 더해도 Long 타입으로 형변환되어 Long.MIN_VALUE를 출력한다.
        assertEquals(myPoint + amount, Long.MIN_VALUE)

        // 두 포인트를 연산했을 때, Long의 범위를 넘어서게 되면 ArithmeticException이 발생한다.
        val exception = assertThrows(ArithmeticException::class.java) {
            Math.addExact(myPoint, amount)
        }

        assertEquals("long overflow", exception.message)
    }
}