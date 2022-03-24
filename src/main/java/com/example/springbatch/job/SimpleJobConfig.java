package com.example.springbatch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
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
                .start(simpleStep1())
                .build();
    }

    @Bean
    public Step simpleStep1() {
        return stepBuilderFactory.get("simpleStep1")//simpleStep1이란 이름의 Batch job을 생성합니다.
                //job은 이렇게 별도로 이름을 생성하지 않고 builder에서 할당합니다
                .tasklet((contribution, chunkContext) -> { //.tasklet: Step안에서 수행될 기능들을 명시, Step안에서 단일로 수행될 커스텀한 기능들을 선언할때 사용합니다.
                    log.debug(">>>>> This is Step1"); //해당 Batch가 수행되면 log가 터집니다
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
