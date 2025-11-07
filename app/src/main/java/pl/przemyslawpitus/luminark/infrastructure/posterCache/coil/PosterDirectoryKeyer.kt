package pl.przemyslawpitus.luminark.infrastructure.posterCache.coil

import coil3.key.Keyer
import coil3.request.Options

class PosterDirectoryKeyer : Keyer<PosterFetcher.PosterPath> {
    override fun key(data: PosterFetcher.PosterPath, options: Options): String {
        return data.path.toString()
    }
}