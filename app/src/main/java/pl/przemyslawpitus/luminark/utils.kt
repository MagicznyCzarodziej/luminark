package pl.przemyslawpitus.luminark

import pl.przemyslawpitus.luminark.domain.library.EntryId
import java.util.UUID

fun <T : Any?> T.letIf(condition: Boolean, block: (T) -> T): T {
    return if (condition) {
        block(this)
    } else {
        this
    }
}

fun randomEntryId(): EntryId = EntryId(UUID.randomUUID().toString())