package com.prime.toolz.chatbot
/*


import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.acsbendi.requestinspectorwebview.RequestInspectorWebViewClient
import com.acsbendi.requestinspectorwebview.WebViewRequest
import java.net.URL
//TODO: Fix in Future version.
private const val TAG = "Login"
private const val AUTHORIZATION = "authorization"
private const val COOKIE = "cookie"
private const val USER_AGENT = "user-agent"

private const val STREAM_USER_AGENT = "getstream-chatgpt-user-agent"
private const val USER_AGENT2 =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"


private class RequestInspectorWebViewClientImpl(
    view: WebView,
    private val onLoggedIn: (cookie: String) -> Unit
) : RequestInspectorWebViewClient(view) {
    var lastUpdateTime = SystemClock.elapsedRealtime()
    var initialUserAgent: String? = null

    private fun checkIfAuthorized(header: Map<String, String>): Boolean {
        return header.containsKey(AUTHORIZATION) &&
                header.containsKey(COOKIE) &&
                header.containsKey(USER_AGENT)
    }

    private val handler = Handler(Looper.getMainLooper())
    private inline fun runOnUiThread(crossinline block: () -> Unit) {
        handler.post {
            block.invoke()
        }
    }



    private fun WebView.extractAccessToken() {
        */
/*val javascript = """
            // JavaScript code to extract the accessToken from the page
            (function() {
                // Modify the selector according to the structure of the web page
                var accessTokenElement = document.querySelector('#access-token');

                if (accessTokenElement) {
                    return accessTokenElement.innerText;
                } else {
                    return null;
                }
            })();
        """.trimIndent()

        evaluateJavascript(javascript) { result ->
            // Process the result containing the extracted accessToken
            val accessToken = result.trim('"')
            // Use the accessToken as needed
            println("Access Token: $accessToken")
        }*//*

        Log.d(TAG, "extractAccessToken: ")
    }

    override fun onPageFinished(view: WebView, url: String?) {
        if (url != "https://chat.openai.com/api/auth/session")
        else view.extractAccessToken()
        super.onPageFinished(view, url)
    }



    */
/*override fun shouldInterceptRequest(
        view: WebView,
        webViewRequest: WebViewRequest
    ): WebResourceResponse? {
        val userAgent = webViewRequest.headers[USER_AGENT]
        if (initialUserAgent == null && userAgent != null) {
            initialUserAgent = userAgent
        }
        runOnUiThread {
            val currentTime = SystemClock.elapsedRealtime()
            val threadHold = 1000L
            if (currentTime - lastUpdateTime >= threadHold) {
                lastUpdateTime = currentTime
                val url = webViewRequest.url
                if (url.contains("auth0") || url.contains("accounts.google") &&
                    view.settings.userAgentString != STREAM_USER_AGENT
                ) {
                    view.settings.userAgentString = STREAM_USER_AGENT
                } else if (initialUserAgent != null &&
                    view.settings.userAgentString != initialUserAgent
                ) {
                    view.settings.userAgentString = initialUserAgent
                }
            }
        }

        if (checkIfAuthorized(webViewRequest.headers)) {
            val authorization = webViewRequest.headers[AUTHORIZATION] ?: return null
            val cookie = webViewRequest.headers[COOKIE] ?: return null
            val header = webViewRequest.headers
            //onLoggedIn(cookie)
            *//*
*/
/*runOnUiThread {
                view.loadUrl("https://chat.openai.com/api/auth/session")
            }*//*
*/
/*
        }
        return super.shouldInterceptRequest(view, webViewRequest)
    }*//*

}


*/
/**
 * This represents the login screen for [ChatGPT]. Once logged in, the cookie will be sent through the
 * [onLoggedIn] function.
 * This uses a WebView to login to the [ChatGPT] and receive the [cookie].
 *//*

@Composable
fun Login(
    onLoggedIn: (cookie: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = remember {
        WebView(context).also {
            it.webChromeClient = WebChromeClient();
            it.webViewClient = RequestInspectorWebViewClientImpl(it, onLoggedIn)
            it.loadUrl("https://chat.openai.com/chat")
            //it.setBackgroundColor(Color.Blue.toArgb())
            it.clipToOutline = true
            it.clipToPadding = true
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            it.settings.javaScriptEnabled = true
            it.settings.domStorageEnabled = true
            it.settings.savePassword = true
        }
    }
    // Handle back clicks.
    BackHandler(enabled = view.canGoBack()) {
        view.goBack()
    }
    AndroidView(
        factory = { view },
        modifier = modifier
    )
}*/
