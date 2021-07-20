package com.example.batch

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBatchProcessing
class FastcampusSpringbatchApplication

fun main(args: Array<String>) {
    runApplication<FastcampusSpringbatchApplication>(*args)
}
