package com.mywidget.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UrlNormalizerTest {
    @Test
    fun normalize_addsHttpsWhenSchemeIsMissing() {
        assertEquals("https://example.com/path", UrlNormalizer.normalize(" example.com/path "))
    }

    @Test
    fun normalize_acceptsHttpAndHttps() {
        assertEquals("http://example.com", UrlNormalizer.normalize("http://example.com"))
        assertEquals("https://example.com", UrlNormalizer.normalize("https://example.com"))
    }

    @Test
    fun normalize_rejectsUnsupportedOrMalformedUrls() {
        listOf(
            "",
            "javascript:alert(1)",
            "file:///etc/passwd",
            "https://bad host.example",
            "https://",
            "https://user:password@example.com",
            "https://example.com:99999"
        ).forEach { assertNull("Should reject: $it", UrlNormalizer.normalize(it)) }
    }
}
