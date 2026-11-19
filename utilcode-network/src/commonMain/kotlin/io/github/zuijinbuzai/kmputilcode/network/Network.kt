package io.github.zuijinbuzai.kmputilcode.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.call.body
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.github.zuijinbuzai.kmputilcode.serialization.JsonCodec
import kotlinx.coroutines.CancellationException

sealed interface ApiResult<out T> {
    data class Success<T>(val value: T, val status: Int) : ApiResult<T>
    data class HttpFailure(val status: Int) : ApiResult<Nothing>
    data class Failure(val cause: Throwable) : ApiResult<Nothing>
}
expect fun platformHttpEngine(): HttpClientEngine
fun createHttpClient(engine: HttpClientEngine = platformHttpEngine(), timeoutMillis: Long = 15_000): HttpClient {
    require(timeoutMillis > 0)
    return HttpClient(engine) {
        expectSuccess = false
        install(ContentNegotiation) { json(JsonCodec.defaultJson) }
        install(HttpTimeout) { requestTimeoutMillis = timeoutMillis; connectTimeoutMillis = timeoutMillis; socketTimeoutMillis = timeoutMillis }
    }
}

/** Owns the client, including an injected client. Always close once its owner is disposed. */
class NetworkClient(@PublishedApi internal val client: HttpClient = createHttpClient()) {
    suspend inline fun <reified T> get(url: String, noinline configure: HttpRequestBuilder.() -> Unit = {}): ApiResult<T> =
        request(url, HttpMethod.Get, configure)
    suspend inline fun <reified T> request(
        url: String,
        method: HttpMethod,
        noinline configure: HttpRequestBuilder.() -> Unit = {},
    ): ApiResult<T> {
        require(Url(url).protocol in listOf(URLProtocol.HTTP, URLProtocol.HTTPS)) { "Only HTTP(S) is supported" }
        return try {
            val response = client.request(url) { this.method = method; configure() }
            if (response.status.value in 200..299) ApiResult.Success(response.body<T>(), response.status.value)
            else ApiResult.HttpFailure(response.status.value)
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (failure: Exception) { ApiResult.Failure(failure) }
    }
    fun close() = client.close()
}
