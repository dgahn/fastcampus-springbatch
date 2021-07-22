package com.example.batch.part3

import mu.KotlinLogging
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.annotation.AfterJob
import org.springframework.batch.core.annotation.AfterStep
import org.springframework.batch.core.annotation.BeforeJob
import org.springframework.batch.core.annotation.BeforeStep
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener

private val logger = KotlinLogging.logger { }

class SavePersonJobExecutionListener : JobExecutionListener {

    override fun beforeJob(jobExecution: JobExecution) {
        logger.info { "beforeJob" }
    }

    override fun afterJob(jobExecution: JobExecution) {
        val count = jobExecution.stepExecutions.sumOf { it.writeCount }
        logger.info { "afterJob : $count" }
    }

}

class SavePersonAnnotationJobExecutionListener {

    @BeforeJob
    fun beforeJob(jobExecution: JobExecution) {
        logger.info { "annotation beforeJob" }
    }

    @AfterJob
    fun afterJob(jobExecution: JobExecution) {
        val count = jobExecution.stepExecutions.sumOf { it.writeCount }
        logger.info { "annotation afterJob : $count" }
    }

}

class SavePersonStepExecutionListener {
    @BeforeStep
    fun beforeStep(stepExecution: StepExecution) {
        logger.info { "beforeStep" }
    }

    @AfterStep
    fun afterStep(stepExecution: StepExecution): ExitStatus {
        logger.info { "afterStep : ${stepExecution.writeCount}" }
        if (stepExecution.writeCount == 0) {
            return ExitStatus.FAILED
        }
        return stepExecution.exitStatus
    }
}

class SavePersonRetryListener : RetryListener {
    override fun <T : Any?, E : Throwable?> open(context: RetryContext?, callback: RetryCallback<T, E>?): Boolean = true

    override fun <T : Any?, E : Throwable?> close(
        context: RetryContext?,
        callback: RetryCallback<T, E>?,
        throwable: Throwable?
    ) {
        logger.info { "close" }
    }

    override fun <T : Any?, E : Throwable?> onError(
        context: RetryContext?,
        callback: RetryCallback<T, E>?,
        throwable: Throwable?
    ) {
        logger.info { "onError" }
    }
}