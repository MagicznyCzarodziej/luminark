package pl.przemyslawpitus.luminark.ui

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.requestFocus
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TopBarNavigationTest : BaseTvNavigationTest() {

    @Test
    fun `pressing up does not escape above the top bar`() {
        rule.onNodeWithTag(TestTags.topBarButton(0)).requestFocus()
        assertFocused(TestTags.topBarButton(0))

        dpadUp()
        settle()
        assertFocused(TestTags.topBarButton(0))
    }

    @Test
    fun `left and right navigate between buttons`() {
        rule.onNodeWithTag(TestTags.topBarButton(0)).requestFocus()
        assertFocused(TestTags.topBarButton(0))

        dpadRight()
        assertFocused(TestTags.topBarButton(1))

        dpadRight()
        assertFocused(TestTags.topBarButton(2))
    }

    @Test
    fun `pressing down returns focus to the last entry`() {
        focusEntry(8)
        assertFocused(TestTags.entryItem(8))

        rule.onNodeWithTag(TestTags.topBarButton(0)).requestFocus()
        assertFocused(TestTags.topBarButton(0))

        dpadDown()
        assertFocused(TestTags.entryItem(8))
    }

    @Test
    fun `pressing left on the first button does not escape`() {
        rule.onNodeWithTag(TestTags.topBarButton(0)).requestFocus()
        assertFocused(TestTags.topBarButton(0))

        dpadLeft()
        settle()
        assertFocused(TestTags.topBarButton(0))
    }

    @Test
    fun `pressing right on the last button does not escape`() {
        rule.onNodeWithTag(TestTags.topBarButton(0)).requestFocus()
        assertFocused(TestTags.topBarButton(0))
        dpadRight()
        assertFocused(TestTags.topBarButton(1))
        dpadRight()
        assertFocused(TestTags.topBarButton(2))

        dpadRight()
        settle()
        assertFocused(TestTags.topBarButton(2))
    }
}
