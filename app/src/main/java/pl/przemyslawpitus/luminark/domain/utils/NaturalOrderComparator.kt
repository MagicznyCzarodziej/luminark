package pl.przemyslawpitus.luminark.domain.utils

/**
 * E.g., "item 3" comes before "item 12", and "A Movie" is treated the same as "a movie".
 *
 * AI Generated, to be revised later
 */
object NaturalOrderComparator : Comparator<String> {
    override fun compare(s1: String, s2: String): Int {
        var i = 0
        var j = 0
        while (i < s1.length && j < s2.length) {
            val char1 = s1[i]
            val char2 = s2[j]

            if (char1.isDigit() && char2.isDigit()) {
                // Both are digits, so we parse the full number from each string
                var num1 = 0L
                while (i < s1.length && s1[i].isDigit()) {
                    num1 = num1 * 10 + (s1[i] - '0')
                    i++
                }

                var num2 = 0L
                while (j < s2.length && s2[j].isDigit()) {
                    num2 = num2 * 10 + (s2[j] - '0')
                    j++
                }

                if (num1 != num2) {
                    return num1.compareTo(num2)
                }
            } else {
                val result = char1.lowercaseChar().compareTo(char2.lowercaseChar())
                if (result != 0) {
                    return result
                }
                i++
                j++
            }
        }
        // If one string is a prefix of the other (e.g., "test" vs "testing"), the shorter one comes first.
        return s1.length - s2.length
    }
}
