package pl.przemyslawpitus.luminark.domain.library.building

import io.github.ricoapon.readableregex.PatternFlag
import io.github.ricoapon.readableregex.ReadableRegex.regex
import io.github.ricoapon.readableregex.ReadableRegexPattern

/** Gets number from `[1]` at the start of the line */
val ORDINAL_NUMBER_PATTERN: ReadableRegexPattern = regex()
    .startOfLine()
    .literal("[")
    .group("number", regex().digit().oneOrMore())
    .literal("]")
    .whitespace()
    .build()

/** `Season 1` or `S1` or `1` (allows trailing zeros and any digits count. Case insensitive) */
val SEASON_NUMBER_PATTERN: ReadableRegexPattern = regex()
    .oneOf(
        regex()
            .literal("Season")
            .whitespace()
            .group(regex().digit().oneOrMore()),

        regex()
            .literal("S")
            .group(regex().digit().oneOrMore()),

        regex()
            .group(regex().digit().oneOrMore())
    )
    .buildWithFlags(PatternFlag.CASE_INSENSITIVE)

/** Example: `Series title - S01E01 - Episode title.mkv` */
val EPISODE_FILE_PATTERN: ReadableRegexPattern = regex()
    .startOfLine()
    .anything() // Series name
    .whitespace()
    .literal("-")
    .whitespace()
    .literal("S")
    .group("seasonNumber", regex().digit().exactlyNTimes(2))
    .literal("E")
    .group("episodeNumber", regex().digit().exactlyNTimes(2))
    .group(
        regex()
            .whitespace()
            .anything()
    ).optional()
    .literal(".") // Start of file extension part
    .word() // File extension
    .endOfLine()
    .buildWithFlags(PatternFlag.CASE_INSENSITIVE)

/**
 *  Input: `Some (example)`
 *   mainName: `Some`
 *   alternativeName: `example`
 */
val NAME_WITH_ALTERNATIVE_NAME_PATTERN: ReadableRegexPattern = regex()
    .startOfLine()
    .group("mainName", regex().anything().reluctant())
    .whitespace().zeroOrMore()
    .literal("(")
    .group("alternativeName", regex().anything().reluctant())
    .literal(")")
    .whitespace().zeroOrMore() // Allow trailing whitespace just in case of dirty name
    .endOfLine()
    .build()