package com.example.batch.part4

import mu.KotlinLogging
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import java.time.LocalDate

private val logger = KotlinLogging.logger { }

class LevelUpJobExecutionListener(
    private val accountRepo: AccountRepository
) : JobExecutionListener {

    override fun beforeJob(jobExecution: JobExecution) { }

    override fun afterJob(jobExecution: JobExecution) {
        val accounts = accountRepo.findAllByUpdatedDate(LocalDate.now())

        val executeTime = jobExecution.endTime.time - jobExecution.startTime.time

        logger.info { "회원 등급 업데이트 배치 프로그램" }
        logger.info { "-----------------------" }
        logger.info { "총 데이터 처리 ${accounts.size}건, ${executeTime}millis" }
    }

}