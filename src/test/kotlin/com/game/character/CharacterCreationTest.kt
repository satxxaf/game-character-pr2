package com.game.character

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Тесты создания персонажа и проверки смерти. */
class CharacterCreationTest {

    @Test
    fun `createCharacter sets starting stats of warrior`() {
        val hero = createCharacter("Артур", CharacterClass.WARRIOR)
        assertEquals("Артур", hero.name)
        assertEquals(CharacterClass.WARRIOR, hero.characterClass)
        assertEquals(1, hero.level)
        assertEquals(0, hero.experience)
        assertEquals(120, hero.hp)
        assertEquals(120, hero.maxHp)
        assertEquals(12, hero.attack)
        assertEquals(8, hero.defense)
    }

    @Test
    fun `createCharacter sets starting stats of mage`() {
        val mage = createCharacter("Мерлин", CharacterClass.MAGE)
        assertEquals(80, mage.maxHp)
        assertEquals(18, mage.attack)
        assertEquals(3, mage.defense)
    }

    @Test
    fun `createCharacter trims name`() {
        val hero = createCharacter("  Артур  ", CharacterClass.ARCHER)
        assertEquals("Артур", hero.name)
    }

    @Test
    fun `createCharacter rejects blank name`() {
        assertThrows(IllegalArgumentException::class.java) {
            createCharacter("   ", CharacterClass.WARRIOR)
        }
    }

    @Test
    fun `createCharacter accepts name of maximum length`() {
        val name = "a".repeat(MAX_NAME_LENGTH)
        assertEquals(name, createCharacter(name, CharacterClass.WARRIOR).name)
    }

    @Test
    fun `createCharacter rejects too long name`() {
        val name = "a".repeat(MAX_NAME_LENGTH + 1)
        assertThrows(IllegalArgumentException::class.java) {
            createCharacter(name, CharacterClass.WARRIOR)
        }
    }

    @Test
    fun `isDead is false for fresh character`() {
        assertFalse(isDead(createCharacter("Артур", CharacterClass.WARRIOR)))
    }

    @Test
    fun `isDead is false with one hp left`() {
        val hero = createCharacter("Артур", CharacterClass.WARRIOR).copy(hp = 1)
        assertFalse(isDead(hero))
    }

    @Test
    fun `isDead is true with zero hp`() {
        val hero = createCharacter("Артур", CharacterClass.WARRIOR).copy(hp = 0)
        assertTrue(isDead(hero))
    }


    // доп 2
    // CharacterCreationTest.kt
    @Test
    fun `game character rejects non positive max hp`() {
        assertThrows(IllegalArgumentException::class.java) {
            GameCharacter("Артур", CharacterClass.WARRIOR, 1, 0, 0, 0, 12, 8)
        }
    }

    @Test
    fun `game character accepts zero attack and defense`() {
        val hero = GameCharacter("Артур", CharacterClass.WARRIOR, 1, 0, 120, 120, 0, 0)
        assertEquals(0, hero.attack)
        assertEquals(0, hero.defense)
    }
}
