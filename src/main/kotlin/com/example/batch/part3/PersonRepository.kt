package com.example.batch.part3

import org.springframework.data.jpa.repository.JpaRepository

interface PersonRepository : JpaRepository<Person, Int>