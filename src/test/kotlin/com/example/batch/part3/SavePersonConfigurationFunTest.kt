package com.example.batch.part3

import com.example.batch.TestConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

//@SpringBatchTest
@ContextConfiguration(classes = [SavePersonConfiguration::class, TestConfiguration::class])
class SavePersonConfigurationFunTest(
    private val jobLauncherTestUtils: JobLauncherTestUtils,
    private val personRepository: PersonRepository
): FunSpec({
    extension(SpringExtension)

    afterEach {
        personRepository.deleteAll()
    }

    test("step에 대해서 테스트를 진행할 수 있다.") {
        val launchStep = jobLauncherTestUtils.launchStep("savePersonStep")

        launchStep.stepExecutions.sumOf { it.writeCount } shouldBe 3
    }

    test("이름 중복을 허락하지 않으면 Person을 3명 저장한다") {
        val jobParameters = JobParametersBuilder()
            .addString("allow_duplicate", "false")
            .toJobParameters()

        // when
        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        // then
        jobExecution.stepExecutions.sumOf { it.writeCount } shouldBe 3
        personRepository.count() shouldBe 3
    }

    test("이름 중복을 허락하면 Person을 100명 저장한다") {
        val jobParameters = JobParametersBuilder()
            .addString("allow_duplicate", "true")
            .toJobParameters()

        // when
        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        // then
        jobExecution.stepExecutions.sumOf { it.writeCount } shouldBe 99
        personRepository.count() shouldBe 99
    }

})
