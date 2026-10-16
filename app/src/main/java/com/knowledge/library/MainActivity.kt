package com.knowledgelibrary

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var pendingBackup: String? = null

    private val openTextFile =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    val text = contentResolver.openInputStream(uri)
                        ?.bufferedReader(Charsets.UTF_8)
                        ?.use { it.readText() }
                        ?: throw IllegalStateException("Could not read the selected file.")

                    webView.evaluateJavascript(
                        "window.receiveImportedOutline(${JSONObject.quote(text)});",
                        null
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Could not import file: ${e.message ?: "read error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    private val createBackupFile =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
            val backup = pendingBackup
            pendingBackup = null
            if (uri != null && backup != null) {
                try {
                    contentResolver.openOutputStream(uri)?.bufferedWriter(Charsets.UTF_8)?.use {
                        it.write(backup)
                    } ?: throw IllegalStateException("Could not write the selected file.")
                    Toast.makeText(this, "Backup saved successfully.", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Could not save backup: ${e.message ?: "write error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()

        webView.addJavascriptInterface(
            NativeLibraryBridge(),
            "NativeLibrary"
        )

        setContentView(webView)
        webView.loadUrl("file:///android_asset/index.html")
    }

    inner class NativeLibraryBridge {
        @JavascriptInterface
        fun importTextFile() {
            runOnUiThread {
                try {
                    openTextFile.launch(
                        arrayOf("text/plain", "application/json", "application/octet-stream", "*/*")
                    )
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        this@MainActivity,
                        "No file picker is available.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        @JavascriptInterface
        fun exportBackup(json: String) {
            runOnUiThread {
                try {
                    pendingBackup = json
                    createBackupFile.launch("knowledge-library-backup.json")
                } catch (e: ActivityNotFoundException) {
                    pendingBackup = null
                    Toast.makeText(
                        this@MainActivity,
                        "No save-file picker is available.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Android API; retained for broad device compatibility")
    override fun onBackPressed() {
        webView.evaluateJavascript(
            "(function(){return window.handleAndroidBack ? window.handleAndroidBack() : false;})()"
        ) { result ->
            if (result != "true") {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.removeJavascriptInterface("NativeLibrary")
            webView.destroy()
        }
        super.onDestroy()
    }
}
