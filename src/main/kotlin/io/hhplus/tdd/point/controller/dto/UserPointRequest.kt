package io.hhplus.tdd.point.controller.dto


/*
* 충전, 사용 API 요청에 대한 Request DTO입니다.
* 내부에서 유효성 검사를 하도록 하여, Controller 단에서 파라미터 검증을 수행할 수 있습니다.
* */
data class UserPointRequest(
    val userId: Long,
    val amount: Long
) {
    init {
        // user 정보 체크
        if (userId < 0) throw IllegalArgumentException("userId는 0 이상이어야 합니다.")
        // 포인트 정보 체크
        if (amount < 0) throw IllegalArgumentException("amount는 0 이상이어야 합니다.")
    }

    companion object {
        fun of(userId: Long, amount: Long) = UserPointRequest(userId, amount)
    }
}