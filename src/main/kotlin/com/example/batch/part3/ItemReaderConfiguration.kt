package com.example.batch.part3

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

private val logger = KotlinLogging.logger { }

@Configuration
class ItemReaderConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val dataSource: DataSource,
    private val entityManagerFactory: EntityManagerFactory
) {

    @Bean
    fun itemReaderJob(): Job = jobBuilderFactory.get("itemReaderJob")
        .incrementer(RunIdIncrementer())
        .start(customItemReaderStep())
        .next(csvFileStep())
        .next(jdbcStep())
        .next(jpaStep())
        .build()

    @Bean
    fun customItemReaderStep(): Step = stepBuilderFactory.get("customItemReaderStep")
        .chunk<Person, Person>(10)
        .reader(CustomItemReader(getItems()))
        .writer(itemWriter())
        .build()

    @Bean
    fun csvFileStep() = stepBuilderFactory.get("csvFilStep")
        .chunk<Person, Person>(10)
        .reader(csvFileItemReader())
        .writer(itemWriter())
        .build()

    @Bean
    fun jdbcStep() = stepBuilderFactory.get("jdbcStep")
        .chunk<Person, Person>(10)
        .reader(jdbcCursorItemReader())
        .writer(itemWriter())
        .build()

    @Bean
    fun jpaStep() = stepBuilderFactory.get("jpaStep")
        .chunk<Person, Person>(10)
        .reader(jpaCursorItemReader())
        .writer(itemWriter())
        .build()

    private fun jpaCursorItemReader() = JpaCursorItemReaderBuilder<Person>()
        .name("jpaCursorItemReader")
        .entityManagerFactory(entityManagerFactory)
        .queryString("SELECT p FROM Person p")
        .build().apply {
            this.afterPropertiesSet()
        }

    private fun jdbcCursorItemReader() = JdbcCursorItemReaderBuilder<Person>()
        .name("jdbcCursorItemReader")
        .dataSource(dataSource)
        .sql("SELECT id, name, age, address FROM person")
        .rowMapper { rs, rowNum ->
            Person(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4)
            )
        }
        .build().apply {
            this.afterPropertiesSet()
        }

    private fun csvFileItemReader(): FlatFileItemReader<Person> {
        val lineMapper = DefaultLineMapper<Person>()
        val tokenizer = DelimitedLineTokenizer()
        tokenizer.setNames("id", "name", "age", "address")
        lineMapper.setLineTokenizer(tokenizer)

        lineMapper.setFieldSetMapper {
            val id = it.readInt("id")
            val name = it.readString("name")
            val age = it.readString("age")
            val address = it.readString("address")

            Person(id, name, age, address)
        }

        return FlatFileItemReaderBuilder<Person>()
            .name("csvFileItemReader")
            .encoding("UTF-8")
            .resource(ClassPathResource("test.csv"))
            .linesToSkip(1)
            .lineMapper(lineMapper)
            .build()
            .apply {
                this.afterPropertiesSet() // 필수 프로퍼티에 대해 검증하는 메소드
            }
    }

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