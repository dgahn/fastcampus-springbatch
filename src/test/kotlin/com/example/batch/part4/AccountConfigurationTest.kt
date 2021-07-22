package com.example.batch.part4

import com.example.batch.TestConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate

@ContextConfiguration(classes = [AccountConfiguration::class, TestConfiguration::class])
class AccountConfigurationTest(
    private val jobLauncherTestUtils: JobLauncherTestUtils,
    private val accountRepo: AccountRepository
) : FunSpec({

//    afterEach {
//        accountRepo.deleteAll()
//    }

    test("테스트") {
        val jobExecution = jobLauncherTestUtils.launchJob()

        val size = accountRepo.findAllByUpdatedDate(LocalDate.now()).size

        val actual = jobExecution.stepExecutions
            .filter { it.stepName == "userLevelUpStep" }
            .sumOf { it.writeCount }

        actual shouldBe size
        actual shouldBe 300

        accountRepo.count() shouldBe 400
    }

})