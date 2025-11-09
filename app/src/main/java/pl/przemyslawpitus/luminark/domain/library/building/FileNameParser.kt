package pl.przemyslawpitus.luminark.domain.library.building

import pl.przemyslawpitus.luminark.domain.library.Name
import pl.przemyslawpitus.luminark.domain.library.building.strategies.FILM_SERIES_FILM_NUMBER_PATTERN
import java.util.Locale
import java.util.regex.Pattern

private val SEASON_NUMBER_PATTERN: Pattern = Pattern.compile("""(?i)Season\s*(\d+)|S(\d+)|(\d+)""")
private val EPISODE_FILE_PATTERN: Pattern = Pattern.compile(
    """(?i).*\s-\sS(\d{2})E(\d{2})(?:\s-\s(.*))?\.\w+$"""
)
private val GENERIC_NUMBER_PATTERN: Pattern = Pattern.compile("(\\d+)")

data class EpisodeDetails(val number: Int, val title: String)

object FileNameParser {
    fun parseEpisodeDetails(fileName: String, videoExtensions: Set<String>, seriesName: String): EpisodeDetails {
        val matcher = EPISODE_FILE_PATTERN.matcher(fileName)
        val fileBaseName = getFileBaseName(fileName, videoExtensions)
        val episodeNameWithoutSeriesName = fileBaseName.removePrefix("$seriesName - ")

        if (!matcher.matches()) {
            val episodeNumber = extractNumber(fileName) ?: 0
            return EpisodeDetails(episodeNumber, episodeNameWithoutSeriesName)
        }

        return try {
            val episodeNumber = matcher.group(2)?.toInt()

            if (episodeNumber != null) {
                EpisodeDetails(episodeNumber, episodeNameWithoutSeriesName)
            } else {
                EpisodeDetails(extractNumber(fileName) ?: 0, episodeNameWithoutSeriesName)
            }
        } catch (e: NumberFormatException) {
            EpisodeDetails(extractNumber(fileName) ?: 0, episodeNameWithoutSeriesName)
        }
    }

    fun parseName(folderName: String): Name {
        val regex = """^(.*?)\s*\((.*?)\)\s*$""".toRegex()
        val match = regex.find(folderName)

        return if (match != null) {
            val mainName = match.groupValues[1].trim().replace(FILM_SERIES_FILM_NUMBER_PATTERN.toRegex(), "")
            val altName = match.groupValues[2].trim()
            Name(mainName, altName)
        } else {
            Name(folderName.trim().replace(FILM_SERIES_FILM_NUMBER_PATTERN.toRegex(), ""))
        }
    }

    fun extractSeasonNumber(name: String): Int? {
        val matcher = SEASON_NUMBER_PATTERN.matcher(name)
        if (!matcher.find()) return null
        return (matcher.group(1) ?: matcher.group(2) ?: matcher.group(3))?.toIntOrNull()
    }

    fun extractSeasonNumberFromEpisode(fileName: String): Int? {
        val matcher = EPISODE_FILE_PATTERN.matcher(fileName)
        return if (matcher.matches()) {
            matcher.group(1)?.toIntOrNull()
        } else {
            null
        }
    }

    fun isVideoFile(fileName: String, videoExtensions: Set<String>): Boolean {
        return fileName
            .substringAfterLast('.', "")
            .lowercase(Locale.getDefault()) in videoExtensions
    }

    private fun extractNumber(fileName: String): Int? {
        val matcher = GENERIC_NUMBER_PATTERN.matcher(fileName)
        if (matcher.find()) {
            return matcher.group(1)?.toIntOrNull()
        }
        return null
    }

    private fun getFileBaseName(fileName: String, videoExtensions: Set<String>): String {
        val extension = fileName.substringAfterLast('.', "")
        return if (extension.lowercase(Locale.getDefault()) in videoExtensions) {
            fileName.removeSuffix(".$extension")
        } else {
            fileName.substringBeforeLast('.')
        }
    }
}