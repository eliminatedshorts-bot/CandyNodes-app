package com.example

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.network.ConnectivityObserver
import com.example.ui.screens.MainBrowserScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    // Register file choosing launcher (enables config or plugin uploads inside game panel webview)
    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
            fileChooserCallback?.onReceiveValue(results)
        } else {
            fileChooserCallback?.onReceiveValue(null)
        }
        fileChooserCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Pre-create WebView/Chromium cache subdirectories to prevent opendir (Code Cache/js and wasm) warnings
        try {
            val cacheDir = cacheDir
            if (cacheDir != null) {
                val jsCacheDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/js")
                val wasmCacheDir = java.io.File(cacheDir, "WebView/Default/HTTP Cache/Code Cache/wasm")
                if (!jsCacheDir.exists()) {
                    jsCacheDir.mkdirs()
                }
                if (!wasmCacheDir.exists()) {
                    wasmCacheDir.mkdirs()
                }
            }
        } catch (e: Exception) {
            // Safe fallback
        }

        val connectivityObserver = ConnectivityObserver(applicationContext)

        setContent {
            MyApplicationTheme {
                MainBrowserScreen(
                    connectivityObserver = connectivityObserver,
                    onShowFileChooser = { filePathCallback, fileChooserParams ->
                        // Cancel any pending callbacks to avoid leaks
                        fileChooserCallback?.onReceiveValue(null)
                        fileChooserCallback = filePathCallback
                        
                        try {
                            val intent = fileChooserParams?.createIntent()
                            if (intent != null) {
                                fileChooserLauncher.launch(intent)
                            } else {
                                fileChooserCallback?.onReceiveValue(null)
                                fileChooserCallback = null
                            }
                        } catch (e: Exception) {
                            fileChooserCallback?.onReceiveValue(null)
                            fileChooserCallback = null
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
