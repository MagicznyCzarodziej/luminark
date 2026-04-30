package pl.przemyslawpitus.luminark.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SidebarNavigationTest : BaseTvNavigationTest() {

    @Test
    fun `focus can be placed on the first sidebar item`() {
        assertFocused(TestTags.entryItem(0))
        openSidebar()
        assertFocused(TestTags.sidebarItem(0))
    }

    @Test
    fun `pressing left closes the sidebar and restores focus to the entry`() {
        focusEntry(3)
        openSidebar()

        dpadLeft()
        assertFocused(TestTags.entryItem(3))
    }

    @Test
    fun `pressing right closes the sidebar and restores focus to the entry`() {
        focusEntry(3)
        openSidebar()

        dpadRight()
        assertFocused(TestTags.entryItem(3))
    }

    @Test
    fun `pressing down navigates between sidebar items`() {
        openSidebar()
        assertFocused(TestTags.sidebarItem(0))

        dpadDown()
        assertFocused(TestTags.sidebarItem(1))

        dpadDown()
        assertFocused(TestTags.sidebarItem(2))
    }

    @Test
    fun `reopening sidebar after scrolling resets scroll and focuses first item`() {
        openSidebar()

        // Scroll to the bottom of the tag list
        dpadDown(13) // 0=Rebuild, 1=All, 2..13=12 tags → last tag is sidebarItem(13)
        assertFocused(TestTags.sidebarItem(13))

        // Close sidebar
        dpadLeft()
        waitForAnyEntryFocused()

        // Reopen sidebar — should be scrolled to top with first item focused and visible
        openSidebar()
        assertFocused(TestTags.sidebarItem(0))
        rule.onNodeWithTag(TestTags.sidebarItem(0)).assertIsDisplayed()

        // Pressing down should go to the next visible item without a scroll jump
        dpadDown()
        assertFocused(TestTags.sidebarItem(1))
        rule.onNodeWithTag(TestTags.sidebarItem(1)).assertIsDisplayed()
    }
}
