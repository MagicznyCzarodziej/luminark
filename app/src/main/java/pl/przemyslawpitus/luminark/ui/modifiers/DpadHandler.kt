package pl.przemyslawpitus.luminark.ui.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

/**
 * What to do when a D-pad direction is pressed.
 *
 * - [Block]     — always consume the event, preventing focus movement.
 * - [BlockWhen] — consume only when the predicate returns true (evaluated per key press).
 * - [Action]    — run the handler, then consume the event.
 */
sealed interface DpadAction {
    data object Block : DpadAction
    class BlockWhen(val predicate: () -> Boolean) : DpadAction
    class Action(val handler: () -> Unit) : DpadAction
}

/** Always prevent focus from moving in this direction. */
fun block(): DpadAction = DpadAction.Block

/** Prevent focus from moving when [predicate] returns true (evaluated on each key press). */
fun blockWhen(predicate: () -> Boolean): DpadAction = DpadAction.BlockWhen(predicate)

/** Run [handler] and consume the event, preventing default focus navigation. */
fun action(handler: () -> Unit): DpadAction = DpadAction.Action(handler)

/**
 * Declarative D-pad navigation handler.
 *
 * Assign a [DpadAction] to each direction you want to control.
 * Unset directions (null) pass through to Compose's default focus system.
 *
 * ```
 * Modifier.dpadHandler(
 *     onUp = block(),
 *     onDown = blockWhen { focusedIndex == lastIndex },
 *     onRight = action { scrollAndFocusEntry(index) },
 * )
 * ```
 */
fun Modifier.dpadHandler(
    onUp: DpadAction? = null,
    onDown: DpadAction? = null,
    onLeft: DpadAction? = null,
    onRight: DpadAction? = null,
    onCenter: DpadAction? = null,
): Modifier = this.onPreviewKeyEvent { event ->
    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
    val dpadAction = when (event.key) {
        Key.DirectionUp -> onUp
        Key.DirectionDown -> onDown
        Key.DirectionLeft -> onLeft
        Key.DirectionRight -> onRight
        Key.DirectionCenter, Key.Enter -> onCenter
        else -> null
    }
    when (dpadAction) {
        null -> false
        is DpadAction.Block -> true
        is DpadAction.BlockWhen -> dpadAction.predicate()
        is DpadAction.Action -> { dpadAction.handler(); true }
    }
}
