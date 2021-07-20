package com.example.batch.part3

import org.springframework.batch.item.ItemReader

class CustomItemReader<T>(private val items: MutableList<T>) : ItemReader<T> {

    override fun read(): T? {
        if(items.isNotEmpty()) {
            return items.removeAt(0)
        }

        return null
    }

}