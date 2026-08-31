# Checkers — этап 4: добавление ИИ для режима «один игрок против компьютера»

## Цель этапа

Добавить в проект полноценный режим игры **человек против ИИ**, где:

- человек играет за одну сторону;
- ИИ играет за другую сторону;
- ИИ использует уже реализованную игровую логику из этапа 3;
- сложность ИИ можно менять **пока только в коде**;
- ИИ не ломает Android/desktop-совместимость;
- расчёт хода ИИ не блокирует rendering/input;
- поведение покрыто тестами.

Этот этап должен опираться на уже выделенный domain-слой правил шашек. ИИ не должен напрямую работать с libGDX-объектами, `TextureRegion`, `Board`, `Cell`, `Checker` из rendering-слоя и координатами экрана.

---

## Предполагаемое состояние после этапа 3

Перед началом этапа 4 желательно, чтобы уже существовали:

- чистая модель доски;
- модель шашки;
- модель игрока/стороны;
- валидатор легальных ходов;
- генератор всех доступных ходов;
- применение хода к состоянию игры;
- поддержка обязательного взятия;
- поддержка серии взятий;
- поддержка дамок;
- определение победителя;
- тесты на правила.

Если чего-то из этого ещё нет, сначала завершить этап 3, иначе ИИ будет написан поверх нестабильной логики и потом его придётся переписывать.

---

## Главный принцип архитектуры

ИИ должен быть обычным потребителем domain-логики.

Правильная зависимость:

```text
AI
 ↓
GameState / MoveGenerator / MoveValidator / GameRules
```

Неправильная зависимость:

```text
AI
 ↓
Board / Cell / SpriteBatch / Texture / InputProcessor
```

ИИ должен видеть только:

- текущее состояние игры;
- сторону, за которую он играет;
- список легальных ходов;
- результат применения хода;
- признак конца партии.

---

## Предлагаемая структура пакетов

Примерная структура после добавления ИИ:

```text
core/
└── src/main/kotlin/com/infeez/simple/
    ├── domain/
    │   ├── model/
    │   │   ├── GameState.kt
    │   │   ├── PlayerSide.kt
    │   │   ├── Piece.kt
    │   │   ├── PieceType.kt
    │   │   ├── BoardPosition.kt
    │   │   ├── Move.kt
    │   │   └── GameResult.kt
    │   ├── rules/
    │   │   ├── CheckersRules.kt
    │   │   ├── MoveGenerator.kt
    │   │   ├── MoveValidator.kt
    │   │   └── MoveApplier.kt
    │   ├── game/
    │   │   ├── GameController.kt
    │   │   ├── GameMode.kt
    │   │   └── TurnState.kt
    │   └── ai/
    │       ├── CheckersAi.kt
    │       ├── AiMoveRequest.kt
    │       ├── AiMoveResult.kt
    │       ├── AiDifficulty.kt
    │       ├── AiConfig.kt
    │       ├── RandomAi.kt
    │       ├── GreedyAi.kt
    │       ├── MinimaxAi.kt
    │       ├── BoardEvaluator.kt
    │       ├── MoveOrdering.kt
    │       └── SearchLimits.kt
    └── presentation/
        ├── GameScreen.kt
        ├── BoardRenderer.kt
        └── BoardInputController.kt
```

Если текущий проект пока не разделён так строго, не обязательно делать всё идеально сразу, но AI-код всё равно должен быть вынесен отдельно от rendering/input.

---

## Режимы игры

Добавить enum режима игры:

```kotlin
enum class GameMode {
    HUMAN_VS_HUMAN,
    HUMAN_VS_AI
}
```

На этом этапе UI-переключатель не нужен. Режим можно задать в коде, например в `GameConfig`.

Пример:

```kotlin
data class GameConfig(
    val gameMode: GameMode = GameMode.HUMAN_VS_AI,
    val humanSide: PlayerSide = PlayerSide.WHITE,
    val aiDifficulty: AiDifficulty = AiDifficulty.NORMAL,
)
```

Рекомендуемое поведение по умолчанию:

```kotlin
gameMode = HUMAN_VS_AI
humanSide = WHITE
aiSide = BLACK
aiDifficulty = NORMAL
```

---

## Сложность ИИ

Добавить enum сложности:

```kotlin
enum class AiDifficulty {
    RANDOM,
    EASY,
    NORMAL,
    HARD,
    EXPERT
}
```

На этом этапе сложность меняется только в коде.

Пример:

```kotlin
val config = GameConfig(
    gameMode = GameMode.HUMAN_VS_AI,
    humanSide = PlayerSide.WHITE,
    aiDifficulty = AiDifficulty.NORMAL,
)
```

---

## Рекомендуемая таблица сложности

| Сложность | Поведение | Глубина поиска | Особенности |
|---|---|---:|---|
| `RANDOM` | случайный легальный ход | 0 | нужен для отладки |
| `EASY` | случайный ход с небольшим приоритетом взятий | 0-1 | иногда ошибается |
| `NORMAL` | minimax с простой оценкой | 3 | базовый нормальный ИИ |
| `HARD` | minimax + alpha-beta + сортировка ходов | 5 | заметно сильнее |
| `EXPERT` | глубже поиск + улучшенная оценка | 7 или time limit | может быть тяжёлым для Android |

Для Android не стоит сразу ставить слишком большую глубину. Лучше начать с `NORMAL` и `HARD`, а `EXPERT` держать экспериментальным.

---

## Интерфейс ИИ

Создать общий интерфейс:

```kotlin
interface CheckersAi {
    fun chooseMove(request: AiMoveRequest): AiMoveResult
}
```

Запрос:

```kotlin
data class AiMoveRequest(
    val gameState: GameState,
    val aiSide: PlayerSide,
    val difficulty: AiDifficulty,
    val searchLimits: SearchLimits,
)
```

Результат:

```kotlin
data class AiMoveResult(
    val move: Move?,
    val score: Int? = null,
    val searchedNodes: Int = 0,
    val depth: Int = 0,
)
```

`move` может быть `null`, если ходов нет. Это должно трактоваться как поражение или конец игры по правилам.

---

## Конфиг лимитов поиска

Добавить отдельный конфиг лимитов:

```kotlin
data class SearchLimits(
    val maxDepth: Int,
    val maxTimeMillis: Long? = null,
    val randomizeEqualMoves: Boolean = true,
)
```

Пример маппинга сложности:

```kotlin
object AiConfigFactory {

    fun create(difficulty: AiDifficulty): SearchLimits {
        return when (difficulty) {
            AiDifficulty.RANDOM -> SearchLimits(maxDepth = 0)
            AiDifficulty.EASY -> SearchLimits(maxDepth = 1)
            AiDifficulty.NORMAL -> SearchLimits(maxDepth = 3, maxTimeMillis = 500)
            AiDifficulty.HARD -> SearchLimits(maxDepth = 5, maxTimeMillis = 1_500)
            AiDifficulty.EXPERT -> SearchLimits(maxDepth = 7, maxTimeMillis = 3_000)
        }
    }
}
```

Для desktop можно позже разрешить большие лимиты, для Android — держать осторожные значения.

---

## Этап 4.1 — подготовка domain API для ИИ

Проверить, что в domain-слое есть стабильные методы:

```kotlin
interface CheckersRules {
    fun getLegalMoves(state: GameState, side: PlayerSide): List<Move>
    fun applyMove(state: GameState, move: Move): GameState
    fun getGameResult(state: GameState): GameResult?
    fun getCurrentPlayer(state: GameState): PlayerSide
}
```

Требования:

- `getLegalMoves(...)` возвращает только реально допустимые ходы;
- если есть обязательное взятие, обычные ходы не возвращаются;
- серия взятий должна быть представлена корректно;
- `applyMove(...)` не мутирует исходный `GameState`, если проект выбрал immutable-подход;
- после применения хода корректно меняется текущий игрок;
- после серии взятий ход переходит к другому игроку только когда серия завершена.

---

## Этап 4.2 — добавить самый простой Random AI

Сначала реализовать `RandomAi`.

Задача:

- получить список легальных ходов;
- выбрать случайный ход;
- вернуть его.

Пример поведения:

```kotlin
class RandomAi(
    private val rules: CheckersRules,
    private val random: Random = Random.Default,
) : CheckersAi {

    override fun chooseMove(request: AiMoveRequest): AiMoveResult {
        val moves = rules.getLegalMoves(request.gameState, request.aiSide)

        if (moves.isEmpty()) {
            return AiMoveResult(move = null)
        }

        return AiMoveResult(
            move = moves.random(random),
            depth = 0,
        )
    }
}
```

Зачем нужен `RandomAi`:

- быстро проверить режим `HUMAN_VS_AI`;
- проверить смену ходов;
- проверить, что ИИ не пытается ходить за человека;
- проверить Android touch-flow;
- иметь fallback, если minimax сломается.

---

## Этап 4.3 — добавить Greedy AI

Добавить `GreedyAi`, который выбирает ход по простой эвристике без глубокого поиска.

Приоритеты:

1. ход со взятием;
2. ход с максимальным количеством съеденных шашек;
3. ход, превращающий шашку в дамку;
4. ход, который не подставляет шашку под немедленное взятие;
5. случайный из лучших равных вариантов.

Пример scoring:

```text
+100 за каждую съеденную обычную шашку
+250 за каждую съеденную дамку
+80 за превращение в дамку
-70 если после хода шашку можно сразу съесть
+10 за продвижение обычной шашки вперёд
```

Этот AI можно использовать для `EASY`.

---

## Этап 4.4 — добавить BoardEvaluator

Создать класс оценки позиции:

```kotlin
interface BoardEvaluator {
    fun evaluate(state: GameState, side: PlayerSide): Int
}
```

Оценка должна быть с точки зрения `side`.

Пример базовой оценки:

```text
+100 за свою обычную шашку
+300 за свою дамку
-100 за чужую обычную шашку
-300 за чужую дамку
+5..30 за продвижение обычной шашки к дамке
+10 за контроль центра
+20 за мобильность, если доступных ходов больше
-50 если своя шашка под боем
+10000 за выигранную позицию
-10000 за проигранную позицию
```

Важно:

- оценка должна быть детерминированной;
- не использовать rendering-координаты;
- не завязываться на цвет текстур;
- покрыть тестами хотя бы материальное преимущество.

---

## Этап 4.5 — добавить Minimax AI

Добавить `MinimaxAi`.

Минимальный вариант:

- depth-limited minimax;
- оценка листьев через `BoardEvaluator`;
- генерация ходов через `CheckersRules`;
- корректная обработка конца игры.

Сигнатура:

```kotlin
class MinimaxAi(
    private val rules: CheckersRules,
    private val evaluator: BoardEvaluator,
) : CheckersAi
```

Алгоритм:

```text
chooseMove:
  получить все легальные ходы за aiSide
  если ходов нет — вернуть null
  для каждого хода:
    nextState = applyMove(state, move)
    score = minimax(nextState, depth - 1, opponentSide, aiSide)
  выбрать ход с максимальным score
```

---

## Этап 4.6 — добавить alpha-beta pruning

После простого minimax добавить alpha-beta pruning.

Зачем:

- меньше перебираемых позиций;
- быстрее работа на Android;
- можно поднять глубину поиска.

Логика:

```text
alpha = лучшая уже найденная оценка для maximizing side
beta = лучшая уже найденная оценка для minimizing side

если beta <= alpha:
  прекратить перебор ветки
```

Важно:

- сначала добиться корректности minimax;
- потом добавить alpha-beta;
- после оптимизации тесты должны остаться зелёными.

---

## Этап 4.7 — сортировка ходов

Добавить `MoveOrdering`.

Цель:

- сначала проверять сильные ходы;
- улучшить эффективность alpha-beta.

Приоритет сортировки:

1. взятия;
2. серии взятий с большим количеством жертв;
3. взятие дамки;
4. превращение в дамку;
5. ходы дамкой;
6. продвижение к дамке;
7. остальные ходы.

Интерфейс:

```kotlin
interface MoveOrdering {
    fun orderMoves(state: GameState, side: PlayerSide, moves: List<Move>): List<Move>
}
```

---

## Этап 4.8 — поддержка time limit

Для Android лучше иметь ограничение не только по глубине, но и по времени.

Добавить в поиск проверку:

```kotlin
if (timeLimitExceeded()) {
    return evaluator.evaluate(state, aiSide)
}
```

Поведение:

- если время вышло на глубоком уровне, вернуть текущую оценку;
- если время вышло на верхнем уровне, вернуть лучший уже найденный ход;
- если лучший ход ещё не найден, вернуть первый легальный ход или fallback `RandomAi`.

Не делать бесконечные расчёты на UI thread.

---

## Этап 4.9 — выбор реализации AI по сложности

Добавить фабрику:

```kotlin
class CheckersAiFactory(
    private val rules: CheckersRules,
    private val evaluator: BoardEvaluator,
) {

    fun create(difficulty: AiDifficulty): CheckersAi {
        return when (difficulty) {
            AiDifficulty.RANDOM -> RandomAi(rules)
            AiDifficulty.EASY -> GreedyAi(rules)
            AiDifficulty.NORMAL -> MinimaxAi(rules, evaluator)
            AiDifficulty.HARD -> MinimaxAi(rules, evaluator)
            AiDifficulty.EXPERT -> MinimaxAi(rules, evaluator)
        }
    }
}
```

Глубина и лимиты не обязательно должны быть внутри реализации ИИ. Лучше передавать их через `AiMoveRequest`.

---

## Этап 4.10 — интеграция в GameController

Добавить `GameController`, который управляет партией:

```kotlin
class GameController(
    private val rules: CheckersRules,
    private val ai: CheckersAi?,
    private val config: GameConfig,
) {

    var state: GameState = GameState.initial()
        private set

    fun isHumanTurn(): Boolean
    fun isAiTurn(): Boolean
    fun makeHumanMove(move: Move): MoveResult
    fun makeAiMove(): AiMoveResult
}
```

Правила:

- человек может ходить только в свой ход;
- ИИ может ходить только в свой ход;
- если режим `HUMAN_VS_HUMAN`, ИИ не вызывается;
- если игра закончилась, новые ходы запрещены;
- после хода человека автоматически наступает ход ИИ, если игра не завершена;
- после хода ИИ управление возвращается человеку.

---

## Этап 4.11 — не блокировать rendering/input

На Android нельзя считать сложный ход ИИ прямо в render/input callback, если поиск может занять сотни миллисекунд или секунды.

Минимальный подход:

```text
Human move completed
 ↓
Set turn state = AI_THINKING
 ↓
Run AI calculation async/background
 ↓
When result ready, apply move on render thread
 ↓
Set turn state = HUMAN_TURN
```

В libGDX для возврата результата в главный поток использовать:

```kotlin
Gdx.app.postRunnable {
    controller.applyAiMove(result.move)
}
```

Варианты реализации:

### Вариант 1 — Kotlin coroutines

Если в проект уже добавлены coroutines:

```kotlin
scope.launch(Dispatchers.Default) {
    val result = ai.chooseMove(request)

    Gdx.app.postRunnable {
        applyAiResult(result)
    }
}
```

### Вариант 2 — ExecutorService

Если не хочется тащить coroutines:

```kotlin
private val aiExecutor = Executors.newSingleThreadExecutor()
```

И запускать расчёт через executor.

Для простоты Codex может выбрать `ExecutorService`, если в проекте пока нет coroutine-инфраструктуры.

---

## Этап 4.12 — состояние хода

Добавить состояние игрового flow:

```kotlin
enum class TurnState {
    HUMAN_TURN,
    AI_THINKING,
    ANIMATING_MOVE,
    GAME_OVER
}
```

Поведение input:

- при `HUMAN_TURN` разрешить drag-and-drop;
- при `AI_THINKING` запретить drag-and-drop;
- при `ANIMATING_MOVE` запретить drag-and-drop;
- при `GAME_OVER` запретить drag-and-drop или разрешить только restart.

---

## Этап 4.13 — визуальная интеграция

Минимально:

- после хода человека шашка перемещается;
- затем ИИ делает ход;
- ход ИИ отображается на доске;
- при расчёте ИИ нельзя двигать свои шашки;
- при завершении игры показывать победителя хотя бы через log/overlay.

Желательно:

- добавить небольшую задержку перед ходом ИИ, например 200-500 мс;
- подсветить последнюю сделанную ИИ клетку/ход;
- добавить простой статус:

```text
Ваш ход
ИИ думает...
Победа белых
Победа чёрных
Ничья
```

На этом этапе полноценный UI сложности не нужен.

---

## Этап 4.14 — управление стороной игрока в коде

Добавить возможность в коде выбрать сторону человека:

```kotlin
val config = GameConfig(
    gameMode = GameMode.HUMAN_VS_AI,
    humanSide = PlayerSide.WHITE,
    aiDifficulty = AiDifficulty.NORMAL,
)
```

Если человек играет за `BLACK`:

- ИИ должен сделать первый ход за `WHITE`;
- доска может пока не переворачиваться;
- input должен позволять человеку двигать только чёрные шашки;
- ИИ должен двигать только белые шашки.

Опционально для будущего:

```kotlin
val rotateBoardForBlackPlayer: Boolean = false
```

---

## Этап 4.15 — защита от ошибок

Добавить защиту:

- ИИ не должен ходить, если игра завершена;
- ИИ не должен ходить, если сейчас ход человека;
- человек не должен ходить за сторону ИИ;
- нельзя запустить два параллельных расчёта ИИ;
- если ИИ вернул нелегальный ход, ход не применять, записать ошибку в log;
- если ИИ не нашёл ход, завершить игру по правилам;
- если расчёт ИИ упал с exception, использовать fallback `RandomAi`.

Пример fallback:

```text
try MinimaxAi
catch exception
  log error
  use RandomAi
```

---

## Этап 4.16 — тесты Random/Greedy AI

Добавить тесты:

- `RandomAi` возвращает только легальный ход;
- `RandomAi` возвращает `null`, если ходов нет;
- `GreedyAi` предпочитает взятие обычному ходу;
- `GreedyAi` предпочитает большее количество взятий;
- `GreedyAi` предпочитает превращение в дамку при прочих равных;
- AI не мутирует исходное состояние, если `GameState` immutable.

---

## Этап 4.17 — тесты BoardEvaluator

Добавить тесты:

- равная стартовая позиция имеет оценку около 0;
- лишняя шашка даёт преимущество;
- дамка оценивается выше обычной шашки;
- выигранная позиция получает высокий score;
- проигранная позиция получает низкий score;
- оценка с точки зрения белых противоположна оценке с точки зрения чёрных.

Пример:

```text
evaluate(state, WHITE) == -evaluate(state, BLACK)
```

Допускается нестрогое равенство, если оценка включает асимметричные факторы вроде продвижения.

---

## Этап 4.18 — тесты Minimax AI

Добавить тесты:

- выбирает немедленное выигрышное взятие;
- не выбирает ход, после которого сразу проигрывает, если есть безопасная альтернатива;
- при depth = 1 ведёт себя близко к Greedy;
- при depth = 3 видит простую ловушку;
- не падает, если ходов нет;
- соблюдает обязательное взятие;
- не делает ход за неправильную сторону.

---

## Этап 4.19 — тесты GameController

Добавить тесты:

- в режиме `HUMAN_VS_AI` после хода человека наступает ход ИИ;
- человек не может ходить во время `AI_THINKING`;
- ИИ не вызывается в режиме `HUMAN_VS_HUMAN`;
- если человек играет за чёрных, ИИ делает первый ход;
- после game over ИИ не запускается;
- нелегальный ход человека отклоняется;
- легальный ход человека применяется;
- ход ИИ применяется только если он легальный.

---

## Этап 4.20 — Android/performance проверки

Проверить на Android:

- приложение не фризится на ходе ИИ;
- touch input блокируется во время расчёта;
- после поворота экрана или сворачивания не запускается несколько ИИ-расчётов;
- при `pause/resume` состояние игры не ломается;
- при `dispose` executor/coroutine scope корректно завершается;
- при `HARD/EXPERT` не возникает ANR;
- `NORMAL` работает комфортно на средних устройствах.

---

## Этап 4.21 — логирование для отладки

Добавить debug-лог ИИ:

```text
AI difficulty: NORMAL
AI side: BLACK
AI depth: 3
Legal moves: 7
Chosen move: c5-d4
Score: 120
Searched nodes: 1540
Time: 84 ms
```

Логи должны быть полезными, но не засорять релиз.

Можно сделать флаг:

```kotlin
data class AiDebugConfig(
    val enabled: Boolean = true,
    val logEvaluations: Boolean = false,
)
```

---

## Этап 4.22 — настройка сложности в коде

Добавить единое место, где меняется сложность.

Например:

```kotlin
object DefaultGameConfig {

    val value = GameConfig(
        gameMode = GameMode.HUMAN_VS_AI,
        humanSide = PlayerSide.WHITE,
        aiDifficulty = AiDifficulty.NORMAL,
    )
}
```

И явно написать комментарий:

```kotlin
// Change AI difficulty here while there is no settings UI.
```

Не размазывать сложность по разным классам.

---

## Этап 4.23 — документация для разработчика

Добавить файл:

```text
docs/ai.md
```

В нём описать:

- какие есть уровни сложности;
- где менять сложность;
- как выбрать сторону игрока;
- как работает Random/Greedy/Minimax;
- какие лимиты стоят на Android;
- какие тесты запускать после изменения AI;
- какие параметры можно тюнить.

---

## Этап 4.24 — ручной чеклист

Проверить вручную:

```text
[ ] Desktop запускается.
[ ] Android запускается.
[ ] По умолчанию включён HUMAN_VS_AI.
[ ] Человек играет только своей стороной.
[ ] ИИ играет только своей стороной.
[ ] После хода человека ИИ делает ответный ход.
[ ] Во время хода ИИ drag-and-drop заблокирован.
[ ] ИИ не делает нелегальные ходы.
[ ] ИИ соблюдает обязательные взятия.
[ ] ИИ выполняет серию взятий, если она требуется правилами.
[ ] ИИ умеет ходить дамкой.
[ ] ИИ умеет бить дамкой.
[ ] Игра завершается победой, если у стороны нет ходов/шашек.
[ ] При RANDOM ходы выглядят случайными.
[ ] При EASY ИИ часто выбирает взятия.
[ ] При NORMAL ИИ играет заметно осмысленнее RANDOM.
[ ] При HARD приложение не зависает на Android.
[ ] Сложность меняется в одном месте в коде.
```

---

## Этап 4.25 — критерии готовности

Этап считается завершённым, если:

- есть режим `HUMAN_VS_AI`;
- человек может сыграть полную партию против ИИ;
- ИИ выбирает только легальные ходы;
- ИИ соблюдает правила шашек из этапа 3;
- сложность меняется через enum/config в коде;
- есть минимум 3 рабочих уровня сложности:
  - `RANDOM`;
  - `EASY`;
  - `NORMAL`;
- `HARD` может быть добавлен как улучшенный уровень, если производительность позволяет;
- Android не зависает во время расчёта хода;
- есть fallback на случай ошибки AI;
- логика ИИ покрыта unit-тестами;
- integration flow покрыт тестами `GameController`;
- есть `docs/ai.md`.

---

## Рекомендуемый порядок выполнения для Codex

```text
1. Проверить, что domain-логика из этапа 3 стабильна.
2. Добавить GameMode, GameConfig, AiDifficulty.
3. Добавить CheckersAi, AiMoveRequest, AiMoveResult, SearchLimits.
4. Реализовать RandomAi.
5. Подключить HUMAN_VS_AI через GameController.
6. Проверить полный flow человек → ИИ → человек.
7. Добавить GreedyAi.
8. Добавить BoardEvaluator.
9. Добавить MinimaxAi без alpha-beta.
10. Покрыть Minimax тестами.
11. Добавить alpha-beta pruning.
12. Добавить MoveOrdering.
13. Добавить time limit.
14. Добавить async-запуск ИИ для Android.
15. Добавить защиту от двойного запуска ИИ.
16. Добавить debug-логирование.
17. Добавить docs/ai.md.
18. Прогнать desktop/android.
19. Прогнать unit-тесты.
20. Провести ручную проверку полной партии.
```

---

## Важное ограничение этапа

На этом этапе не делать:

- UI-экран выбора сложности;
- online multiplayer;
- сохранение партий;
- replay ходов;
- undo/redo;
- обучение нейросети;
- внешние AI-сервисы;
- красивую анимацию ходов;
- полноценное меню настроек.

Цель этапа — рабочий локальный ИИ, который позволяет играть одному игроку против компьютера.

---

## Возможные улучшения после этапа

После завершения этого этапа можно отдельно запланировать:

1. UI выбора сложности.
2. UI выбора стороны игрока.
3. Подсказку лучшего хода.
4. Подсветку доступных ходов.
5. Undo последнего хода.
6. Историю ходов.
7. Сохранение партии.
8. Разные правила шашек: русские, английские, international draughts.
9. Более сильный AI:
   - iterative deepening;
   - transposition table;
   - killer moves;
   - quiescence search;
   - endgame tablebase.
10. Настройки производительности отдельно для Android и desktop.

---

## Минимальный MVP этапа

Если нужно сделать быстро, минимальный MVP такой:

```text
[ ] GameMode.HUMAN_VS_AI
[ ] GameConfig с humanSide и aiDifficulty
[ ] AiDifficulty.RANDOM / EASY / NORMAL
[ ] RandomAi
[ ] GreedyAi
[ ] Simple Minimax depth 3
[ ] GameController вызывает ИИ после хода человека
[ ] ИИ работает не на UI thread
[ ] Человек не может ходить во время AI_THINKING
[ ] Сложность меняется в одном месте в коде
[ ] Unit-тесты на AI
```

Это уже даст полноценную базовую игру «один игрок против ИИ».
