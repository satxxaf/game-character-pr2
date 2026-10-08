package com.game.character

/**
 * Форматирует краткую строку состояния персонажа для вывода в консоль.
 *
 * @param c персонаж
 * @return строка вида `Артур [WARRIOR] ур.1 HP 120/120 ATK 12 DEF 8 XP 0`
 */
private fun status(c: GameCharacter): String =
    "${c.name} [${c.characterClass}] ур.${c.level} HP ${c.hp}/${c.maxHp} " +
        "ATK ${c.attack} DEF ${c.defense} XP ${c.experience}"

/**
 * Точка входа: короткая демонстрация работы модуля «Игровой персонаж».
 */
fun main() {
    var hero = createCharacter("Артур", CharacterClass.WARRIOR)
    var enemy = createCharacter("Мерлин", CharacterClass.MAGE)
    println("Создано: ${status(hero)}")
    println("Создано: ${status(enemy)}")

    // Бой: враг бьёт героя, затем герой отвечает критическим ударом
    val hit1 = attack(enemy, hero)
    hero = hit1.target
    println("Мерлин наносит ${hit1.damageDealt} урона -> ${status(hero)}")

    val hit2 = attack(hero, enemy, critical = true)
    enemy = hit2.target
    println("Артур наносит ${hit2.damageDealt} урона (крит) -> ${status(enemy)}")

    // Лечение предметом
    val potion = Item("Малое зелье лечения", ItemType.HEALING_POTION, 40)
    hero = useItem(hero, potion)
    println("Зелье -> ${status(hero)}")

    // Опыт и уровень
    hero = gainExperience(hero, 150)
    println("+150 XP -> ${status(hero)}")

    println("Враг мёртв: ${isDead(enemy)}")
}
