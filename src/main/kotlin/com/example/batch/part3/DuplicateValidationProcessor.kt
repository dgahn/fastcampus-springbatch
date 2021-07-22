package com.example.batch.part3

import org.springframework.batch.item.ItemProcessor
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

class DuplicateValidationProcessor<T>(
    private val keyExtractor: Function<T, String>,
    private val allowDuplicate: Boolean
) : ItemProcessor<T, T> {
    private val keyPool = ConcurrentHashMap<String, Any>()

    override fun process(item: T): T? {
        return if (allowDuplicate) {
            item
        } else {
            val key = keyExtractor.apply(item)
            if (keyPool.containsKey(key)) {
                null
            } else {
                keyPool.put(key, key)

                item
            }
        }
    }
}
