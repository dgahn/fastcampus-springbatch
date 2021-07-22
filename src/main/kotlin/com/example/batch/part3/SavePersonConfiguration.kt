package com.example.batch.part3

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import javax.persistence.EntityManagerFactory

private val logger = KotlinLogging.logger { }

@Configuration
class SavePersonConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory
) {

    @Bean
    fun savePersonJob(): Job = jobBuilderFactory.get("savePersonJob")
        .incrementer(RunIdIncrementer())
        .start(savePersonStep(null))
        .listener(SavePersonJobExecutionListener())
        .listener(SavePersonAnnotationJobExecutionListener())
        .build()

    @Bean
    @JobScope
    fun savePersonStep(@Value("#{jobParameters[allow_duplicate]}") allowDuplicate: String?): Step =
        stepBuilderFactory.get("savePersonStep")
            .chunk<Person, Person>(10)
            .reader(itemReader())
            .processor(itemProcessor(allowDuplicate))
            .writer(itemWriter())
            .listener(SavePersonStepExecutionListener())
            .faultTolerant()
            .skip(NotFoundNameException::class.java)
            .skipLimit(2)
            .build()

    private fun itemProcessor(allowDuplicate: String?): ItemProcessor<Person, Person> {
        val duplicateValidationProcessor = DuplicateValidationProcessor(Person::name, allowDuplicate.toBoolean())
        val validationProcessor = ItemProcessor<Person, Person> { item ->
            if (item.isNotEmptyName()) {
                item
            } else {
                throw NotFoundNameException()
            }
        }

        return CompositeItemProcessorBuilder<Person, Person>()
            .delegates(validationProcessor, duplicateValidationProcessor)
            .build()
            .apply { afterPropertiesSet() }
    }

    private fun itemWriter(): ItemWriter<in Person> {
        val jpaItemWriter = JpaItemWriterBuilder<Person>()
            .entityManagerFactory(entityManagerFactory)
            .build()

        val logItemWriter = ItemWriter<Person> { items ->
            logger.info { "person.size ${items.size}" }
        }

        return CompositeItemWriterBuilder<Person>()
            .delegates(jpaItemWriter, logItemWriter)
            .build()
            .apply { afterPropertiesSet() }
    }

    private fun itemReader(): ItemReader<out Person> {
        val lineMapper = DefaultLineMapper<Person>()
        val tokenizer = DelimitedLineTokenizer()
        tokenizer.setNames("name", "age", "address")
        lineMapper.setLineTokenizer(tokenizer)
        lineMapper.setFieldSetMapper {
            Person(
                name = it.readString(0),
                age = it.readString(1),
                address = it.readString(2)
            )
        }

        return FlatFileItemReaderBuilder<Person>()
            .name("savePersonItemReader")
            .encoding("UTF-8")
            .linesToSkip(1)
            .resource(ClassPathResource("test.csv"))
            .lineMapper(lineMapper)
            .build()
    }

}