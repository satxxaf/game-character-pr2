# game-character — модуль «Игровой персонаж»

Небольшой программный модуль на Kotlin для моделирования игрового персонажа:
создание, бой, лечение, предметы, опыт и уровни. Подготовлен для модульного и
мутационного тестирования (практическая работа №2).

## Функциональность

Все функции находятся в `src/main/kotlin/com/game/character/CharacterOperations.kt`.
Персонаж (`GameCharacter`) — **неизменяемый** объект: каждая операция возвращает новый экземпляр.

| Функция | Назначение |
|---|---|
| `createCharacter(name, characterClass)` | Создаёт персонажа 1 уровня со стартовыми характеристиками класса |
| `isDead(character)` | Проверяет, мёртв ли персонаж |
| `calculateDamage(attackPower, defense, critical)` | Рассчитывает урон атаки с учётом защиты и критического удара |
| `takeDamage(character, damage)` | Применяет к персонажу фиксированный урон |
| `attack(attacker, target, critical)` | Атака одного персонажа на другого, возвращает `AttackResult` |
| `heal(character, amount)` | Восстанавливает здоровье (не выше максимума) |
| `xpForNextLevel(level)` | Опыт, необходимый для перехода на следующий уровень |
| `gainExperience(character, amount)` | Начисляет опыт и повышает уровень |
| `useItem(character, item)` | Применяет предмет (зелье, эликсир, щит) |

### Классы персонажей

| Класс | HP | Атака | Защита |
|---|---|---|---|
| `WARRIOR` | 120 | 12 | 8 |
| `MAGE` | 80 | 18 | 3 |
| `ARCHER` | 100 | 15 | 5 |

### Игровые правила (кратко)

- Урон = атака (при критическом ударе ×2) − защита цели, но не менее 1.
- Здоровье не может быть меньше 0 и больше максимума.
- Мёртвый персонаж не может атаковать, лечиться, получать опыт и использовать предметы
  (выбрасывается `IllegalStateException`).
- Некорректные аргументы приводят к `IllegalArgumentException`.
- Для перехода с уровня *N* на *N+1* нужно `N * 100` опыта; остаток опыта переносится.
- За каждый уровень: максимальное HP +10, атака +2, защита +1; при повышении уровня HP восстанавливается полностью.
- Предметы: `HEALING_POTION` (лечение), `STRENGTH_ELIXIR` (+атака), `IRON_SHIELD` (+защита).

Подробное описание каждой функции — в KDoc-комментариях исходного кода.

## Структура проекта

```
game-character/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore
├── README.md
└── src/
    ├── main/kotlin/com/game/character/
    │   ├── Models.kt                 # CharacterClass, GameCharacter, ItemType, Item, AttackResult
    │   ├── CharacterOperations.kt    # основные функции модуля
    │   └── Main.kt                   # демонстрационный запуск
    └── test/kotlin/com/game/character/
        ├── CharacterCreationTest.kt
        ├── CombatTest.kt
        ├── HealingAndItemsTest.kt
        └── ProgressionTest.kt
```

## Зависимости

| Компонент | Версия |
|---|---|
| JDK | 21 или новее |
| Gradle | 8.5 или новее (Kotlin DSL) |
| Kotlin (плагин `kotlin("jvm")`) | 2.0.21 |
| JUnit Jupiter (через `junit-bom`) | 5.10.3 |
| gradle-pitest-plugin (мутационное тестирование) | 1.15.0 |
| PIT JUnit 5 plugin | 1.2.1 |

Для первой сборки нужен доступ в интернет (загрузка зависимостей из Maven Central и Gradle Plugin Portal).

## Сборка, запуск, тесты

Gradle Wrapper в архив не включён. Если Gradle установлен глобально — достаточно команд ниже.
Чтобы создать wrapper: `gradle wrapper --gradle-version 8.10.2`, после чего используйте `./gradlew`
(в Windows — `gradlew.bat`). Проект также можно открыть в IntelliJ IDEA как Gradle-проект.

```bash
gradle build          # компиляция + запуск тестов
gradle run            # демонстрационный запуск (Main.kt)
gradle test           # только модульные тесты
```

HTML-отчёт тестов: `build/reports/tests/test/index.html`.

### Мутационное тестирование (PIT)

```bash
gradle pitest
```

Отчёт: `build/reports/pitest/index.html`. Настройки находятся в блоке `pitest { ... }` файла
`build.gradle.kts`. Замечание: PIT для Kotlin может создавать «шумовые» мутанты в
синтетическом коде, который генерирует компилятор (например, в `data class`); это нормально —
при анализе ориентируйтесь на мутанты в функциях `CharacterOperations.kt`.

## Примеры использования

```kotlin
import com.game.character.*

fun main() {
    var hero = createCharacter("Артур", CharacterClass.WARRIOR)
    var enemy = createCharacter("Мерлин", CharacterClass.MAGE)

    // Атака (обычная и критическая)
    val hit = attack(enemy, hero)                 // 18 - 8 = 10 урона
    hero = hit.target
    println("${hit.damageDealt} урона, HP героя: ${hero.hp}")   // 10 урона, HP героя: 110

    val crit = attack(hero, enemy, critical = true)  // 24 - 3 = 21 урона
    enemy = crit.target

    // Лечение предметом
    val potion = Item("Малое зелье", ItemType.HEALING_POTION, 40)
    hero = useItem(hero, potion)                  // HP не выше максимума (120)

    // Опыт и уровни
    hero = gainExperience(hero, 150)              // повышение уровня, остаток опыта переносится
    println("Уровень: ${hero.level}, опыт: ${hero.experience}")

    println(isDead(enemy))                        // false
}
```

Обработка ошибок:

```kotlin
try {
    createCharacter("   ", CharacterClass.MAGE)       // IllegalArgumentException
} catch (e: IllegalArgumentException) {
    println(e.message)
}

val dead = createCharacter("Тень", CharacterClass.ARCHER).copy(hp = 0)
heal(dead, 10)                                        // IllegalStateException
```

## Тестирование

В `src/test` находится базовый набор модульных тестов разработчика (JUnit 5).
Тестировщику рекомендуется самостоятельно проверить поведение каждой функции по
KDoc-описанию и правилам из этого README, уделив внимание граничным значениям.
