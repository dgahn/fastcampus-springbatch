package com.example.batch.part3

import mu.KotlinLogging
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.ListItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger { }

@Configuration
class ChunkProcessingConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {

    @Bean
    fun chunkProcessingJob() = jobBuilderFactory.get("chunkProcessingJob")
        .incrementer(RunIdIncrementer())
        .start(this.taskBaseStep())
        .next(this.chunkBaseStep())
        .build()

    @Bean
    fun taskBaseStep() = stepBuilderFactory.get("taskBaseStep")
        .tasklet(tasklet())
        .build()

    @Bean
    @StepScope
    fun tasklet(@Value("#{jobParameters[chunkSize]}") value: String? = null): Tasklet {
        val items = getItems()

        return Tasklet { contribution, chunkContext ->
            val stepExecution = contribution.stepExecution

            val fromIndex = stepExecution.readCount
            val chunkSize = value?.toInt() ?: 10
            val toIndex = fromIndex + chunkSize

            if (fromIndex >= items.size) {
                RepeatStatus.FINISHED
            } else {
                val subList = items.subList(fromIndex, toIndex)

                logger.info { "task item size: ${subList.size}" }

                stepExecution.readCount = toIndex

                RepeatStatus.CONTINUABLE
            }
        }
    }

    private fun getItems(): List<String> {
        val items = mutableListOf<String>()
        for (i in 1..100) {
            items.add("$i + hello")
        }
        return items
    }

    @Bean
    @JobScope
    fun chunkBaseStep(@Value("#{jobParameters[chunkSize]}") chunkSize: String? = null) = stepBuilderFactory.get("chunkBaseStep")
        .chunk<String, String>(chunkSize?.toInt() ?: 10)
        .reader(itemReader())
        .processor(itemProcessor())
        .writer(itemWriter())
        .build()

    private fun itemWriter(): ItemWriter<String> =
        ItemWriter { items: List<String> -> logger.info { "chunk item size: ${items.size}" } }
//        ItemWriter { items: List<String> -> items.forEach(logger::info) }


    private fun itemProcessor(): ItemProcessor<String, String> {
        return ItemProcessor { item: String -> "$item, Spring Batch" }
    }

    private fun itemReader(): ItemReader<String?> = ListItemReader(getItems())

}