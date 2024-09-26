package io.hhplus.tdd.point.controller.dto

import io.hhplus.tdd.point.repository.dto.PointHistory
import io.hhplus.tdd.point.repository.dto.TransactionType

data class PointHistoryResponse(
    val id: Long,
    val userId: Long,
    val type: TransactionType,
    val amount: Long,
    val timeMillis: Long,
) {
    companion object {
        fun of(pointHistory: PointHistory): PointHistoryResponse {
            return PointHistoryResponse(
                id = pointHistory.id,
                userId = pointHistory.userId,
                type = pointHistory.type,
                amount = pointHistory.amount,
                timeMillis = pointHistory.timeMillis,
            )
        }
    }
}