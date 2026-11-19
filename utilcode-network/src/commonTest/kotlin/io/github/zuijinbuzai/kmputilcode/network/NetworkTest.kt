package io.github.zuijinbuzai.kmputilcode.network
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*
class NetworkTest {
    @Test fun successAndQueryParameters() = runTest {
        val engine=MockEngine { request ->
            assertEquals("hello world",request.url.parameters["q"])
            respond("""{"ok":true}""",HttpStatusCode.OK,headersOf(HttpHeaders.ContentType,"application/json"))
        }
        val api=NetworkClient(createHttpClient(engine))
        try {
            val result=api.get<Map<String,Boolean>>("https://example.test") { parameter("q","hello world") }
            assertEquals(ApiResult.Success(mapOf("ok" to true),200),result)
        } finally {api.close()}
    }
    @Test fun httpErrorAndMalformedPayload() = runTest {
        for ((code,body) in listOf(404 to "missing",200 to "not json")) {
            val api=NetworkClient(createHttpClient(MockEngine {respond(body,HttpStatusCode.fromValue(code),headersOf(HttpHeaders.ContentType,"application/json"))}))
            try {
                val result=api.get<Map<String,Int>>("https://example.test")
                if(code==404) assertEquals(ApiResult.HttpFailure(404),result) else assertIs<ApiResult.Failure>(result)
            } finally {api.close()}
        }
    }
    @Test fun cancellationPropagates() = runTest {
        val api=NetworkClient(createHttpClient(MockEngine {throw CancellationException("stop")}))
        try {assertFailsWith<CancellationException> {api.get<String>("https://example.test")}} finally {api.close()}
    }
    @Test fun rejectsUnsupportedProtocols() = runTest {
        val api=NetworkClient(createHttpClient(MockEngine {error("must not execute")}))
        try {assertFailsWith<IllegalArgumentException> {api.get<String>("file:///etc/passwd")}} finally {api.close()}
    }
}
