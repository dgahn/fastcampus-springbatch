package com.example.batch.part4

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "account")
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val username: String,

    @Enumerated(EnumType.STRING)
    var level: Level = Level.NORMAL,

    @OneToMany(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "account_id")
    val orders: List<Orders>? = null,

    var updatedDate: LocalDate? = null
) {
    private val totalAmount: Int
        get() = orders!!.sumOf { it.amount }

    fun availableLevelUp(): Boolean = level.availableLevelUp(totalAmount)

    fun levelUp(): Level {
        val nextLevel = level.getNexLevel(totalAmount)

        level = nextLevel
        updatedDate = LocalDate.now()

        return nextLevel
    }

    enum class Level(val nextAmount: Int, val nextLevel: Level?) {
        VIP(500_000, null),
        GOLD(500_000, VIP),
        SILVER(300_000, GOLD),
        NORMAL(200_000, SILVER);

        fun availableLevelUp(totalAmount: Int): Boolean {
            if (this.nextLevel == null) {
                return false
            }

            return totalAmount >= this.nextAmount
        }

        fun getNexLevel(totalAmount: Int): Level {
            if (totalAmount >= VIP.nextAmount) {
                return VIP
            }

            if (totalAmount >= GOLD.nextAmount) {
                return GOLD.nextLevel!!
            }

            if (totalAmount >= SILVER.nextAmount) {
                return SILVER.nextLevel!!
            }

            if (totalAmount >= NORMAL.nextAmount) {
                return NORMAL.nextLevel!!
            }

            return NORMAL
        }
    }
}