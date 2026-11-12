package com.uedcl.power.data.network

import com.uedcl.power.utils.SecureStorageManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class AuthInterceptorTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: TestApiService
    private val secureStorage: SecureStorageManager = mockk()

    // Simple test Retrofit endpoint interface
    interface TestApiService {
        @GET("/test-endpoint")
        suspend fun getData(): retrofit2.Response>
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Mock secure storage returning a test JWT token
        every { secureStorage.getEncryptedToken("JWT_BEARER_TOKEN") } returns "mock_jwt_token_12345"

        // Build OkHttpClient with the AuthInterceptor under test
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(secureStorage))
            .build()

        // Build Retrofit pointing to the MockWebServer URL
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TestApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `auth interceptor correctly injects Bearer token into headers`() = runBlocking {
        // Queue a mock success response from the server
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{\"status\":\"success\"}"))

        // Make the network call via Retrofit
        apiService.getData()

        // Take the recorded request from MockWebServer to inspect headers
        val recordedRequest = mockWebServer.takeRequest()

        // Verify the Authorization header was correctly attached
        assertEquals("Bearer mock_jwt_token_12345", recordedRequest.getHeader("Authorization"))
        assertEquals("application/json", recordedRequest.getHeader("Accept"))
    }
}
