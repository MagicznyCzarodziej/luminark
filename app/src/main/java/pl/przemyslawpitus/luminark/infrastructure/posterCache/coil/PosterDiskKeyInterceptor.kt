package pl.przemyslawpitus.luminark.infrastructure.posterCache.coil

import coil3.intercept.Interceptor
import coil3.request.ImageResult

class PosterDiskKeyInterceptor : Interceptor {
    override suspend fun intercept(
        chain: Interceptor.Chain
    ): ImageResult {
        val originalRequest = chain.request
        val data = originalRequest.data

        if (data is PosterFetcher.PosterPath) {
            val newRequest = originalRequest.newBuilder()
                .diskCacheKey(data.path.toString())
                .build()

            return chain.withRequest(newRequest).proceed()
        }

        return chain.proceed()
    }
}