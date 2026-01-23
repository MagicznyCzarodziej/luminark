package pl.przemyslawpitus.luminark.domain.library.building

import pl.przemyslawpitus.luminark.domain.library.Name
import timber.log.Timber
import java.lang.Exception
import java.util.Locale

data class EpisodeDetails(val number: Int, val title: String)

object FileNameParser {
    fun parseEpisodeDetails(fileName: String, videoExtensions: Set<String>, seriesName: String): EpisodeDetails {
        val matcher = EPISODE_FILE_PATTERN.matches(fileName)
        val fileBaseName = getFileBaseName(fileName, videoExtensions)
        val episodeNameWithoutSeriesName = fileBaseName.removePrefix("$seriesName - ")

        return try {
            if (!matcher.matches()) {
                throw RuntimeException("Episode file name does not match episode pattern: $fileName")
            }

            val episodeNumber = matcher.group("episodeNumber")!!.toInt()

            EpisodeDetails(episodeNumber, episodeNameWithoutSeriesName)
        } catch (exception: Exception) {
            Timber.w(exception, "Error while trying to parse episode title: $fileName")
            EpisodeDetails(-1, episodeNameWithoutSeriesName)
        }
    }

    fun parseName(folderName: String): Name {
        val match = NAME_WITH_ALTERNATIVE_NAME_PATTERN.matches(folderName)

        return if (match.matches()) {
            val mainName = match.group("mainName")!!
                .trim()
                .replaceFirst(ORDINAL_NUMBER_PATTERN.underlyingPattern.toRegex(), "") // Remove ordinal number from main name

            val altName = match.group("alternativeName")!!.trim()

            Name(mainName, altName)
        } else {
            Name(folderName.trim().replaceFirst(ORDINAL_NUMBER_PATTERN.underlyingPattern.toRegex(), ""))
        }
    }

    fun extractSeasonNumber(name: String): Int? {
        val matcher = SEASON_NUMBER_PATTERN.matches(name)
        if (!matcher.find()) return null
        return (matcher.group(1) ?: matcher.group(2) ?: matcher.group(3))?.toIntOrNull()
    }

    fun extractSeasonNumberFromEpisode(fileName: String): Int? {
        val matcher = EPISODE_FILE_PATTERN.matches(fileName)
        return if (matcher.matches()) {
            matcher.group("seasonNumber")!!.toInt()
        } else {
            null
        }
    }

    /** `[1] Example title` -> 1 */
    fun getOrdinalNumberFromName(name: String): Int? {
        val matcher = ORDINAL_NUMBER_PATTERN.matches(name)
        if (matcher.find()) {
            return matcher.group("number")!!.toInt()
        }
        return null
    }

    fun isVideoFile(fileName: String, videoExtensions: Set<String>): Boolean {
        return fileName
            .substringAfterLast('.', "")
            .lowercase(Locale.getDefault()) in videoExtensions
    }

    /** Returns file name without extension */
    private fun getFileBaseName(fileName: String, videoExtensions: Set<String>): String { // TODO Why is this so complicated, why not just return substringBeforeLast('.')?
        val extension = fileName.substringAfterLast('.', "")
        return if (extension.lowercase(Locale.getDefault()) in videoExtensions) {
            fileName.removeSuffix(".$extension")
        } else {
            fileName.substringBeforeLast('.')
        }
    }
}