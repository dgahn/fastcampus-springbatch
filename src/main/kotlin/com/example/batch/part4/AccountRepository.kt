package com.example.batch.part4

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AccountRepository : JpaRepository<Account, Long> {
    fun findAllByUpdatedDate(updatedDate: LocalDate): List<Account>
}