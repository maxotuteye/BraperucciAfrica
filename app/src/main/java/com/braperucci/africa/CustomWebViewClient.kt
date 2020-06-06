package com.braperucci.africa

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.util.Base64
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.InputStream

class CustomWebViewClient(
    private val context: Context,
    private val progressBar: ProgressBar,
    private val bottomNavigationView: BottomNavigationView,
    private val textView: TextView,
    private val retry_button:Button
) :
    WebViewClient() {

        var page_url = ""

    override fun shouldOverrideUrlLoading(view: WebView?, request: String?): Boolean {
        page_url = request.toString()
        return try {
            progressBar.visibility = View.VISIBLE
            view?.loadUrl(request.toString())
            true
        } catch (e: Exception) {
            false
        }

    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (!isNetworkAvailable()) {
            retry_button.visibility = View.VISIBLE
            bottomNavigationView.visibility = View.GONE
            textView.visibility = View.VISIBLE
        }
        super.onPageStarted(view, url, favicon)
        view?.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        if (isNetworkAvailable()) {
            injectCSS(view!!)
            progressBar.visibility = View.GONE
            super.onPageFinished(view, url)
            bottomNavigationView.visibility = View.VISIBLE
            retry_button.visibility = View.GONE
            textView.visibility = View.GONE
            view.visibility = View.VISIBLE
        } else {
            retry_button.visibility = View.VISIBLE
            bottomNavigationView.visibility = View.GONE
            textView.visibility = View.VISIBLE
        }
    }

    private fun injectCSS(view: WebView) {
        try {
            val inputStream: InputStream = context.assets.open("style.css")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded: String = Base64.encodeToString(buffer, Base64.NO_WRAP)
            view.loadUrl(
                "javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +
                        "style.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(style)" +
                        "})()"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
