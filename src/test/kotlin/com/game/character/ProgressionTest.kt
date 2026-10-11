package com.game.character

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/** Тесты системы опыта и уровней. */
class ProgressionTest {

    private val warrior = createCharacter("Артур", CharacterClass.WARRIOR) // HP 120, ATK 12, DEF 8

    // ---- xpForNextLevel ----

    @Test
    fun `xpForNextLevel grows with level`() {
        assertEquals(100, xpForNextLevel(1))
        assertEquals(300, xpForNextLevel(3))
    }

    @Test
    fun `xpForNextLevel rejects level below one`() {
        assertThrows(IllegalArgumentException::class.java) { xpForNextLevel(0) }
    }

    // ---- gainExperience ----

    @Test
    fun `gainExperience below threshold keeps level`() {
        val result = gainExperience(warrior, 50)
        assertEquals(1, result.level)
        assertEquals(50, result.experience)
        assertEquals(120, result.maxHp)
    }

    @Test
    fun `gainExperience with zero amount changes nothing`() {
        assertEquals(warrior, gainExperience(warrior, 0))
    }

    @Test
    fun `gainExperience above threshold levels up and keeps remainder`() {
        val result = gainExperience(warrior, 150)
        assertEquals(2, result.level)
        assertEquals(50, result.experience)
        assertEquals(130, result.maxHp)
        assertEquals(14, result.attack)
        assertEquals(9, result.defense)
    }

    @Test
    fun `gainExperience restores hp on level up`() {
        val wounded = warrior.copy(hp = 10)
        assertEquals(130, gainExperience(wounded, 150).hp)
    }

    @Test
    fun `gainExperience keeps hp without level up`() {
        val wounded = warrior.copy(hp = 10)
        assertEquals(10, gainExperience(wounded, 20).hp)
    }

    @Test
    fun `gainExperience can give several levels at once`() {
        val result = gainExperience(warrior, 350) // 100 -> L2, 200 -> L3, 50 left
        assertEquals(3, result.level)
        assertEquals(50, result.experience)
        assertEquals(140, result.maxHp)
        assertEquals(16, result.attack)
        assertEquals(10, result.defense)
    }

    @Test
    fun `gainExperience accumulates experience across calls`() {
        val step1 = gainExperience(warrior, 40)
        val step2 = gainExperience(step1, 30)
        assertEquals(70, step2.experience)
        assertEquals(1, step2.level)
    }

    @Test
    fun `gainExperience rejects negative amount`() {
        assertThrows(IllegalArgumentException::class.java) { gainExperience(warrior, -1) }
    }

    @Test
    fun `gainExperience rejects dead character`() {
        assertThrows(IllegalStateException::class.java) { gainExperience(warrior.copy(hp = 0), 10) }
    }

    // доп
    @Test
    fun `gainExperience levels up when experience equals threshold`() {
        val result = gainExperience(warrior, 100) // ровно порог для 2 уровня

        assertEquals(2, result.level)
        assertEquals(0, result.experience)
        assertEquals(130, result.maxHp)
        assertEquals(130, result.hp)     // здоровье восстановлено полностью
        assertEquals(14, result.attack)
        assertEquals(9, result.defense)
    }
}
