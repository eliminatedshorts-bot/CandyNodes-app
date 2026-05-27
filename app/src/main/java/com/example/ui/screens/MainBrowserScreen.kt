package com.example.ui.screens

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Loop
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.network.ConnectivityObserver
import com.example.ui.components.BrowserView
import com.example.ui.components.rememberBrowserState

@Composable
fun MainBrowserScreen(
    connectivityObserver: ConnectivityObserver,
    onShowFileChooser: ((ValueCallback<Array<Uri>>?, WebChromeClient.FileChooserParams?) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val browserState = rememberBrowserState("https://gp.candynodes.xyz/")
    
    // Core states
    var isConnected by remember { mutableStateOf(true) }

    // Observe Internet Connectivity
    LaunchedEffect(Unit) {
        connectivityObserver.isConnected.collect { status ->
            isConnected = status
            if (status && browserState.hasError) {
                // Instantly try reloading when picking up network
                browserState.reload()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp) // Edge-to-edge full webview experience
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Actual interactive Web Wrapper View - Always composed & attached to prevent lifecycle or InputDispatcher crashes
            BrowserView(
                state = browserState,
                onShowFileChooser = onShowFileChooser,
                modifier = Modifier.fillMaxSize()
            )

            // Page state route overlay: Check Internet & Errors
            if (!isConnected || browserState.hasError) {
                // Offline or failure panel overlay with a solid background to cleanly cover the WebView
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.error.copy(alpha = 0.4f),
                                RoundedCornerShape(30.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (!isConnected) Icons.Rounded.WifiOff else Icons.Rounded.ErrorOutline,
                            contentDescription = "Connection Problem Indicator",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = if (!isConnected) stringResource(R.string.error_offline_title) else "Error Loading Portal",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (!isConnected) stringResource(R.string.error_offline_desc) 
                               else "${browserState.lastErrorDescription}\nPlease verify the URL or ensure your hosting server is active.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.widthIn(max = 320.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    
                    Button(
                        onClick = {
                            if (isConnected) {
                                browserState.reload()
                            } else {
                                Toast.makeText(context, "Still offline. Check networking settings.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Rounded.Loop, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.btn_retry),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            // Reset back to absolute default GP portal
                            browserState.loadUrl("https://gp.candynodes.xyz/")
                        }
                    ) {
                        Text(
                            text = "Reset Default Portal",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
