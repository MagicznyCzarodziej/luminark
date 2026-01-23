package pl.przemyslawpitus.luminark.domain.library.building

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

class FileNameParserTest {
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    inner class ParseEpisodeDetails {
        @ParameterizedTest
        @MethodSource("validNames")
        fun `should parse episode file name to details`(testData: TestData) {
            val episodeDetails = FileNameParser.parseEpisodeDetails(
                fileName = testData.fileName,
                videoExtensions = testData.extensions,
                seriesName = testData.seriesName
            )
            episodeDetails.title shouldBe testData.expectedTitle
            episodeDetails.number shouldBe 1
        }

        fun validNames(): Stream<TestData> = Stream.of(
            TestData(
                "Some series - S02E01 - Title.mkv",
                setOf("mkv"),
                "Some series",
                "S02E01 - Title"
            ),
            TestData(
                "Some series - s02e01 - Title.mkv",
                setOf("mkv"),
                "Some series",
                "s02e01 - Title"
            ),
            TestData(
                "Some series - S02E01 - Title.mkv",
                setOf(),
                "Some series",
                "S02E01 - Title"
            ),
        )

        @ParameterizedTest
        @MethodSource("invalidNames")
        fun `should use fallback`(testData: TestData) {
            val episodeDetails = FileNameParser.parseEpisodeDetails(
                fileName = testData.fileName,
                videoExtensions = testData.extensions,
                seriesName = testData.seriesName
            )

            episodeDetails.title shouldBe testData.expectedTitle
            episodeDetails.number shouldBe -1
        }

        fun invalidNames(): Stream<TestData> = Stream.of(
            TestData(
                "Series title - S03ES2 - Episode title.mkv",
                setOf("mkv"),
                "Series title",
                "S03ES2 - Episode title"
            ),
            TestData(
                "Series title - 02 - Episode title.mkv",
                setOf("mkv"),
                "Series title",
                "02 - Episode title"
            ),
            TestData(
                "Series title - Episode title.mkv",
                setOf("mkv"),
                "Series title",
                "Episode title"
            ),
            TestData(
                "Episode title.mkv",
                setOf("mkv"),
                "Series title",
                "Episode title"
            ),
        )

        inner class TestData(
            val fileName: String,
            val extensions: Set<String>,
            val seriesName: String,
            val expectedTitle: String
        )
    }

    @Nested
    inner class ParseName {
        @ParameterizedTest
        @ValueSource(
            strings = [
                "Main name (Alternative name)",
                "Main name    (Alternative name)   ",
                "[12] Main name (Alternative name)",
            ]
        )
        fun `should parse name with alternative name`(folderName: String) {
            val result = FileNameParser.parseName(folderName)
            result.name shouldBe "Main name"
            result.alternativeName shouldBe "Alternative name"
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "Main name",
                "Main name    ",
                "[12] Main name",
            ]
        )
        fun `should parse name without alternative name`(folderName: String) {
            val result = FileNameParser.parseName(folderName)
            result.name shouldBe "Main name"
            result.alternativeName shouldBe null
        }
    }

    @Nested
    inner class ExtractSeasonNumberFromEpisode {
        @Test
        fun `should extract season number from episode file name`() {
            FileNameParser.extractSeasonNumberFromEpisode("Series title - S01E02 - Episode title.mkv") shouldBe 1
        }

        @Test
        fun `should return null if episode file name doesn't match regex`() {
            FileNameParser.extractSeasonNumberFromEpisode("Series title - S01ES1 - Episode title.mkv") shouldBe null
        }
    }

    @Nested
    inner class GetOrdinalNumberFromName {
        @Test
        fun `should extract ordinal number from file name`() {
            FileNameParser.getOrdinalNumberFromName("[1] Title") shouldBe 1
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "[1]Title",
                "(1) Title",
                "1 Title",
                "Title",
            ]
        )
        fun `should return null if name doesn't match regex`(text: String) {
            FileNameParser.getOrdinalNumberFromName(text) shouldBe null
        }
    }
}