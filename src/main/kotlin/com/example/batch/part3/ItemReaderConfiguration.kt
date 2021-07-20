package com.example.batch.part3

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger { }

@Configuration
class ItemReaderConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun itemReaderJob(): Job = jobBuilderFactory.get("itemReaderJob")
        .incrementer(RunIdIncrementer())
        .start(customItemReaderStep())
        .build()

    @Bean
    fun customItemReaderStep(): Step = stepBuilderFactory.get("customItemReaderStep")
        .chunk<Person, Person>(10)
        .reader(CustomItemReader(getItems()))
        .writer(itemWriter())
        .build()

    private fun itemWriter(): ItemWriter<in Person> = ItemWriter { items ->
        logger.info { items.joinToString { it.name } }
    }

    private fun getItems(): MutableList<Person> {
        val items = mutableListOf<Person>()

        for (i in 1..10) {
            items.add(Person(i, "$i test name", "test age", " test address"))
        }

        return items
    }

}