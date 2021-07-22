package com.example.batch.part3

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.item.file.transform.FieldSet
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import javax.persistence.EntityManagerFactory

private val logger = KotlinLogging.logger {  }

@Configuration
class TaskConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory
) {

    @Bean
    fun taskJob(): Job = jobBuilderFactory.get("taskJob")
        .incrementer(RunIdIncrementer())
        .start(taskStep())
        .build()

    @Bean
    fun taskStep(): Step = stepBuilderFactory.get("taskStep")
        .chunk<Person, Person>(100)
        .reader(itemReader())
        .processor(itemProcessor())
        .writer(itemWriter())
        .build()

    private fun itemWriter() = CompositeItemWriterBuilder<Person>()
        .delegates(jpaItemWriter(), loggerItemWriter())
        .build()
        .apply { afterPropertiesSet() }

    private fun loggerItemWriter() = ItemWriter<Person> {
        logger.info { "size: ${it.size}" }
    }

    private fun jpaItemWriter() = JpaItemWriterBuilder<Person>()
        .entityManagerFactory(entityManagerFactory)
        .build()
        .apply { afterPropertiesSet() }

    private fun itemProcessor(): ItemProcessor<Person, Person> {
        val personMap = mutableMapOf<String, Person>()

        return ItemProcessor<Person, Person> {
            if(personMap[it.name] == null) {
                personMap[it.name] = it
                it
            } else {
                null
            }
        }
    }

    private fun itemReader(): FlatFileItemReader<Person> = FlatFileItemReaderBuilder<Person>()
        .name("csvFileItemReader")
        .encoding("UTF-8")
        .resource(ClassPathResource("test.csv"))
        .linesToSkip(1)
        .lineMapper(getPersonLineMapper())
        .build()
        .apply {
            this.afterPropertiesSet() // 필수 프로퍼티에 대해 검증하는 메소드 }
        }


    private fun getPersonLineMapper(): DefaultLineMapper<Person> = DefaultLineMapper<Person>()
        .apply {
            val tokenizer = DelimitedLineTokenizer().apply { setNames("name", "age", "address") }
            setLineTokenizer(tokenizer)
            setFieldSetMapper { getPersonFieldSet(it) }
        }

    private fun getPersonFieldSet(fieldSet: FieldSet): Person {
        val name = fieldSet.readString("name")
        val age = fieldSet.readString("age")
        val address = fieldSet.readString("address")

        return Person(name = name, age = age, address = address)
    }
}
