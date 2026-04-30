package pl.przemyslawpitus.luminark.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EntryListNavigationTest : BaseTvNavigationTest() {

    @Test
    fun `pressing down moves focus to the next entry`() {
        assertFocused(TestTags.entryItem(0))
        dpadDown()
        assertFocused(TestTags.entryItem(1))
    }

    @Test
    fun `pressing down 20 times scrolls and focuses a distant entry`() {
        assertFocused(TestTags.entryItem(0))
        dpadDown(20)
        assertFocused(TestTags.entryItem(20))
        rule.onNodeWithTag(TestTags.entryItem(20)).assertIsDisplayed()
    }

    @Test
    fun `scrolling through entries updates the selected letter`() {
        assertFocused(TestTags.entryItem(0))
        assertLetterSelected(1) // 'A' is symbol index 1

        // Entry 2 = first 'B' entry (Back to the Future)
        dpadDown(2)
        assertFocused(TestTags.entryItem(2))
        assertLetterSelected(symbolIndex('B'))
    }
}
