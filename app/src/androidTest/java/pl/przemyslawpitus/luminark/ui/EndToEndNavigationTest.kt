package pl.przemyslawpitus.luminark.ui

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.requestFocus
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EndToEndNavigationTest : BaseTvNavigationTest() {

    @Test
    fun `full navigation flow across all screen regions`() {
        assertFocused(TestTags.entryItem(0))

        // Scroll entries
        dpadDown(10)
        assertFocused(TestTags.entryItem(10))

        // Sidebar round-trip
        openSidebar()
        assertFocused(TestTags.sidebarItem(0))
        dpadLeft()
        assertFocused(TestTags.entryItem(10))

        // Letters selector
        dpadLeft()
        waitForAnyLetterFocused()

        // Navigate to top, then to letter E
        dpadUp(TOTAL_SYMBOLS)
        settle()
        assertFocused(TestTags.letterTag(0))
        val eIdx = symbolIndex('E')
        dpadDown(eIdx)
        assertFocused(TestTags.letterTag(eIdx))
        dpadCenter()

        val eEntry = waitForAnyEntryFocused()
        assertLetterSelected(eIdx)

        // Top bar round-trip
        rule.onNodeWithTag(TestTags.topBarButton(0)).requestFocus()
        assertFocused(TestTags.topBarButton(0))
        dpadRight()
        assertFocused(TestTags.topBarButton(1))

        dpadDown()
        assertFocused(TestTags.entryItem(eEntry))
    }

    @Test
    fun `rapid letter selection updates entries correctly each time`() {
        navigateToLetters(1) // letter A

        // Select C
        val cIdx = symbolIndex('C')
        dpadDown(cIdx - 1)
        assertFocused(TestTags.letterTag(cIdx))
        dpadCenter()
        waitForAnyEntryFocused()
        assertLetterSelected(cIdx)

        // Select F
        dpadLeft()
        waitForAnyLetterFocused()
        dpadUp(TOTAL_SYMBOLS)
        assertFocused(TestTags.letterTag(0))
        val fIdx = symbolIndex('F')
        dpadDown(fIdx)
        assertFocused(TestTags.letterTag(fIdx))
        dpadCenter()
        waitForAnyEntryFocused()
        assertLetterSelected(fIdx)

        // Select J
        dpadLeft()
        waitForAnyLetterFocused()
        dpadUp(TOTAL_SYMBOLS)
        assertFocused(TestTags.letterTag(0))
        val jIdx = symbolIndex('J')
        dpadDown(jIdx)
        assertFocused(TestTags.letterTag(jIdx))
        dpadCenter()
        waitForAnyEntryFocused()
        assertLetterSelected(jIdx)
    }
}
