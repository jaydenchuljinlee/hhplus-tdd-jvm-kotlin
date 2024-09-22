package io.hhplus.tdd.point.domain.req

import kotlin.jvm.Throws

data class UserPointRequest(
    val userId: Long,
    val amount: Long
) {
    companion object {
        fun of(userId: Long, amount: Long) = UserPointRequest(userId, amount)
    }

    @Throws(IllegalArgumentException::class)
    fun validate() {
        // user 정보 체크
        // 포인트 정보 체크
        if (userId < 0) throw IllegalArgumentException("userId는 0 이상이어야 합니다.")
        if (amount < 0) throw IllegalArgumentException("amount는 0 이상이어야 합니다.")
    }
}