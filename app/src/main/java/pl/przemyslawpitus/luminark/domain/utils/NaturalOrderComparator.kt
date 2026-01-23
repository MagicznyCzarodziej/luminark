package pl.przemyslawpitus.luminark.domain.utils

/** Comparator that ignores articles (a, an, the) */
object LibraryStyleComparator: Comparator<String> {
    private val articles = listOf("a ", "an ", "the ")

    override fun compare(s1: String, s2: String): Int {
        val normalized1 = s1.normalize()
        val normalized2 = s2.normalize()

        return normalized1.compareTo(normalized2)
    }

    private fun String.normalize(): String {
        val lowercase = this.trim().lowercase()

        // Find if the string starts with any of the defined articles
        for (article in articles) {
            if (lowercase.startsWith(article)) {
                // Return the string without the article prefix
                return lowercase.substring(article.length).trim()
            }
        }
        return lowercase
    }
}
