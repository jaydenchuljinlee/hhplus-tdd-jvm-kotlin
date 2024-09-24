package io.hhplus.tdd.point.service

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

@Component
class PointLockContainer {
    private val locks = ConcurrentHashMap<Long, ReentrantLock>()

    fun <T> withLock(id: Long, func: () -> T): T {
        val lock = locks.computeIfAbsent(id) { ReentrantLock() }

        lock.lock()

        try {
            return func()
        } finally {
            lock.unlock()
        }
    }
}