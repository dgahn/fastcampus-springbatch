package com.example.batch.part2

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger { }

@Configuration
class SharedConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun shareJob(): Job = jobBuilderFactory.get("shareJob")
        .incrementer(RunIdIncrementer())
        .start(this.shareStep())
        .next(this.shareStep2())
        .build()

    @Bean
    fun shareStep() = stepBuilderFactory.get("shareStep")
        .tasklet { contribution, chunkContext ->
            val stepExecution = contribution.stepExecution
            val stepExecutionContext = stepExecution.executionContext
            stepExecutionContext.putString("stepKey", "step execution context")

            val jobExecution = stepExecution.jobExecution
            val jobInstance = jobExecution.jobInstance
            val jobExecutionContext = jobExecution.executionContext
            jobExecutionContext.putString("jobKey", "job execution context")
            val jobParameter = jobExecution.jobParameters

            logger.info {
                "jobName : ${jobInstance.jobName}, "
                "stepName : ${stepExecution.stepName}, "
                "parameter : ${jobParameter.getLong("run.id")}"
            }

            RepeatStatus.FINISHED
        }
        .build()

    @Bean
    fun shareStep2() = stepBuilderFactory.get("shareStep2")
        .tasklet { contribution, chunkContext ->
            val stepExecution = contribution.stepExecution
            val stepExecutionContext = stepExecution.executionContext

            val jobExecution = stepExecution.jobExecution
            val jobExecutionContext = jobExecution.executionContext

            logger.info {
                "jobKey: ${jobExecutionContext.getString("jobKey", "emtpyJobKey")}"
                "stepKey: ${stepExecutionContext.getString("stepKey", "emtpyJobKey")}"
            }
            RepeatStatus.FINISHED
        }
        .build()

}