package io.hhplus.tdd.point.controller.dto

import io.hhplus.tdd.point.repository.dto.UserPoint

/*
* 도메인 계층에서 뷰 계층으로 넘겨주기 위해 Response 형식의 DTO를 정의했습니다.
* */
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