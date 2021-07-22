package com.example.batch.part3

import com.example.batch.TestConfiguration
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

@SpringBatchTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [SavePersonConfiguration::class, TestConfiguration::class])
class SavePersonConfigurationTest {

    @Autowired
    lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Test
    fun `step 테스트`() {
        val launchStep = jobLauncherTestUtils.launchStep("savePersonStep")

        launchStep.stepExecutions.sumOf { it.writeCount } shouldBe 3
    }

    @Test
    fun `중복을 허락한 Person을 저장할 수 있다`() {
        // given
        val jobParameters = JobParametersBuilder()
            .addString("allow_duplicate", "false")
            .toJobParameters()

        // when
        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        // then
        assertThat(jobExecution.stepExecutions.map { it.writeCount }.sum()).isEqualTo(3)
    }

}