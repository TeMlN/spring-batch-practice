package com.example.springbatch.next;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepNextOptionsConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepNextOptions() {
        return jobBuilderFactory.get("stepNextOptions")
                .start(conditionalStep1()) // conditionalStep1 Step Batch 시작
                    .on("FAILED") // conditionalStep1의 ExitStatus가 FAILED라면
                    .to(conditionalStep3()) // conditionalStep3 Step 시작
                    .on("*") // conditionalStep3의 결과와 상관없이
                    .end() // conditionalStep3이 실행하면 flow가 종료
                .from(conditionalStep1()) //conditionalStep1 로부터
                    .on("*") // FAILED 외에 모든 경우
                    .to(conditionalStep2()) // conditionalStep2 Step 실행
                    .next(conditionalStep3()) // conditionalStep2 이 정상 종료되면 conditionalStep3 시작
                    .on("*") // conditionalStep3의 결과와 상관없이
                    .end() // flow 종료
                .end() // job 종료
                .build();

        /**

         해당 Job의 시나리오
         step1 실패 시: conditionalStep1 -> conditionalStep3
         step1 성공 시: conditionalStep1 -> conditionalStep2 -> conditionalStep3


         .on()
            catch할 ExitStatus지정
            *일 경우 모든 ExitStatus가 지정
         .to()
            다음으로 이동할 Step정의
         .from()
            상태값을 보고 일치하는 상태라면 to()에 포함된 step을 호출
            위 예제를 예로들어 처음 Job을 start하고나서 FAILED를 검증함, 그래서 2번째 conditionalStep1을 실행하면(from) FAILED로 되어있지만 추가로 이벤트 캐치가 가능.
            그래서 이벤트가 캐채되었지만 추가로 이벤트를 캐치하려면 from을 사용헤야만 함

         job이 build() 하기전 .end()는 FlowBuilder를 종료
         이벤트 캐치후 .end()는 FlowBuilder를 반환

         on이 캐치하는 상태값은 ExitStatus이지 BatchStatus가 아니다 절대 혼동하면 안된다 (성공한 Batch의 ExitStatus는 EXECUTING이다)

         **/
    }

    @Bean
    public Step conditionalStep1() {
        return stepBuilderFactory.get("conditionalStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is conditionalStep1");

                    contribution.setExitStatus(ExitStatus.FAILED); //실패한 Batch 예제를 위한 ExitStatus.FAILED
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalStep2() {
        return stepBuilderFactory.get("conditionalStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is conditionalStep2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalStep3() {
        return stepBuilderFactory.get("conditionalStep3")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>> This is conditionalStep3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
