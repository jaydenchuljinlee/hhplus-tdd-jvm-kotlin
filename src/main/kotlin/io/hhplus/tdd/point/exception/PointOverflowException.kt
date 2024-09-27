package io.hhplus.tdd.point.exception

class PointOverflowException(message: String = "포인트의 범위가 지원하는 범위를 넘어섰습니다."): PointException(message) {
}