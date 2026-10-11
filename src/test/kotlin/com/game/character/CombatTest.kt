package com.game.character

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Тесты расчёта урона, получения урона и атаки. */
class CombatTest {

    private val warrior = createCharacter("Артур", CharacterClass.WARRIOR) // HP 120, ATK 12, DEF 8
    private val mage = createCharacter("Мерлин", CharacterClass.MAGE)     // HP 80, ATK 18, DEF 3

    // ---- calculateDamage ----

    @Test
    fun `calculateDamage subtracts defense from attack`() {
        assertEquals(10, calculateDamage(15, 5))
    }

    @Test
    fun `calculateDamage never goes below minimum`() {
        assertEquals(MIN_DAMAGE, calculateDamage(5, 10))
    }

    @Test
    fun `calculateDamage gives minimum when attack equals defense`() {
        assertEquals(MIN_DAMAGE, calculateDamage(10, 10))
    }

    @Test
    fun `calculateDamage doubles attack on critical hit`() {
        assertEquals(25, calculateDamage(15, 5, critical = true))
    }

    @Test
    fun `calculateDamage rejects negative arguments`() {
        assertThrows(IllegalArgumentException::class.java) { calculateDamage(-1, 0) }
        assertThrows(IllegalArgumentException::class.java) { calculateDamage(1, -1) }
    }

    // ---- takeDamage ----

    @Test
    fun `takeDamage reduces hp`() {
        assertEquals(90, takeDamage(warrior, 30).hp)
    }

    @Test
    fun `takeDamage does not drop hp below zero`() {
        assertEquals(0, takeDamage(warrior, 500).hp)
    }

    @Test
    fun `takeDamage with zero damage keeps hp`() {
        assertEquals(120, takeDamage(warrior, 0).hp)
    }

    @Test
    fun `takeDamage does not modify original character`() {
        takeDamage(warrior, 30)
        assertEquals(120, warrior.hp)
    }

    @Test
    fun `takeDamage rejects negative damage`() {
        assertThrows(IllegalArgumentException::class.java) { takeDamage(warrior, -5) }
    }

    // ---- attack ----

    @Test
    fun `attack deals damage based on attack and defense`() {
        val result = attack(mage, warrior) // 18 - 8 = 10
        assertEquals(10, result.damageDealt)
        assertEquals(110, result.target.hp)
        assertFalse(result.targetDefeated)
    }

    @Test
    fun `attack with critical hit deals more damage`() {
        val result = attack(mage, warrior, critical = true) // 36 - 8 = 28
        assertEquals(28, result.damageDealt)
        assertEquals(92, result.target.hp)
    }

    @Test
    fun `attack reports defeated target`() {
        val weakened = warrior.copy(hp = 5)
        val result = attack(mage, weakened)
        assertEquals(0, result.target.hp)
        assertTrue(result.targetDefeated)
    }

    @Test
    fun `attack fails when attacker is dead`() {
        val dead = mage.copy(hp = 0)
        assertThrows(IllegalStateException::class.java) { attack(dead, warrior) }
    }

    @Test
    fun `attack fails when target is already dead`() {
        val dead = warrior.copy(hp = 0)
        assertThrows(IllegalStateException::class.java) { attack(mage, dead) }
    }

    // доп 2
    // CombatTest.kt
    @Test
    fun `calculateDamage with zero attack gives minimum damage`() {
        assertEquals(MIN_DAMAGE, calculateDamage(0, 0))
    }

    @Test
    fun `calculateDamage with zero defense returns full attack`() {
        assertEquals(15, calculateDamage(15, 0))
    }
}
