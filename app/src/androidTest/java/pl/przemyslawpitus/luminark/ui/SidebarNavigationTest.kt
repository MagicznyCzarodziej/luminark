package pl.przemyslawpitus.luminark.ui

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
}
