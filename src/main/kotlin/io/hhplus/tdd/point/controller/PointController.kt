package io.hhplus.tdd.point.controller

import io.hhplus.tdd.point.controller.dto.PointHistoryResponse
import io.hhplus.tdd.point.controller.dto.UserPointRequest
import io.hhplus.tdd.point.controller.dto.UserPointResponse
import io.hhplus.tdd.point.domain.PointService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController(
    private val pointService: PointService,
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("{id}")
    fun point(
        @PathVariable id: Long,
    ): UserPointResponse {
        val result = pointService.getUserPoint(id)
        return UserPointResponse.of(result)
    }

    @GetMapping("{id}/histories")
    fun history(
        @PathVariable id: Long,
    ): List<PointHistoryResponse> {
        val result = pointService.getPointHistories(id)
        return result.map { PointHistoryResponse.of(it) }
    }

    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPointResponse {
        val result = pointService.charge(UserPointRequest.of(id, amount))
        return UserPointResponse.of(result)
    }

    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestBody amount: Long,
    ): UserPointResponse {
        val result = pointService.use(UserPointRequest.of(id, amount))
        return UserPointResponse.of(result)
    }
}