package com.braperucci.africa

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener {
    private val braperucci = "https://braperucci.africa"
    private lateinit var customClient: CustomWebViewClient

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customClient = CustomWebViewClient(this, progress_circular, bottomNavigationView, connect, retry_button)
        progress_circular.visibility = View.VISIBLE
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.setOnNavigationItemReselectedListener(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.settings.domStorageEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = customClient
        if (customClient.isNetworkAvailable()) {
            connect.visibility = View.GONE
            braperucci.loadPage(webView)
        } else {
            connect.visibility = View.VISIBLE
            bottomNavigationView.visibility = View.GONE
            retry_button.visibility = View.VISIBLE
        }
    }

    private fun checkRetry(b: Boolean) {
        if (b && retry_button.visibility == View.GONE) {
            retry_button.visibility = View.VISIBLE
            retry_button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in))
        } else if (retry_button.visibility == View.VISIBLE && !b) {
            retry_button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_out))
            retry_button.visibility = View.GONE
        }
    }

    private fun String.loadPage(webView: WebView?) {
        webView?.loadUrl(this)
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Exit app?")
            dialog.setCancelable(true)
            dialog.setPositiveButton("Exit") { _, _ -> super.onBackPressed() }
            dialog.setNegativeButton("Stay") { d, _ -> d.cancel() }
            dialog.show()
        }
    }

    fun reloadPage(view: View) {
        customClient.page_url.loadPage(webView)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> braperucci.loadPage(webView)
            R.id.events -> ("$braperucci/category/events").loadPage(webView)
            R.id.fashion -> ("$braperucci/category/fashion").loadPage(webView)
            R.id.weddings -> ("$braperucci/category/weddings").loadPage(webView)
            R.id.lookbooks -> ("$braperucci/lookbooks").loadPage(webView)
        }
        return true
    }

    override fun onNavigationItemReselected(item: MenuItem) {
        // Do nothing
    }
}
