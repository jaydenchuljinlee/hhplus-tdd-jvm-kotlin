# 동시성 제어 방식에 대한 분석 보고서

### 동시성 제어를 위해 어떤 방식을 사용했나?
> ReentrantLock을 사용하였다.<br>
> 해당 Lock Class의 경우 **재진입성, 유연한 Lock 획득, 공정성** 설정이 가능했기 때문이다.

- 재진입성의 예시

```kotlin
// 재진입 가능한 Lock을 제공하여 동일한 스레드가 획득한 Lock을 다시 획득할 수 있다.
lock.lock();  // 첫 번째 획득
try {
    someOtherMethod();
} finally {
    lock.unlock();  // 첫 번째 해제
}

void someOtherMethod() {
    lock.lock();  // 동일 스레드에서 두 번째 획득 (허용됨)
    try {
        // 작업 수행
    } finally {
        lock.unlock();  // 두 번째 해제
    }
}
```

- 유연한 Lock 획득 예시

```kotlin
// tryLock()을 통해 Lock을 얻을 때까지 기다릴 수 있고, timeout을 설정하여 Deadlock을 방지할 수도 있다.
if (lock.tryLock()) {
    try {
        // Lock을 획득했을 때 수행할 작업
    } finally {
        lock.unlock();
    }
} else {
    // Lock을 획득하지 못한 경우 처리
}
```

- 공정성에 대한 예시

```kotlin
// 다음과 같이 생성자에 true를 넣을 경우, 순차성이 보장된다.
val fairLock = ReentrantLock(true)  // 공정성 모드 활성화
```

### 어떤 방식으로 사용했나?
> 같은 사용자가 동시에 포인트를 충전, 사용하는 경우 데이터 정합성에 문제가 생길 수 있다.<br>
> 따라서, userId를 Key로 가지는 ConcurrentHashMap을 사용하여 다음과 같이 구현했다.

```kotlin
private val locks = ConcurrentHashMap<Long, ReentrantLock>()

fun <T> withLock(id: Long, func: () -> T): T {
    val lock = locks.computeIfAbsent(id) { ReentrantLock(true) } // 순서 보장을 위해 true로 선언

    lock.lock()

    try {
        return func()
    } finally {
        lock.unlock()
    }
}
```

### 어떤식으로 테스트를 진행했나?
> 3명의 사용자가 동시에 각각 자신의 포인트를 충전, 사용하려고 하는 상황을 재현했다.<br>
> 각각의 충전, 사용의 요청에는 약간의 시간을 두고 동시성 테스트를 진행했다.<br>
> 동시성 테스트를 재현함에 있엇서는 <b>CompletableFuture</b>를 사용했다.

- 사용 예시

```kotlin
@DisplayName("같은 사용자에 대한 동시 충전 및 사용 결과의 데이터 정합성이 보장된다.")
@Test
fun concurrencyTest() {
    // when
    val futures = listOf(
        // 사용자 0번에 대한 동시 요청 수행
        CompletableFuture.runAsync {
            Thread.sleep(10)
            pointService.charge(UserPointRequest.of(firstPoint.id, 1))
        },
        CompletableFuture.runAsync {
            Thread.sleep(15)
            pointService.use(UserPointRequest.of(firstPoint.id, 3))
        },
        // 사용자 1번에 대한 동시 요청 수행. 두 요청은 사용자 0번의 두 번째 요청보다 먼저 시작된다.
        CompletableFuture.runAsync {
            Thread.sleep(13)
            pointService.use(UserPointRequest.of(secondPoint.id, 2))
        },
        CompletableFuture.runAsync {
            Thread.sleep(14)
            pointService.charge(UserPointRequest.of(secondPoint.id, 2))
        },

        // 사용자 2번에 대한 동시 요청 수행. 두 요청은 사용자 0번의 두 번째 요청보다 먼저 시작된다.
        CompletableFuture.runAsync {
            Thread.sleep(13)
            pointService.use(UserPointRequest.of(thirdPoint.id, 1))
        },
        CompletableFuture.runAsync {
            Thread.sleep(14)
            pointService.charge(UserPointRequest.of(thirdPoint.id, 2))
        },
    )

    CompletableFuture.allOf(*futures.toTypedArray()).join()
    
    // Assertion을 통해 데이터 정합성 점검
}
```
