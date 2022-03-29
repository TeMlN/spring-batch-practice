# SpringBatch

본 소스코드의 mysql 환경으로 실행하려면 batch 메타테이블이 필요하니 spring-batch-core 라이브러리의 schema-mysql.sql 파일의 코드를 그대로 복사해서 테이블을 만들어주세요.


#### Batch Application의 조건
* 대용량 데이터: 배치 어플리케이션은 대량의 데이터를 가져오거나, 전달하거나, 계산하는 등의 처리를 할 수 있어야 합니다.
* 자동화: 배치 어플리케이션은 심각한 문제 해결을 제외하고는 **사용자 개입 없이 실행**되어야 합니다.
* 견고성: 배치 어플리케이션은 잘못된 데이터를 충돌/중단 없이 처리할 수 있어야 합니다.
* 신뢰성: 배치 어플리케이션은 무엇이 잘못 되어있는지를 추적할 수 있어야 합니다. (Log, 알림)
* 성능: 배치 어플리케이션은 **지정한 시간 안에 처리를 완료**하거나 동시에 실행되는 **다른 어플리케이션을 방해하지 않도록 수행**되어야 합니다.

<br>

**Spring Batch의 모든 Job은 클래스 레벨에 @Configuration을 통해 @Bean으로 등록해서 사용합니다.**

* Job

하나의  배치 작업 단위
Job 안에는 여러 Step이 존재하고, Step안에는 Tasklet 혹은 Reader & Process & Writer 묶음이 존재합니다.

* Step

Tasklet 하나와 Reader & Processor & Writer 한 묶음이 같은 레벨입니다
그래서 Reader & Processor가 끝나고 Tasklet으로 마무리 짓는 등으로 만들 순 없습니다

> Tasklet은 어떻게 보면 Spring 의 @Component, @Bean 과 비슷한 역할이라고 보셔도 됩니다.
> 명확한 역할은 없지만 개발자가 지정한 커스텀한 기능을 위한 단위입니다.


#### Reference
[이동욱님 Spring Batch](https://github.com/jojoldu/spring-batch-in-action)