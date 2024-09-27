package io.hhplus.tdd.point.domain

import io.hhplus.tdd.point.controller.dto.UserPointRequest
import io.hhplus.tdd.point.repository.PointHistoryRepository
import io.hhplus.tdd.point.repository.UserPointRepository
import io.hhplus.tdd.point.repository.dto.PointHistory
import io.hhplus.tdd.point.repository.dto.UserPoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PointServiceImpl(
    private val pointLockContainer: PointLockContainer,
    private val userPointRepository: UserPointRepository,
    private val pointHistoryRepository: PointHistoryRepository
): PointService {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun getUserPoint(userId: Long): UserPoint {
        return userPointRepository.getUserPoint(userId)
    }

    /*
    * 포인트 충전 시에는 락을 사용하여 사용자 별로 동시성에 대한 안전성을 확보합니다.
    * 포인트 조회, 충전, 이력 등록은 같은 트랜잭션에 묶여있어야 하기 때문에 같이 lock을 걸었습니다.
    * */
    override fun charge(userPointRequest: UserPointRequest): UserPoint {
        return pointLockContainer.withLock(userPointRequest.userId) {
            val userPoint = userPointRepository.getUserPoint(userPointRequest.userId)

            logger.info("포인트 충전 요청: 사용자 ${userPointRequest.userId} 현재 포인트: ${userPoint.point}, 충전하려는 포인트: ${userPointRequest.amount}, ${System.currentTimeMillis()}")

            userPoint.charge(userPointRequest.amount)
            val result = userPointRepository.charge(userPoint)

            pointHistoryRepository.charge(userPoint)
            logger.info("포인트 충전 완료: 사용자: ${userPointRequest.userId}, amount: ${userPointRequest.amount}, 총 포인트: ${result.point}")
            result
        }
    }

    /*
    * 포인트 사용 시에는 락을 사용하여 사용자 별로 동시성에 대한 안전성을 확보합니다.
    * 포인트 조회, 사용, 이력 등록은 같은 트랜잭션에 묶여있어야 하기 때문에 같이 lock을 걸었습니다.
    * */
    override fun use(userPointRequest: UserPointRequest): UserPoint {
        return pointLockContainer.withLock(userPointRequest.userId) {
            val userPoint = userPointRepository.getUserPoint(userPointRequest.userId)

            logger.info("포인트 사용 요청: 사용자 ${userPointRequest.userId} 현재 포인트: ${userPoint.point}, 사용하려는 포인트: ${userPointRequest.amount}, ${System.currentTimeMillis()}")

            userPoint.use(userPointRequest.amount)
            val result = userPointRepository.use(userPoint)

            pointHistoryRepository.use(userPoint)
            logger.info("포인트 사용 완료: 사용자: ${userPointRequest.userId}, amount: ${userPointRequest.amount}, 총 포인트: ${result.point}")

            result
        }
    }

    override fun getPointHistories(userId: Long): List<PointHistory> {
        return pointHistoryRepository.getPointHistories(userId)
    }
}