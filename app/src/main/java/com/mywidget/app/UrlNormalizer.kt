package com.mywidget.app

import java.net.URI

/**
 * Accepts only absolute HTTP(S) URLs. A missing scheme is treated as HTTPS.
 * java.net.URI is intentionally used instead of Android Uri.parse because the
 * latter accepts malformed input such as host names containing spaces.
 */
internal object UrlNormalizer {
    fun normalize(raw: String): String? {
        val input = raw.trim()
        if (input.isEmpty() || input.any(Char::isWhitespace)) return null

        val hasExplicitScheme = Regex("^[A-Za-z][A-Za-z0-9+.-]*:").containsMatchIn(input)
        val candidate = if (hasExplicitScheme) input else "https://$input"
        val uri = runCatching { URI(candidate) }.getOrNull() ?: return null
        val scheme = uri.scheme?.lowercase()
        if (scheme != "http" && scheme != "https") return null
        if (uri.host.isNullOrBlank() || uri.rawUserInfo != null) return null
        if (uri.port !in -1..65535) return null

        return uri.normalize().toASCIIString()
    }
}
