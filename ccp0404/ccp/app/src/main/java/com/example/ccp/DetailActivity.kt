package com.example.ccp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccp.adapter.IngrBoardAdapter
import com.example.ccp.databinding.ActivityDetailBinding
import com.example.ccp.model.BoardDTO
import com.example.ccp.model.IngrBoard
import com.example.ccp.service.ApiService
import com.example.ccp.service.FavoriteRequest
import com.example.ccp.service.UpdatePriceRequest
import com.example.ccp.util.RetrofitClient
import com.example.ccp.util.RetrofitClient.myPageService
import com.example.ccp.util.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var apiService: ApiService
    private var totalPrice: Int = 0
    private val boards = mutableListOf<IngrBoard>()
    private var boardNum: Int = -1 // 보드 번호 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitClient.apiService

        boardNum = intent.getIntExtra("board_id", -1) // 보드 번호 초기화

        if (boardNum != -1) {
            loadData(boardNum)
            setupWebView(boardNum) // 웹뷰 설정 메서드 호출
        }
        binding.checkLike.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 찜하기를 체크한 경우
                val username = SharedPreferencesHelper.getUsername(applicationContext)
                if (username != null) {
                    val favoriteRequest = FavoriteRequest(username = username, boardId = boardNum)
                    myPageService.addFavorite(favoriteRequest).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                // 성공적으로 서버에 찜 정보를 전송했을 때의 처리
                                Log.d("DetailActivity", "찜하기 정보가 성공적으로 전송되었습니다.")
                            } else {
                                // 서버 응답에 실패했을 때의 처리
                                Log.e("DetailActivity", "찜하기 정보 전송 실패: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            // 네트워크 오류 등으로 호출 자체에 실패했을 때의 처리
                            Log.e("DetailActivity", "찜하기 정보 전송 실패: ${t.message}")
                        }
                    })
                } else {
                    // username이 null인 경우, 로그인이 되어있지 않다는 메시지 처리
                    Log.e("DetailActivity", "사용자 이름을 불러올 수 없습니다. 로그인이 필요합니다.")
                    // 여기서 사용자에게 로그인 화면으로 이동하라는 안내를 할 수도 있습니다.
                }
            } else {
                // 찜하기를 해제한 경우, 필요에 따라 처리
            }
        }

    }

    private fun loadData(num: Int) {
        apiService.getBoardByNum(num).enqueue(object : Callback<BoardDTO> {
            override fun onResponse(call: Call<BoardDTO>, response: Response<BoardDTO>) {
                if (response.isSuccessful) {
                    val board = response.body()
                    board?.let {
                        binding.detailTitle.text = Editable.Factory.getInstance().newEditable(it.title)
                        binding.detailWriter.text = Editable.Factory.getInstance().newEditable(it.writer?.name ?: "Unknown")
                        binding.detailContent.text = Editable.Factory.getInstance().newEditable(it.content)
                    }
                } else {
                    Log.e("DetailActivity", "게시글 정보를 불러오는데 실패했습니다. 응답코드: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<BoardDTO>, t: Throwable) {
                Log.e("DetailActivity", "게시글 정보를 불러오는데 실패했습니다: ${t.message}", t)
            }
        })
    }

    private fun setupWebView(num: Int) {
        val webView = binding.webviewDetail
        webView.settings.javaScriptEnabled = true // JavaScript 활성화
        webView.webViewClient = WebViewClient()
        webView.loadUrl("http://211.220.34.225:8005/ingredient/$num") // 해당 URL 로드
    }
}
