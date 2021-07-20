package com.example.batch.part1

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger { }

@Configuration
class HelloConfiguration(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun helloJob(): Job {
        return jobBuilderFactory.get("helloJob")
            .incrementer(RunIdIncrementer()) // 새로운 잡 ID를 만듬.
            .start(this.helloStep())
            .build()
    }

    @Bean
    fun helloStep(): Step {
        return stepBuilderFactory.get("helloStep")
            .tasklet { contribution, chunkContext ->
                logger.info { "~~~~~~~~~~~~~~~~~~" }
                logger.info { "hello spring batch" }
                logger.info { "~~~~~~~~~~~~~~~~~~" }
                RepeatStatus.FINISHED
            }
            .build()
    }

}