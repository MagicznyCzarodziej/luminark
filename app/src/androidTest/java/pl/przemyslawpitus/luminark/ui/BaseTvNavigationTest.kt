package pl.przemyslawpitus.luminark.ui

import android.Manifest
import android.view.KeyEvent
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import pl.przemyslawpitus.luminark.MainActivity
import org.junit.Before
import org.junit.Rule

/**
 * Shared setup, rules, and D-pad helpers for all TV focus-navigation tests.
 *
 * Mock-data symbols list: # A B C D E F G H I J L M N O P S T U W Z  (21 total)
 */
abstract class BaseTvNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
    )

    @get:Rule(order = 2)
    val rule = createAndroidComposeRule<MainActivity>()

    protected val instrumentation by lazy { InstrumentationRegistry.getInstrumentation() }

    protected val TOTAL_SYMBOLS = 21

    private val symbols = listOf('#') + "ABCDEFGHIJLMNOPSTUWZ".toList()

    // ── D-pad input ──────────────────────────────────────────────────

    protected fun pressKey(keyCode: Int, times: Int = 1) {
        repeat(times) {
            instrumentation.sendKeyDownUpSync(keyCode)
            rule.waitForIdle()
        }
    }

    protected fun dpadDown(n: Int = 1) = pressKey(KeyEvent.KEYCODE_DPAD_DOWN, n)
    protected fun dpadUp(n: Int = 1) = pressKey(KeyEvent.KEYCODE_DPAD_UP, n)
    protected fun dpadLeft(n: Int = 1) = pressKey(KeyEvent.KEYCODE_DPAD_LEFT, n)
    protected fun dpadRight(n: Int = 1) = pressKey(KeyEvent.KEYCODE_DPAD_RIGHT, n)
    protected fun dpadCenter() = pressKey(KeyEvent.KEYCODE_DPAD_CENTER)

    // ── Assertions ───────────────────────────────────────────────────

    /** Wait for compose + async focus coroutines to settle. */
    protected fun settle() {
        rule.waitForIdle()
        rule.mainClock.advanceTimeBy(1_500)
        rule.waitForIdle()
    }

    /**
     * Poll until the node with [tag] exists AND has focus.
     * Catches IllegalStateException for the case when compose is not yet ready.
     */
    protected fun assertFocused(tag: String, timeoutMs: Long = 8_000) {
        rule.waitUntil(timeoutMs) {
            try {
                val nodes = rule.onAllNodesWithTag(tag).fetchSemanticsNodes()
                nodes.isNotEmpty() && isFocused(nodes)
            } catch (_: IllegalStateException) {
                false
            }
        }
    }

    /** Poll until the letter node is marked selected. */
    protected fun assertLetterSelected(index: Int) {
        rule.waitUntil(5_000) {
            try {
                val nodes = rule.onAllNodesWithTag(TestTags.letterTag(index))
                    .fetchSemanticsNodes()
                nodes.isNotEmpty() &&
                    SemanticsProperties.Selected in nodes[0].config &&
                    nodes[0].config[SemanticsProperties.Selected]
            } catch (_: IllegalStateException) {
                false
            }
        }
    }

    // ── Focus helpers ────────────────────────────────────────────────

    /** Check if the first semantic node in [nodes] has the Focused property set. */
    protected fun isFocused(nodes: List<SemanticsNode>): Boolean {
        return nodes.isNotEmpty() &&
            SemanticsProperties.Focused in nodes[0].config &&
            nodes[0].config[SemanticsProperties.Focused]
    }

    /** Scroll the entries list to [index] and request focus on that item. */
    protected fun focusEntry(index: Int) {
        rule.onNodeWithTag(TestTags.ENTRIES_LIST).performScrollToIndex(index)
        settle()
        rule.onNodeWithTag(TestTags.entryItem(index)).requestFocus()
        settle()
    }

    /** Wait until any entry in [0, count) has focus. Returns the focused index. */
    protected fun waitForAnyEntryFocused(count: Int = 66, timeoutMs: Long = 8_000): Int {
        rule.waitUntil(timeoutMs) {
            try {
                (0 until count).any { i ->
                    isFocused(rule.onAllNodesWithTag(TestTags.entryItem(i)).fetchSemanticsNodes())
                }
            } catch (_: IllegalStateException) { false }
        }
        return (0 until count).first { i ->
            try {
                isFocused(rule.onAllNodesWithTag(TestTags.entryItem(i)).fetchSemanticsNodes())
            } catch (_: IllegalStateException) { false }
        }
    }

    /** Wait until any letter in [0, count) has focus. */
    protected fun waitForAnyLetterFocused(count: Int = TOTAL_SYMBOLS, timeoutMs: Long = 8_000) {
        rule.waitUntil(timeoutMs) {
            try {
                (0 until count).any { i ->
                    isFocused(rule.onAllNodesWithTag(TestTags.letterTag(i)).fetchSemanticsNodes())
                }
            } catch (_: IllegalStateException) { false }
        }
    }

    // ── Navigation shortcuts ─────────────────────────────────────────

    /** Navigate from entry list to letters by pressing LEFT, then wait for focus. */
    protected fun navigateToLetters(expectedLetterIndex: Int) {
        dpadLeft()
        assertFocused(TestTags.letterTag(expectedLetterIndex))
    }

    /**
     * Open sidebar by explicitly requesting focus on the first item.
     *
     * D-pad Right from entries does NOT spatially navigate to the sidebar because
     * the sidebar overlay shares the same x-region as the entries list.
     */
    protected fun openSidebar() {
        rule.onNodeWithTag(TestTags.sidebarItem(0)).requestFocus()
        settle()
        assertFocused(TestTags.sidebarItem(0))
    }

    /** Find the symbol index for a letter in the alphabet column. */
    protected fun symbolIndex(letter: Char): Int = symbols.indexOf(letter)

    // ── Lifecycle ────────────────────────────────────────────────────

    @Before
    fun ensureInitialFocus() {
        hiltRule.inject()

        rule.waitUntil(10_000) {
            try {
                rule.onAllNodesWithTag(TestTags.entryItem(0))
                    .fetchSemanticsNodes().isNotEmpty()
            } catch (_: IllegalStateException) {
                false
            }
        }
        settle()

        // A key event is needed to establish window input focus after the
        // permission dialog or splash screen.
        dpadDown()
        settle()

        rule.onNodeWithTag(TestTags.entryItem(0)).requestFocus()
        settle()
        assertFocused(TestTags.entryItem(0))
    }
}
