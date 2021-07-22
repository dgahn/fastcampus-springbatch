package com.example.batch.part4

import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import java.time.LocalDate

class SaveAccountTasklet(
    private val accountRepo: AccountRepository
) : Tasklet {
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val users = createUsers().shuffled()
        accountRepo.saveAll(users);
        return RepeatStatus.FINISHED
    }

    private fun createUsers(): List<Account> {
        val users = mutableListOf<Account>()

        for (i in 1..100) {
            users.add(
                Account(
                    username = "test username $i",
                    totalAmount = 1_000
                )
            )
        }

        for (i in 101..200) {
            users.add(
                Account(
                    username = "test username $i",
                    totalAmount = 200_000
                )
            )
        }

        for (i in 201..300) {
            users.add(
                Account(
                    username = "test username $i",
                    totalAmount = 300_000
                )
            )
        }

        for (i in 301..400) {
            users.add(
                Account(
                    username = "test username $i",
                    totalAmount = 500_000
                )
            )
        }


        return users
    }
}