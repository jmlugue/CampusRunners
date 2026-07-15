package com.malayanquest.app

import android.os.Build
import java.net.URI

object Config {
    // Override with api.base.url in the root local.properties file when using
    // a physical phone, another Apache port, or another XAMPP project folder.
    val DEFAULT_API_BASE_URL: String
        get() {
            val configuredUrl = BuildConfig.API_BASE_URL
            return if (isEmulator()) emulatorHostUrl(configuredUrl) else configuredUrl
        }

    private fun isEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        val device = Build.DEVICE.lowercase()
        val product = Build.PRODUCT.lowercase()

        return fingerprint.startsWith("generic") ||
                fingerprint.contains("emulator") ||
                model.contains("sdk") ||
                model.contains("emulator") ||
                model.contains("android sdk built for") ||
                manufacturer.contains("genymotion") ||
                brand.startsWith("generic") && device.startsWith("generic") ||
                product.contains("sdk") ||
                product.contains("emulator")
    }

    private fun emulatorHostUrl(value: String): String {
        return try {
            val uri = URI(value)
            val host = uri.host ?: return value
            if (!isLocalNetworkHost(host)) {
                return value
            }
            URI(
                uri.scheme,
                uri.userInfo,
                "10.0.2.2",
                uri.port,
                uri.path,
                uri.query,
                uri.fragment
            ).toString()
        } catch (_: Exception) {
            value
        }
    }

    private fun isLocalNetworkHost(host: String): Boolean {
        return host == "localhost" ||
                host == "127.0.0.1" ||
                host.startsWith("192.168.") ||
                host.startsWith("10.") ||
                Regex("^172\\.(1[6-9]|2\\d|3[0-1])\\.").containsMatchIn(host)
    }
}
