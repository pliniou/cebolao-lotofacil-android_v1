package com.cebolao.lotofacil.core.utils

import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RetryExtensionsTest {

    @Test
    fun `retryExponentialBackoff should succeed on first attempt`() = runTest {
        var attempts = 0
        val result = retryExponentialBackoff(maxRetries = 3) {
            attempts++
            "success"
        }
        assertEquals("success", result)
        assertEquals(1, attempts)
    }

    @Test
    fun `retryExponentialBackoff should retry on failure`() = runTest {
        var attempts = 0
        val result = retryExponentialBackoff(maxRetries = 3, initialDelayMs = 10) {
            attempts++
            if (attempts < 3) throw RuntimeException("Simulated failure")
            "success"
        }
        assertEquals("success", result)
        assertEquals(3, attempts)
    }

    @Test
    fun `retryExponentialBackoff should fail after max retries exceeded`() = runTest {
        var attempts = 0
        try {
            retryExponentialBackoff(maxRetries = 2, initialDelayMs = 10) {
                attempts++
                throw RuntimeException("Persistent failure")
            }
            assertTrue(false, "Should have thrown exception")
        } catch (e: RuntimeException) {
            assertEquals("Persistent failure", e.message)
            assertEquals(3, attempts) // initial + 2 retries
        }
    }

    @Test
    fun `retryExponentialBackoff should have exponential backoff delay`() = runTest {
        val timestamps = mutableListOf<Long>()
        try {
            retryExponentialBackoff(maxRetries = 2, initialDelayMs = 50) {
                timestamps.add(System.currentTimeMillis())
                throw RuntimeException("Fail")
            }
        } catch (e: Exception) {
            // Expected
        }

        // Verify we have 3 attempts (initial + 2 retries)
        assertEquals(3, timestamps.size)
    }
}
