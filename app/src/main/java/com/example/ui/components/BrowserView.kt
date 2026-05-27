package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserView(
    state: BrowserState,
    onShowFileChooser: ((ValueCallback<Array<Uri>>?, WebChromeClient.FileChooserParams?) -> Unit)?,
    modifier: Modifier = Modifier
) {
    // Intercept hardware system back button to navigate backwards in WebView history
    val canGoBack = state.webViewInstance?.canGoBack() ?: false
    BackHandler(enabled = canGoBack) {
        state.goBack()
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Configure WebView settings for game panel compatibility
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    builtInZoomControls = true
                    displayZoomControls = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                    // Fix mixed content issues if any server assets stream HTTP
                    mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        state.isLoading = true
                        state.hasError = false
                        url?.let { state.currentUrl = it }
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        state.isLoading = false
                        url?.let { state.currentUrl = it }
                        state.pageTitle = view?.title ?: "CandyNodes Portal"
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val uri = request?.url ?: return false
                        val uriString = uri.toString()

                        // Intercept non-http(s) links to open in designated system tools
                        if (!uriString.startsWith("http://") && !uriString.startsWith("https://")) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                view?.context?.startActivity(intent)
                                return true
                            } catch (e: Exception) {
                                // Fallback
                            }
                            return true
                        }
                        
                        // Handle discord server redirects or similar externally if desired
                        if (uriString.contains("discord.gg") || uriString.contains("discord.com/invite")) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
                                view?.context?.startActivity(intent)
                                return true
                            } catch (e: Exception) {
                                // Fallback to loading inside webview
                            }
                        }

                        return false
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        // Only flag error on primary frame redirects to avoid blocking on simple analytical/ad script failures
                        if (request?.isForMainFrame == true) {
                            state.hasError = true
                            state.isLoading = false
                            state.lastErrorCode = error?.errorCode ?: -1
                            state.lastErrorDescription = error?.description?.toString() ?: "Unknown error"
                        }
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        state.loadingProgress = newProgress
                    }

                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        state.pageTitle = title ?: "CandyNodes Portal"
                    }

                    // Handle file choose callbacks (crucial for configuration uploads inside control panels)
                    override fun onShowFileChooser(
                        webView: WebView?,
                        filePathCallback: ValueCallback<Array<Uri>>?,
                        fileChooserParams: FileChooserParams?
                    ): Boolean {
                        if (onShowFileChooser != null) {
                            onShowFileChooser(filePathCallback, fileChooserParams)
                            return true
                        }
                        return false
                    }
                }

                // Support client-download attachments on click (Backups, CSVs, log files)
                setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Fallback
                    }
                }

                state.webViewInstance = this
                loadUrl(state.currentUrl)
            }
        },
        update = { webView ->
            // Dynamically alter user-agent depending on desktop/mobile modes
            val defaultUaString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 CandyNodesPortal/1.0"
            val desktopUaString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

            if (state.isDesktopMode) {
                webView.settings.userAgentString = desktopUaString
                webView.settings.useWideViewPort = true
                webView.settings.loadWithOverviewMode = true
            } else {
                webView.settings.userAgentString = defaultUaString
                webView.settings.useWideViewPort = true
                webView.settings.loadWithOverviewMode = true
            }
        },
        onRelease = { webView ->
            try {
                webView.stopLoading()
            } catch (e: Exception) {
                // Ignore
            }
            state.webViewInstance = null
        },
        modifier = modifier.fillMaxSize()
    )
}
