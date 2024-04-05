[![codecov](https://codecov.io/gh/spring-templates/spring-concurrency-thread/graph/badge.svg?token=N3GEH8C5K7)](https://codecov.io/gh/spring-templates/spring-concurrency-thread)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?logo=junit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-D22128?logo=jacoco&logoColor=white)
![Codecov](https://img.shields.io/badge/Codecov-F01F7A?logo=codecov&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white)

# 관심사

- [멀티 스레드의 자원 공유 문제](https://github.com/spring-templates/spring-concurrency-thread/discussions/16)
- [멀티 쓰레드 자원 업데이트 문제](https://github.com/spring-templates/spring-concurrency-thread/discussions/17)

# 정보

- [동시성 기본 조건과 관심사](https://github.com/spring-templates/spring-concurrency-thread/discussions/2)

## 카운터 밴치 마크

**실험 환경**

칩 : Apple M2 8코어,
메모리 : 16GB

| 5회 평균                    | 최대 스레드 개수 | 전체 요청 수   | 테스트 시간(ms) | 메모리 사용량(MB) |
|--------------------------|-----------|-----------|------------|-------------|
| AtomicCounter            | 9         | 5,000,000 | 321.15     | 12.82       |
| AtomicCounter            | 15        | 5,000,000 | 419.33     | 12.16       |
| CompletableFutureCounter | 9         | 5,000,000 | 885.95     | 11.78       |
| CompletableFutureCounter | 15        | 5,000,000 | 939.16     | 11.78       |
| SynchronizedCounter      | 9         | 5,000,000 | 398.63     | 12.32       |
| SynchronizedCounter      | 15        | 5,000,000 | 495.99     | 11.86       |

- **프로세서** 12th Gen Intel(R) Core(TM) i7-12650H, 2300Mhz, 10 코어, 16 논리 프로세서

- **구현체** ConcurrentBatchingCounter

- **전체 요청 수** Integer.MAX_VALUE

| 스레드 개수 | 테스트 시간(s) | 메모리 사용량(MB) |
|--------|-----------|-------------|
| 1      | 26-28     | 4           |
| 2      | 14        | 4           |
| 4      | 9         | 4           |
| 8      | 7         | 4           |
| 16     | 5         | 4           |
| 32     | 5-6       | 4           |
| 64     | 6         | 4           |
| 128    | 9         | 337         |
| 1024   | 10        | 373         |
