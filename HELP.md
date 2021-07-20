# 스프링 배치

## 스프링 배치의 구조

- Job : 배치에서 하는 작업 실행의 단위
- JobLauncher : 작업을 실행하는 클래스
- JobRepository : Job에 대한 meta 데이터를 저장하는 클래스
- Step : Job의 단위를 쪼갠 것으로 흐름을 관리할 수 있다.
    - 예를 들어, 조건에 따라 작업을 선택할 수 있다.
- Step의 실행 단위는 크게 2가지로 나눌 수 있다.
    1. Chunk 기반 : 하나의 큰 덩어리를 N개씩 나눠서 실행
    2. Task 기반 : 하나의 작업 기반으로 실행
- ItemReader : 배치 처리 대상을 읽는다.
    - 예를 들면, 파일 또는 DB에서 데이터를 읽는다.
- ItemProcessor는 입력 값을 출력 값으로 처리하는 과정을 한다. 
    - ItemReader에서 읽은 데이터를 ItemWriter 대상인지 선정한다.
    - ItemProcessor는 옵션이고
    - ItemProcessor가 하는 일은 ItemReader 또는 ItemWriter가 대신할 수 있다.
- ItemWriter는 배치 처리 대상 객체를 처리한다.
    - 예를 들어, DB update를 하거나, 처리 대상 사용자에게 알림을 보낸다.
    
## 스프링 배치 테이블 구조와 이해

- BATCH_JOB_INSTANCE
- BATCH_JOB_EXECUTION
- BATCH_JOB_EXECUTION_PARAMS
- BATCH_JOB_EXECUTION_CONTEXT
- BATCH_STEP_EXECUTION
- BATCH_STEP-EXECUTION_CONTEXT

## 스프링 배치 클래스와 배치 테이블 매핑

- JobInstance : BATCH_JOB_INSTANCE 테이블과 매핑
- JobExecution : BATCH_JOB_EXECUTION 테이블과 매핑
- JobParameters : BATCH_JOB_EXECUTION_PARAMS 테이블과 매핑
- ExecutionContext : BATCH_JOB_EXECUTION_CONTEXT 테이블과 매핑
