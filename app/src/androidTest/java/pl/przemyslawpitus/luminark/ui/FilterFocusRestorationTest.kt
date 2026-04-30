package pl.przemyslawpitus.luminark.ui

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.requestFocus
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Regression tests for focus restoration after applying filters.
 *
 * Covers two bugs fixed together:
 * 1. DisposableEffect keyed on (index, state) — after filtering, EntriesListState is
 *    recreated and focusRequesters must re-register on the new state instance.
 * 2. lastFocusedIndex clamped to 0 when it exceeds the new (smaller) entries list.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FilterFocusRestorationTest : BaseTvNavigationTest() {

    // ── Bug 1: DisposableEffect re-registration ─────────────────────

    @Test
    fun `after applying top bar filter pressing Down focuses an entry`() {
        // Apply "Films" filter from the top bar
        rule.onNodeWithTag(TestTags.topBarButton(1)).requestFocus()
        settle()
        assertFocused(TestTags.topBarButton(1))
        dpadCenter() // triggers filterEntries(FILMS)
        settle()

        // Down from the top bar should land on an entry in the filtered list
        dpadDown()
        waitForAnyEntryFocused()
    }

    // ── Bug 2: stale lastFocusedIndex clamping ──────────────────────

    @Test
    fun `stale focus index is clamped after filter reduces entry count`() {
        // Navigate to entry 40 — this sets lastFocusedIndex = 40
        focusEntry(40)
        assertFocused(TestTags.entryItem(40))

        // Apply "Series" filter (~14-18 entries, well below index 40)
        rule.onNodeWithTag(TestTags.topBarButton(2)).requestFocus()
        settle()
        assertFocused(TestTags.topBarButton(2))
        dpadCenter() // triggers filterEntries(SERIES)
        settle()

        // Down from top bar should focus entry 0 (clamped from 40)
        dpadDown()
        val focused = waitForAnyEntryFocused()
        assertEquals("lastFocusedIndex should clamp to 0", 0, focused)
    }

    // ── Combined / round-trip scenarios ──────────────────────────────

    @Test
    fun `after top bar filter sidebar exit still focuses an entry`() {
        // Apply "Films" filter (entries list changes, new state created)
        rule.onNodeWithTag(TestTags.topBarButton(1)).requestFocus()
        settle()
        dpadCenter()
        settle()

        // Open sidebar and immediately exit
        openSidebar()
        dpadLeft()
        waitForAnyEntryFocused()
    }

    @Test
    fun `switching filters multiple times preserves focus navigation`() {
        // Films filter
        rule.onNodeWithTag(TestTags.topBarButton(1)).requestFocus()
        settle()
        dpadCenter()
        settle()
        dpadDown()
        waitForAnyEntryFocused()

        // Series filter
        rule.onNodeWithTag(TestTags.topBarButton(2)).requestFocus()
        settle()
        dpadCenter()
        settle()
        dpadDown()
        waitForAnyEntryFocused()

        // Back to All
        rule.onNodeWithTag(TestTags.topBarButton(0)).requestFocus()
        settle()
        dpadCenter()
        settle()
        dpadDown()
        waitForAnyEntryFocused()
    }
}
