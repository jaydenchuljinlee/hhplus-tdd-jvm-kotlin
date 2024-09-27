package io.hhplus.tdd.point.controller.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/*
* 충전, 사용 API 요청에 대한 파라미터 검증 테스트입니다.
* */
class UserPointRequestTest {

    @DisplayName("userId가 0보다 작을 경우 IllegalArgumentException")
    @Test
    fun throwsIllegalArgumentExceptionIfUserIdIsBelowZero() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserPointRequest.of(-1, 100)
        }

        assertEquals("userId는 0 이상이어야 합니다.", exception.message)
    }

    @DisplayName("amount가 0보다 작거나 같을 경우 IllegalArgumentException")
    @Test
    fun throwsIllegalArgumentExceptionIfAmountIsBelowZero() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            UserPointRequest.of(1, -100)
        }

        assertEquals("amount는 0 이상이어야 합니다.", exception.message)
    }
}