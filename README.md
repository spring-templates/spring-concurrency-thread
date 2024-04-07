# spring-thread-concurrency

## 카운터 밴치 마크

**실험 환경**
칩 : Apple M2 8코어
메모리 : 16GB

| 5회 평균 | 최대 스레드 개수 | 전체 요청 수 | 테스트 시간(ms) | 메모리 사용량(MB) |
| --- | --- | --- | --- | --- |
| AtomicCounter | 9 | 5,000,000 | 321.15 | 12.82 |
| AtomicCounter | 15 | 5,000,000 | 419.33 | 12.16 |
| CompletableFutureCounter | 9 | 5,000,000 | 885.95 | 11.78 |
| CompletableFutureCounter | 15 | 5,000,000 | 939.16 | 11.78 |
| SynchronizedCounter | 9 | 5,000,000 | 398.63 | 12.32 |
| SynchronizedCounter | 15 | 5,000,000 | 495.99 | 11.86 |
