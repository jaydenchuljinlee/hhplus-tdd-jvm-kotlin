package io.hhplus.tdd.point.controller

import io.hhplus.tdd.point.controller.dto.PointHistoryResponse
import io.hhplus.tdd.point.controller.dto.UserPointResponse
import io.hhplus.tdd.point.controller.stub.PointServiceStub
import io.hhplus.tdd.point.repository.dto.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/*
* 컨틀롤러 테스트는 API 스펙을 위해 만들었습니다.
* */
class UserPointControllerTest {

    private lateinit var controller: PointController

    @BeforeEach
    fun before() {
        controller = PointController(PointServiceStub())

        controller.charge(0, 100)
    }

    @DisplayName("현재 등록 된 사용자에 대한 포인트 스펙 조회")
    @Test
    fun getPoint() {
        val result = controller.point(0)

        val userResponse = UserPointResponse(0, 100, result.updateMillis)

        assertEquals(result.id, userResponse.id)
        assertEquals(result.point, userResponse.point)
    }

    @DisplayName("현재 등록 된 사용자의 포인트 이력 스펙 조회")
    @Test
    fun getHistories() {
        val result = controller.history(0)

        val historyResponse = listOf(PointHistoryResponse(1, 0, TransactionType.CHARGE,  100, result[0].timeMillis))

        assertEquals(result.size, 1)
        assertEquals(result[0].id, historyResponse[0].id)
        assertEquals(result[0].userId, historyResponse[0].userId)
        assertEquals(result[0].amount, historyResponse[0].amount)
        assertEquals(result[0].type, historyResponse[0].type)
    }

    @DisplayName("포인트 충전 API에 대한 스펙")
    @Test
    fun charge() {
        val result = controller.charge(0, 30)

        val userResponse = UserPointResponse(0, 130, result.updateMillis)

        val histories = controller.history(0)

        assertEquals(result.id, userResponse.id)
        assertEquals(result.point, userResponse.point)
        assertEquals(histories.size, 2)
    }

    @DisplayName("포인트 사용 API에 대한 스펙")
    @Test
    fun use() {
        val result = controller.use(0, 30)

        val userResponse = UserPointResponse(0, 70, result.updateMillis)

        val histories = controller.history(0)

        assertEquals(result.id, userResponse.id)
        assertEquals(result.point, userResponse.point)
        assertEquals(histories.size, 2)
    }
}