package com.game.character

/**
 * Класс (профессия) игрового персонажа.
 *
 * От класса зависят стартовые характеристики персонажа на 1 уровне.
 *
 * @property baseHp стартовый (и максимальный) запас здоровья
 * @property baseAttack стартовая сила атаки
 * @property baseDefense стартовая защита
 */
enum class CharacterClass(
    val baseHp: Int,
    val baseAttack: Int,
    val baseDefense: Int
) {
    /** Прочный боец ближнего боя. */
    WARRIOR(baseHp = 120, baseAttack = 12, baseDefense = 8),

    /** Хрупкий, но сильный в атаке заклинатель. */
    MAGE(baseHp = 80, baseAttack = 18, baseDefense = 3),

    /** Сбалансированный дальнобойный боец. */
    ARCHER(baseHp = 100, baseAttack = 15, baseDefense = 5)
}

/**
 * Игровой персонаж. Объект неизменяемый: все операции из
 * [CharacterOperations.kt] возвращают новый экземпляр.
 *
 * @property name имя персонажа (не пустое)
 * @property characterClass класс персонажа
 * @property level текущий уровень (не меньше 1)
 * @property experience опыт, накопленный на текущем уровне (не отрицательный)
 * @property hp текущее здоровье, в диапазоне `0..maxHp`
 * @property maxHp максимальное здоровье (положительное)
 * @property attack сила атаки (не отрицательная)
 * @property defense защита (не отрицательная)
 * @throws IllegalArgumentException если какое-либо поле нарушает ограничения
 */
data class GameCharacter(
    val name: String,
    val characterClass: CharacterClass,
    val level: Int,
    val experience: Int,
    val hp: Int,
    val maxHp: Int,
    val attack: Int,
    val defense: Int
) {
    init {
        require(name.isNotBlank()) { "Имя персонажа не может быть пустым" }
        require(level >= 1) { "Уровень должен быть не меньше 1, получено: $level" }
        require(experience >= 0) { "Опыт не может быть отрицательным: $experience" }
        require(maxHp > 0) { "Максимальное здоровье должно быть положительным: $maxHp" }
        require(hp in 0..maxHp) { "Здоровье должно быть в диапазоне 0..$maxHp, получено: $hp" }
        require(attack >= 0) { "Атака не может быть отрицательной: $attack" }
        require(defense >= 0) { "Защита не может быть отрицательной: $defense" }
    }
}

/**
 * Тип предмета, который можно использовать на персонаже.
 */
enum class ItemType {
    /** Восстанавливает здоровье на [Item.power] единиц. */
    HEALING_POTION,

    /** Постоянно увеличивает атаку на [Item.power] единиц. */
    STRENGTH_ELIXIR,

    /** Постоянно увеличивает защиту на [Item.power] единиц. */
    IRON_SHIELD
}

/**
 * Предмет, используемый персонажем.
 *
 * @property name название предмета (не пустое)
 * @property type тип предмета
 * @property power сила эффекта (строго положительная)
 * @throws IllegalArgumentException если название пустое или [power] не положительна
 */
data class Item(
    val name: String,
    val type: ItemType,
    val power: Int
) {
    init {
        require(name.isNotBlank()) { "Название предмета не может быть пустым" }
        require(power > 0) { "Сила предмета должна быть положительной: $power" }
    }
}

/**
 * Результат одной атаки.
 *
 * @property target цель после получения урона (новый экземпляр)
 * @property damageDealt рассчитанный урон атаки
 * @property targetDefeated `true`, если после атаки цель мертва
 */
data class AttackResult(
    val target: GameCharacter,
    val damageDealt: Int,
    val targetDefeated: Boolean
)
