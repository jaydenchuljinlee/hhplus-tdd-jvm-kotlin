package io.hhplus.tdd.point.controller.dto

import io.hhplus.tdd.point.repository.dto.UserPoint

data class UserPointResponse(
    val id: Long,
    var point: Long,
    val updateMillis: Long,
) {
    companion object {
        fun of(userPoint: UserPoint) = UserPointResponse(
            userPoint.id,
            userPoint.point,
            userPoint.updateMillis,
        )
    }
}