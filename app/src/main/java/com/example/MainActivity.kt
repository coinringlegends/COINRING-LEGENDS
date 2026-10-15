package com.example

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.ByteArrayInputStream
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private var webView: WebView? = null
  private var fileUploadCallback: ValueCallback<Array<Uri>>? = null

  private val fileChooserLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { result ->
    val uri = result.data?.data
    if (uri != null) {
      fileUploadCallback?.onReceiveValue(arrayOf(uri))
    } else {
      fileUploadCallback?.onReceiveValue(null)
    }
    fileUploadCallback = null
  }

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Create native WebView directly as root view to avoid Compose ViewLayer reflection & Mesa rendernode probing
    val wv = WebView(this).apply {
      layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
      setBackgroundColor(android.graphics.Color.parseColor("#0A0A0A"))
      setLayerType(View.LAYER_TYPE_SOFTWARE, null)

      settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        allowFileAccess = true
        allowContentAccess = true
        allowFileAccessFromFileURLs = true
        allowUniversalAccessFromFileURLs = true
        loadWithOverviewMode = true
        useWideViewPort = true
        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
      }

      webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
          val url = request?.url?.toString() ?: return false
          if (url.endsWith(".apk") || url.contains("app-release.apk")) {
            try {
              val targetUrl = if (url.startsWith("http")) url else "https://cyberchatx.github.io/coinring-legends/app-release.apk"
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
              startActivity(intent)
              return true
            } catch (e: Exception) {
              return false
            }
          }
          if (url.startsWith("twint://") || 
              url.startsWith("whatsapp://") || 
              url.startsWith("https://wa.me") || 
              url.startsWith("tel:") || 
              url.startsWith("mailto:")) {
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
              startActivity(intent)
              return true
            } catch (e: Exception) {
              return false
            }
          }
          // Keep internal app and base44 domains inside webview
          if (url.contains("base44.app") || url.startsWith("file://")) {
            return false
          }
          if (url.startsWith("http://") || url.startsWith("https://")) {
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
              startActivity(intent)
              return true
            } catch (e: Exception) {
              return false
            }
          }
          return false
        }

        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
          val url = request?.url?.toString() ?: ""
          if (url.contains("app-release.apk")) {
            return WebResourceResponse(
              "application/vnd.android.package-archive",
              "UTF-8",
              ByteArrayInputStream(ByteArray(0))
            )
          }
          return super.shouldInterceptRequest(view, request)
        }
      }

      setDownloadListener { url, _, _, _, _ ->
        try {
          val targetUrl = if (url.startsWith("http")) url else "https://cyberchatx.github.io/coinring-legends/app-release.apk"
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
          startActivity(intent)
        } catch (e: Exception) {
          // ignore
        }
      }

      webChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
          webView: WebView?,
          filePathCallback: ValueCallback<Array<Uri>>?,
          fileChooserParams: FileChooserParams?
        ): Boolean {
          val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
          }
          if (filePathCallback != null) {
            fileUploadCallback = filePathCallback
            try {
              fileChooserLauncher.launch(intent)
              return true
            } catch (e: Exception) {
              fileUploadCallback = null
              return false
            }
          }
          return false
        }
      }

      loadUrl("file:///android_asset/index.html")
    }

    webView = wv
    setContentView(wv)

    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (webView?.canGoBack() == true) {
          webView?.goBack()
        } else {
          isEnabled = false
          onBackPressedDispatcher.onBackPressed()
        }
      }
    })
  }

  override fun onDestroy() {
    webView?.destroy()
    webView = null
    super.onDestroy()
  }
}

/**
 * Compose preview container for testing or previewing the Coinring Legends UI
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CoinringWebViewContainer(
  modifier: Modifier = Modifier
) {
  MyApplicationTheme {
    Surface(
      modifier = modifier.fillMaxSize().background(Color(0xFF0A0A0A)),
      color = Color(0xFF0A0A0A)
    ) {
      AndroidView(
        factory = { ctx ->
          WebView(ctx).apply {
            setBackgroundColor(android.graphics.Color.parseColor("#0A0A0A"))
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            settings.javaScriptEnabled = true
            loadUrl("file:///android_asset/index.html")
          }
        },
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}
