package pl.przemyslawpitus.luminark.domain.library.building

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PatternsTest {
    @ParameterizedTest
    @ValueSource(strings = [
        "[1] ",
        "[1234] ",
        "[01] ",
    ])
    fun `ORDINAL_NUMBER_PATTERN - valid`(fileName: String) {
        ORDINAL_NUMBER_PATTERN.matchesTextExactly(fileName) shouldBe true
    }

    @ParameterizedTest
    @ValueSource(strings = [
        " [1] ",
        "(1) ",
        "1 ",
    ])
    fun `ORDINAL_NUMBER_PATTERN - invalid`(fileName: String) {
        ORDINAL_NUMBER_PATTERN.matchesTextExactly(fileName) shouldBe false
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "Season 01",
        "Season 1",
        "season 1",
        "S01",
        "S1",
        "s1",
        "01",
        "1",
    ])
    fun `SEASON_NUMBER_PATTERN - valid`(text: String) {
        SEASON_NUMBER_PATTERN.matchesTextExactly(text) shouldBe true
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "Series title - S01E02 - Episode title.mkv",
        "Series title - s01e02 - Episode title.MKV",
        "Series title - S01E02.mkv",
    ])
    fun `EPISODE_FILE_PATTERN - valid`(fileName: String) {
        EPISODE_FILE_PATTERN.matchesTextExactly(fileName) shouldBe true

        val matcher = EPISODE_FILE_PATTERN.matches(fileName)
        matcher.matches()
        matcher.group("seasonNumber") shouldBe "01"
        matcher.group("episodeNumber") shouldBe "02"
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "Main name (Alternative name)",
        "Main name     (Alternative name)    ",
    ])
    fun `NAME_WITH_ALTERNATIVE_NAME_PATTERN - valid`(fileName: String) {
        NAME_WITH_ALTERNATIVE_NAME_PATTERN.matchesTextExactly(fileName) shouldBe true

        val matcher = NAME_WITH_ALTERNATIVE_NAME_PATTERN.matches(fileName)
        matcher.matches()
        matcher.group("mainName") shouldBe "Main name"
        matcher.group("alternativeName") shouldBe "Alternative name"
    }
}