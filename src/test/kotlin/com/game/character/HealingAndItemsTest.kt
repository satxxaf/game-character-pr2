package com.game.character

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/** Тесты лечения и использования предметов. */
class HealingAndItemsTest {

    private val wounded = createCharacter("Артур", CharacterClass.WARRIOR).copy(hp = 50)

    // ---- heal ----

    @Test
    fun `heal restores hp`() {
        assertEquals(80, heal(wounded, 30).hp)
    }

    @Test
    fun `heal does not exceed max hp`() {
        assertEquals(120, heal(wounded, 100).hp)
    }

    @Test
    fun `heal with zero amount keeps hp`() {
        assertEquals(50, heal(wounded, 0).hp)
    }

    @Test
    fun `heal rejects dead character`() {
        assertThrows(IllegalStateException::class.java) { heal(wounded.copy(hp = 0), 10) }
    }

    @Test
    fun `heal rejects negative amount`() {
        assertThrows(IllegalArgumentException::class.java) { heal(wounded, -1) }
    }

    // ---- useItem ----

    @Test
    fun `healing potion restores hp`() {
        val potion = Item("Зелье", ItemType.HEALING_POTION, 40)
        assertEquals(90, useItem(wounded, potion).hp)
    }

    @Test
    fun `strength elixir increases attack`() {
        val elixir = Item("Эликсир силы", ItemType.STRENGTH_ELIXIR, 5)
        assertEquals(17, useItem(wounded, elixir).attack)
    }

    @Test
    fun `iron shield increases defense`() {
        val shield = Item("Железный щит", ItemType.IRON_SHIELD, 3)
        assertEquals(11, useItem(wounded, shield).defense)
    }

    @Test
    fun `useItem rejects dead character`() {
        val potion = Item("Зелье", ItemType.HEALING_POTION, 40)
        assertThrows(IllegalStateException::class.java) { useItem(wounded.copy(hp = 0), potion) }
    }

    @Test
    fun `item rejects non positive power`() {
        assertThrows(IllegalArgumentException::class.java) { Item("Пусто", ItemType.HEALING_POTION, 0) }
    }
}
