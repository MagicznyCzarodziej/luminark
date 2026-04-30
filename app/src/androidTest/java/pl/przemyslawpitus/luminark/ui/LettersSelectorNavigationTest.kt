package pl.przemyslawpitus.luminark.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LettersSelectorNavigationTest : BaseTvNavigationTest() {

    @Test
    fun `pressing down moves through letters`() {
        navigateToLetters(1) // entry 0 starts with 'A' → letter index 1

        val dIdx = symbolIndex('D')
        dpadDown(dIdx - 1)
        assertFocused(TestTags.letterTag(dIdx))
    }

    @Test
    fun `pressing left does not escape the selector`() {
        navigateToLetters(1)

        dpadLeft()
        settle()
        assertFocused(TestTags.letterTag(1))
    }

    @Test
    fun `pressing right returns focus to the last entry`() {
        focusEntry(5) // "Bear, The" → sortName starts with 'B'
        assertFocused(TestTags.entryItem(5))

        navigateToLetters(symbolIndex('B'))

        dpadRight()
        assertFocused(TestTags.entryItem(5))
    }

    @Test
    fun `selecting a letter focuses the first matching entry`() {
        navigateToLetters(1) // letter A

        val dIdx = symbolIndex('D')
        dpadDown(dIdx - 1)
        assertFocused(TestTags.letterTag(dIdx))

        dpadCenter()
        waitForAnyEntryFocused()
        assertLetterSelected(dIdx)
    }

    @Test
    fun `selecting a letter scrolls the entry list to the correct position`() {
        navigateToLetters(1) // letter A

        val mIdx = symbolIndex('M')
        dpadDown(mIdx - 1)
        assertFocused(TestTags.letterTag(mIdx))

        dpadCenter()
        waitForAnyEntryFocused()
        assertLetterSelected(mIdx)
    }
}
