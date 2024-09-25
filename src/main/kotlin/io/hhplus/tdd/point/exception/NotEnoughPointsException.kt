package io.hhplus.tdd.point.exception

class NotEnoughPointsException(message: String = "포인트가 부족합니다."): PointException(message) {
}