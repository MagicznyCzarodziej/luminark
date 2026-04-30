package pl.przemyslawpitus.luminark.ui

object TestTags {
    const val LETTERS_COLUMN = "letters_column"
    fun letterTag(index: Int) = "letter_$index"

    const val TOP_BAR = "top_bar"
    fun topBarButton(index: Int) = "topbar_btn_$index"

    const val ENTRIES_LIST = "entries_list"
    fun entryItem(index: Int) = "entry_$index"

    const val SIDEBAR = "sidebar"
    fun sidebarItem(index: Int) = "sidebar_item_$index"
}
