package com.example.springbatch.decide;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DecideJobConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job deciderJob() {
        return jobBuilderFactory.get("deciderJob")
                .start(firstStep()) // firstStep() 실행
                .next(decider()) // 짝수, 홀수중 하나를 랜덤으로 뽑아내는 decider() 실행
                .from(decider()) // decider()의 ExitStatus가
                    .on("ODD") // "ODD"라면
                    .to(oddStep()) // oddStep 실행
                .from(decider()) // oddStep을 제외한 다른 ExitStatus중
                    .on("EVEN") // "EVEN"이라면
                    .to(evenStep()) // evenStep 실행
                .end() // FlowBuilder 종료
                .build();
    }

    /**
     * 이처럼 왜 decider을 이용해서 분기 처리를 해야할까?
     * 이전 StepNextOptionsConfiguration처럼 직접 Step에서 ExitStatus를 설정해서 분기 처리시
     * Step이 담당하는 역할이 2개 이상이됨 - SRP 위반
     * 해당 Step이 처리해야할 로직 외에도 분기처리를 시키기 위해 시키기 위해 ExitStatus 조작이 필요하다
     *
     * 다양한 분기 로직 처리의 어려움
     * ExitStatus를 커스텀하게 고치기 위해선 Listener(.from)를 생성하고 Job Flow에 등록하는 등 코드를 수정하는일이 늘어나고 번거로움이 늘어남
     */

    @Bean
    public Step firstStep() {
        return stepBuilderFactory.get("firstStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>> This is First Step");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step oddStep() {
        return stepBuilderFactory.get("oddStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("홀수 입니다");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step evenStep() {
        return stepBuilderFactory.get("evenStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("짝수 입니다");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    @Bean
    public JobExecutionDecider decider() {
        return new OddDecider();
    }

    public static class OddDecider implements JobExecutionDecider {


        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            Random random = new Random();

            int randomNumber = random.nextInt(50) + 1;
            log.info("랜덤숫자: {}", randomNumber);

            if (randomNumber % 2 == 0) {
                return new FlowExecutionStatus("EVEN");
            } else {
                return new FlowExecutionStatus("ODD");
            }
        }
    }
}
