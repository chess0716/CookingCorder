package com.example.ccp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MotionEvent
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.ccp.adapter.BoardAdapter
import com.example.ccp.databinding.ActivityDetailBinding
import com.example.ccp.model.BoardDTO
import com.example.ccp.model.User
import com.example.ccp.service.ApiService
import com.example.ccp.util.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Path


class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    lateinit var adapter: BoardAdapter
    private lateinit var apiService: ApiService
    private lateinit var ingrGetSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ingrGetSwitch = findViewById(R.id.ingrGet)

        apiService = RetrofitClient.apiService

        // Intent로 전달된 번호(num) 가져오기
        val num = intent.getIntExtra("board_id", -1)
        if (num != -1) {
            loadData(num)
        }

        // 수정페이지로 이동하기
        binding.btnGoUpdate.setOnClickListener {
            val intent = Intent(this@DetailActivity, UpdateActivity::class.java)
            // 필요하다면 업데이트에 필요한 데이터를 추가할 수 있습니다.
            startActivity(intent)
        }

        // 뒤로가기
        binding.btnBack.setOnClickListener { finish() }

        // 스위치 상태 변경 감지
        ingrGetSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 만약 스위치가 켜졌다면 텍스트를 "보유"로 변경
                ingrGetSwitch.text = "보유"
            } else {
                // 스위치가 꺼졌다면 텍스트를 "미보유"로 변경
                ingrGetSwitch.text = "미보유"
            }
        }
    }

    private fun loadData(num: Int) {
        apiService.getBoardByNum(num)?.enqueue(object : Callback<BoardDTO?> {
            override fun onResponse(call: Call<BoardDTO?>, response: Response<BoardDTO?>) {
                val board = response.body()
                if (board != null) {
                    val title = board.title
                    val user = board.writer
                    val content = board.content

                    // UI 업데이트
                    updateUI(title, user, content)
                } else {
                    Log.e("DetailActivity", "Failed to load board details")
                }
            }

            override fun onFailure(call: Call<BoardDTO?>, t: Throwable) {
                Log.e("DetailActivity", "Failed to load board details: ${t.message}")
            }
        })
    }

    private fun updateUI(title: String?, user: User?, content: String?) {
        // 받아온 게시물 정보를 UI에 반영합니다.
        binding.detailTitle.text = Editable.Factory.getInstance().newEditable(title)
        binding.detailWriter.text = Editable.Factory.getInstance().newEditable(user?.name ?: "Unknown")
        binding.detailContent.text = Editable.Factory.getInstance().newEditable(content)
    }


    // 화면 터치 시 키보드 숨기기
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            // rawX, rawY는 스크린, 즉 화면의 좌표값, x, y는 View안의 좌표값
            MotionEvent.ACTION_DOWN -> Log.d(
                ">>",
                "Touch down x: ${event.x} , rawX: ${event.rawX}"
            )

            MotionEvent.ACTION_UP -> Log.d(">>", "Touch up")
        }

        return super.onTouchEvent(event)
    }
}

