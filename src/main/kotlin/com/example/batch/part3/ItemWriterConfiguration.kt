package com.example.batch.part3

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor
import org.springframework.batch.item.file.transform.DelimitedLineAggregator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource

@Configuration
class ItemWriterConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun itemWriterJob(): Job = jobBuilderFactory.get("itemWriterJob")
        .incrementer(RunIdIncrementer())
        .start(itemWriterStep())
        .build()

    @Bean
    fun itemWriterStep(): Step = stepBuilderFactory.get("itemWriterStep")
        .chunk<Person, Person>(10)
        .reader(itemReader())
        .writer(csvFileItemWriter())
        .build()

    private fun csvFileItemWriter(): ItemWriter<in Person> {
        val fieldExtractor = BeanWrapperFieldExtractor<Person>()
        fieldExtractor.setNames(arrayOf("id", "name", "age", "address"))

        val lineAggregator = DelimitedLineAggregator<Person>()
        lineAggregator.setDelimiter(",")
        lineAggregator.setFieldExtractor(fieldExtractor)

        return FlatFileItemWriterBuilder<Person>()
            .name("csvFileItemWriter")
            .encoding("UTF-8")
            .resource(FileSystemResource("output/test-output.csv"))
            .lineAggregator(lineAggregator)
            .headerCallback { writer -> writer.write("id,이름,나이,거주지") }
            .footerCallback { writer -> writer.write("--------------\n") }
            .append(true) // csv 파일에 덧 붙이고 싶은 경우
            .build()
            .apply { this.afterPropertiesSet() }
    }

    private fun itemReader(): ItemReader<Person> = CustomItemReader(getItems())

    private fun getItems(): MutableList<Person> {
        val items = mutableListOf<Person>()
        for (i in 1..100) {
            items.add(Person(i, "test name $i", "test age", "test address"))
        }
        return items
    }

}