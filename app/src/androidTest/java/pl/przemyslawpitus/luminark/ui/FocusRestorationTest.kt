package pl.przemyslawpitus.luminark.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FocusRestorationTest : BaseTvNavigationTest() {

    @Test
    fun `closing the sidebar returns focus to the same entry`() {
        dpadDown(15)
        assertFocused(TestTags.entryItem(15))

        openSidebar()

        dpadLeft()
        assertFocused(TestTags.entryItem(15))
    }

    @Test
    fun `letters round-trip returns focus to the same entry`() {
        focusEntry(8) // "Blade Runner 2049" → sortName starts with 'B'
        assertFocused(TestTags.entryItem(8))

        navigateToLetters(symbolIndex('B'))

        dpadRight() // back without selecting
        assertFocused(TestTags.entryItem(8))
    }

    @Test
    fun `selecting a letter overrides the last focused entry`() {
        assertFocused(TestTags.entryItem(0))

        navigateToLetters(1) // letter A
        val gIdx = symbolIndex('G')
        dpadDown(gIdx - 1)
        assertFocused(TestTags.letterTag(gIdx))
        dpadCenter()

        val focusedEntry = waitForAnyEntryFocused()

        // After sidebar round-trip, focus should return to the G entry
        openSidebar()
        dpadLeft()
        assertFocused(TestTags.entryItem(focusedEntry))
    }
}
