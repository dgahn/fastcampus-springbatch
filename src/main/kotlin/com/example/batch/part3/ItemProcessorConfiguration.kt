package com.example.batch.part3

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger { }

@Configuration
class ItemProcessorConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun itemProcessorJob(): Job = jobBuilderFactory.get("itemProcessorJob")
        .incrementer(RunIdIncrementer())
        .start(itemProcessorStep())
        .build()

    @Bean
    fun itemProcessorStep(): Step = stepBuilderFactory.get("itemProcessorStep")
        .chunk<Person, Person>(10)
        .reader(itemReader())
        .processor(itemProcessor())
        .writer(itemWriter())
        .build()

    private fun itemWriter(): ItemWriter<in Person> = ItemWriter { items ->
        items.forEach { person -> logger.info { "PERSON.ID : ${person.id}" } }
    }

    private fun itemProcessor() = ItemProcessor<Person, Person> { item ->
        if (item.id!! % 2 == 0) item else null
    }

    private fun itemReader(): ItemReader<Person> = CustomItemReader(getItems())

    private fun getItems(): MutableList<Person> {
        val items = mutableListOf<Person>()
        for (i in 1..100) {
            items.add(Person(id = i, name = "test name $i", age = "test age", address = "test address"))
        }
        return items
    }

}