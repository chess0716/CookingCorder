package com.example.ccp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.example.ccp.databinding.ActivityUpdateBinding

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val num = intent.getIntExtra("board_id", -1) // 보드 번호 초기화

        if (num != -1) {
            setupWebView(num)
        }
    }

    private fun setupWebView(num: Int) {
        val webView = binding.webviewUpdate
        webView.settings.javaScriptEnabled = true // JavaScript 활성화
        webView.webViewClient = WebViewClient()
        webView.loadUrl("http://10.100.103.42:8005/update_for_mobile/$num") // 해당 URL 로드
    }
}