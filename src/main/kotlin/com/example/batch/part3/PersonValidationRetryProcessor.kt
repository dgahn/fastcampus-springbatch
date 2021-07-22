package com.example.batch.part3

import org.springframework.batch.item.ItemProcessor
import org.springframework.retry.RecoveryCallback
import org.springframework.retry.RetryCallback
import org.springframework.retry.support.RetryTemplateBuilder

class PersonValidationRetryProcessor : ItemProcessor<Person, Person> {

    private val retryTemplate = RetryTemplateBuilder()
        .maxAttempts(3)
        .retryOn(NotFoundNameException::class.java)
        .withListener(SavePersonRetryListener())
        .build()

    override fun process(item: Person): Person? = this.retryTemplate
        .execute(
            RetryCallback<Person, NotFoundNameException> { if (item.isNotEmptyName()) item else throw NotFoundNameException() },
            RecoveryCallback { item.unknownName() }
        )
}