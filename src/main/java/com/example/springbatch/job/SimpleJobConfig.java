package com.example.springbatch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob") //simpleJob이란 이름의 Batch job을 생성합니다.
                //job은 이렇게 별도로 이름을 생성하지 않고 builder에서 할당합니다
                .start(simpleStep1(null))
                .start(simpleStep2(null))
                .build();
    }

    @Bean
    @JobScope
    // 같은 Batch Job이라도 Job Parameter가 다르면 BATCH_JOB_INSTANCE 메타 테이블에 기록 되지만
    // Job Parameter가 같은 Batch가 2번 이상 실행되도 기록되지 않습니다
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep1")//simpleStep1이란 이름의 Batch job을 생성합니다.
                //job은 이렇게 별도로 이름을 생성하지 않고 builder에서 할당합니다
                .tasklet((contribution, chunkContext) -> { //.tasklet: Step안에서 수행될 기능들을 명시, Step안에서 단일로 수행될 커스텀한 기능들을 선언할때 사용합니다.
                    log.info(">>>>> This is Step1"); //해당 Batch가 수행되면 log가 터집니다
                    log.info(">>>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
//                    throw new IllegalArgumentException("step1에서 실패합니다.");
                })
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>> This is Step2");
                    log.info(">>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
