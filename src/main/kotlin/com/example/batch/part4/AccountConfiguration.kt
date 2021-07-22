package com.example.batch.part4

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
class AccountConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory,
    private val accountRepo: AccountRepository
) {

    @Bean
    fun userJob(): Job = jobBuilderFactory.get("userJob")
        .incrementer(RunIdIncrementer())
        .start(saveUserStep())
        .next(userLevelUpStep())
        .listener(LevelUpJobExecutionListener(accountRepo))
        .build()

    @Bean
    fun saveUserStep(): Step = stepBuilderFactory.get("saveUserStep")
        .tasklet(SaveAccountTasklet(accountRepo))
        .build()

    @Bean
    fun userLevelUpStep() = stepBuilderFactory.get("userLevelUpStep")
        .chunk<Account, Account>(100)
        .reader(itemReader())
        .processor(itemProcessor())
        .writer(itemWriter())
        .build()

    private fun itemWriter() = ItemWriter<Account> { users ->
        users.forEach { user ->
            user.levelUp()
            accountRepo.save(user)
        }
    }

    private fun itemReader() = JpaPagingItemReaderBuilder<Account>()
        .queryString("SELECT a FROM Account a")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(100)
        .name("userItemReader")
        .build()
        .apply { afterPropertiesSet() }

    private fun itemProcessor() = ItemProcessor<Account, Account> { user ->
        if (user.availableLevelUp()) {
            user
        } else {
            null
        }
    }
}