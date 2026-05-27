package com.example.ui.components

import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class BrowserState(
    initialUrl: String
) {
    var currentUrl by mutableStateOf(initialUrl)
    var pageTitle by mutableStateOf("CandyNodes Game Panel")
    var loadingProgress by mutableStateOf(0)
    var isLoading by mutableStateOf(false)
    var isDesktopMode by mutableStateOf(false)
    var webViewInstance by mutableStateOf<WebView?>(null)
    var hasError by mutableStateOf(false)
    var lastErrorCode by mutableStateOf(0)
    var lastErrorDescription by mutableStateOf("")

    fun loadUrl(url: String) {
        val cleanUrl = when {
            url.isBlank() -> "https://gp.candynodes.xyz/"
            !url.startsWith("http://") && !url.startsWith("https://") -> "https://$url"
            else -> url
        }
        currentUrl = cleanUrl
        hasError = false
        webViewInstance?.loadUrl(cleanUrl)
    }

    fun goBack() {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        }
    }

    fun goForward() {
        if (webViewInstance?.canGoForward() == true) {
            webViewInstance?.goForward()
        }
    }

    fun reload() {
        hasError = false
        webViewInstance?.reload()
    }

    fun stopLoading() {
        webViewInstance?.stopLoading()
    }
}

@Composable
fun rememberBrowserState(initialUrl: String = "https://gp.candynodes.xyz/"): BrowserState {
    return remember { BrowserState(initialUrl) }
}
