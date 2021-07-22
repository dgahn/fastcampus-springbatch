package com.example.batch.part3

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Person(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val name: String,
    val age: String,
    val address: String
) {
    fun isNotEmptyName(): Boolean = name.isNotBlank()
}
