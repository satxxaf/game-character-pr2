package com.game.character

/** Максимальная длина имени персонажа (после удаления крайних пробелов). */
const val MAX_NAME_LENGTH = 20

/** Минимальный урон, который наносит любая атака. */
const val MIN_DAMAGE = 1

/** Множитель урона при критическом ударе. */
const val CRITICAL_MULTIPLIER = 2

/** Количество опыта на один уровень (опыт для перехода = уровень * это значение). */
const val XP_PER_LEVEL = 100

/** Прирост максимального здоровья за уровень. */
const val HP_PER_LEVEL = 10

/** Прирост атаки за уровень. */
const val ATTACK_PER_LEVEL = 2

/** Прирост защиты за уровень. */
const val DEFENSE_PER_LEVEL = 1

/**
 * Создаёт нового персонажа 1 уровня со стартовыми характеристиками его класса.
 *
 * Крайние пробелы в имени удаляются. Здоровье изначально равно максимальному.
 *
 * Пример:
 * ```
 * val hero = createCharacter("Артур", CharacterClass.WARRIOR)
 * ```
 *
 * @param name имя персонажа; после удаления крайних пробелов должно быть
 *             непустым и не длиннее [MAX_NAME_LENGTH] символов
 * @param characterClass класс персонажа
 * @return новый персонаж: уровень 1, опыт 0, полное здоровье
 * @throws IllegalArgumentException если имя пустое или слишком длинное
 */
fun createCharacter(name: String, characterClass: CharacterClass): GameCharacter {
    val cleanName = name.trim()
    require(cleanName.isNotEmpty()) { "Имя персонажа не может быть пустым" }
    require(cleanName.length <= MAX_NAME_LENGTH) {
        "Имя персонажа не может быть длиннее $MAX_NAME_LENGTH символов"
    }
    return GameCharacter(
        name = cleanName,
        characterClass = characterClass,
        level = 1,
        experience = 0,
        hp = characterClass.baseHp,
        maxHp = characterClass.baseHp,
        attack = characterClass.baseAttack,
        defense = characterClass.baseDefense
    )
}

/**
 * Проверяет, мёртв ли персонаж.
 *
 * Персонаж считается мёртвым, когда его здоровье равно нулю.
 *
 * @param character проверяемый персонаж
 * @return `true`, если персонаж мёртв
 */
fun isDead(character: GameCharacter): Boolean = character.hp <= 0

/**
 * Рассчитывает урон одной атаки.
 *
 * Урон равен силе атаки (при критическом ударе — умноженной на
 * [CRITICAL_MULTIPLIER]) за вычетом защиты цели, но не меньше [MIN_DAMAGE].
 *
 * Пример: `calculateDamage(15, 5)` вернёт `10`.
 *
 * @param attackPower сила атаки атакующего (не отрицательная)
 * @param defense защита цели (не отрицательная)
 * @param critical `true`, если удар критический
 * @return итоговый урон, не меньше [MIN_DAMAGE]
 * @throws IllegalArgumentException если [attackPower] или [defense] отрицательны
 */
fun calculateDamage(attackPower: Int, defense: Int, critical: Boolean = false): Int {
    require(attackPower >= 0) { "Сила атаки не может быть отрицательной: $attackPower" }
    require(defense >= 0) { "Защита не может быть отрицательной: $defense" }
    val power = if (critical) attackPower * CRITICAL_MULTIPLIER else attackPower
    return maxOf(MIN_DAMAGE, power - defense)
}

/**
 * Наносит персонажу фиксированный урон (защита здесь не учитывается).
 *
 * Здоровье не опускается ниже нуля. Исходный объект не изменяется.
 *
 * @param character персонаж, получающий урон
 * @param damage величина урона (не отрицательная)
 * @return новый экземпляр персонажа с уменьшенным здоровьем
 * @throws IllegalArgumentException если [damage] отрицателен
 */
fun takeDamage(character: GameCharacter, damage: Int): GameCharacter {
    require(damage >= 0) { "Урон не может быть отрицательным: $damage" }
    return character.copy(hp = maxOf(0, character.hp - damage))
}

/**
 * Выполняет атаку одного персонажа на другого.
 *
 * Урон рассчитывается через [calculateDamage] по атаке атакующего и защите цели,
 * затем применяется к цели через [takeDamage].
 *
 * @param attacker атакующий (должен быть жив)
 * @param target цель атаки (должна быть жива)
 * @param critical `true`, если удар критический
 * @return [AttackResult] с обновлённой целью, уроном и признаком гибели цели
 * @throws IllegalStateException если атакующий или цель уже мертвы
 */
fun attack(attacker: GameCharacter, target: GameCharacter, critical: Boolean = false): AttackResult {
    check(!isDead(attacker)) { "Мёртвый персонаж не может атаковать: ${attacker.name}" }
    check(!isDead(target)) { "Цель уже мертва: ${target.name}" }
    val damage = calculateDamage(attacker.attack, target.defense, critical)
    val updatedTarget = takeDamage(target, damage)
    return AttackResult(
        target = updatedTarget,
        damageDealt = damage,
        targetDefeated = isDead(updatedTarget)
    )
}

/**
 * Восстанавливает здоровье персонажа.
 *
 * Здоровье не превышает [GameCharacter.maxHp]. Мёртвого персонажа вылечить нельзя.
 *
 * @param character лечимый персонаж (должен быть жив)
 * @param amount количество восстанавливаемого здоровья (не отрицательное)
 * @return новый экземпляр персонажа с восстановленным здоровьем
 * @throws IllegalArgumentException если [amount] отрицательно
 * @throws IllegalStateException если персонаж мёртв
 */
fun heal(character: GameCharacter, amount: Int): GameCharacter {
    require(amount >= 0) { "Количество лечения не может быть отрицательным: $amount" }
    check(!isDead(character)) { "Нельзя лечить мёртвого персонажа: ${character.name}" }
    return character.copy(hp = minOf(character.maxHp, character.hp + amount))
}

/**
 * Возвращает количество опыта, необходимое для перехода с уровня [level]
 * на следующий.
 *
 * Формула: `level * [XP_PER_LEVEL]`. Например, для 1 уровня это 100, для 3 — 300.
 *
 * @param level текущий уровень (не меньше 1)
 * @return количество опыта для перехода на следующий уровень
 * @throws IllegalArgumentException если [level] меньше 1
 */
fun xpForNextLevel(level: Int): Int {
    require(level >= 1) { "Уровень должен быть не меньше 1: $level" }
    return level * XP_PER_LEVEL
}

/**
 * Начисляет персонажу опыт и повышает уровень, если накоплено достаточно опыта.
 *
 * Когда опыт достигает порога [xpForNextLevel], уровень повышается, а остаток
 * опыта переносится на новый уровень. За каждый полученный уровень максимальное
 * здоровье растёт на [HP_PER_LEVEL], атака — на [ATTACK_PER_LEVEL], защита — на
 * [DEFENSE_PER_LEVEL]. Если уровень повысился хотя бы раз, здоровье полностью
 * восстанавливается. За один вызов можно получить несколько уровней.
 *
 * @param character персонаж, получающий опыт (должен быть жив)
 * @param amount количество опыта (не отрицательное)
 * @return новый экземпляр персонажа с обновлёнными опытом и характеристиками
 * @throws IllegalArgumentException если [amount] отрицательно
 * @throws IllegalStateException если персонаж мёртв
 */
fun gainExperience(character: GameCharacter, amount: Int): GameCharacter {
    require(amount >= 0) { "Количество опыта не может быть отрицательным: $amount" }
    check(!isDead(character)) { "Мёртвый персонаж не получает опыт: ${character.name}" }

    var level = character.level
    var experience = character.experience + amount
    var maxHp = character.maxHp
    var attackPower = character.attack
    var defense = character.defense

    // Повышаем уровень, пока накопленного опыта хватает на следующий
    while (experience > xpForNextLevel(level)) {
        experience -= xpForNextLevel(level)
        level++
        maxHp += HP_PER_LEVEL
        attackPower += ATTACK_PER_LEVEL
        defense += DEFENSE_PER_LEVEL
    }

    val leveledUp = level > character.level
    return character.copy(
        level = level,
        experience = experience,
        hp = if (leveledUp) maxHp else character.hp,
        maxHp = maxHp,
        attack = attackPower,
        defense = defense
    )
}

/**
 * Применяет предмет к персонажу.
 *
 * - [ItemType.HEALING_POTION] — лечит на [Item.power] (см. [heal]);
 * - [ItemType.STRENGTH_ELIXIR] — увеличивает атаку на [Item.power];
 * - [ItemType.IRON_SHIELD] — увеличивает защиту на [Item.power].
 *
 * @param character персонаж, использующий предмет (должен быть жив)
 * @param item применяемый предмет
 * @return новый экземпляр персонажа после применения предмета
 * @throws IllegalStateException если персонаж мёртв
 */
fun useItem(character: GameCharacter, item: Item): GameCharacter {
    check(!isDead(character)) { "Мёртвый персонаж не может использовать предметы: ${character.name}" }
    return when (item.type) {
        ItemType.HEALING_POTION -> heal(character, item.power)
        ItemType.STRENGTH_ELIXIR -> character.copy(attack = character.attack + item.power)
        ItemType.IRON_SHIELD -> character.copy(defense = character.defense + item.power)
    }
}
