package com.example.ccp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccp.adapter.BoardAdapter
import com.example.ccp.databinding.ActivityMainBinding
import com.example.ccp.model.BoardDTO
import com.example.ccp.service.ApiService
import com.example.ccp.util.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var boardAdapter: BoardAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView() // RecyclerView 초기화

        // Retrofit을 사용하여 ApiService 인터페이스의 구현체를 가져옴
        apiService = RetrofitClient.apiService

        loadBoards() // 서버에서 게시글 데이터를 가져와 RecyclerView에 표시
    }

    private fun initRecyclerView() {
        // RecyclerView를 초기화하고 LinearLayoutManager와 Adapter를 설정
        binding.recyclerViewBoards.layoutManager = LinearLayoutManager(this)
        boardAdapter = BoardAdapter(emptyList()) // 빈 목록으로 초기화된 BoardAdapter 생성
        binding.recyclerViewBoards.adapter = boardAdapter // Adapter 설정
    }

    private fun loadBoards() {
        // ApiService를 사용하여 모든 게시글을 가져오는 네트워크 요청을 만듦
        apiService.getAllBoards()?.enqueue(object : Callback<List<BoardDTO>?> {
            // 성공적으로 응답을 받았을 때 호출되는 콜백 함수
            override fun onResponse(
                call: Call<List<BoardDTO>?>,
                response: Response<List<BoardDTO>?>
            ) {
                val boards = response.body() ?: emptyList() // 응답에서 게시글 목록을 가져옴
                Log.d("MainActivity", "Boards: $boards") // 로그에 게시글 목록을 출력

                // 가져온 게시글 목록으로 RecyclerView를 업데이트
                boardAdapter.setData(boards)

                // 가져온 이미지 URL을 보드 어댑터에 전달하여 이미지를 가져오도록 함
                for (board in boards) {
                    if (!board.imageUrl.isNullOrEmpty()) {
                        loadBoardImage(board)
                    }
                }
            }

            // 요청이 실패했을 때 호출되는 콜백 함수
            override fun onFailure(call: Call<List<BoardDTO>?>, t: Throwable) {
                // 네트워크 호출 실패 처리
                Log.e("MainActivity", "Failed to load boards: ${t.message}")
            }
        })
    }

    private fun loadBoardImage(board: BoardDTO) {
        // 이미지를 가져오는 Retrofit 요청
        apiService.getImage(board.imageUrl).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // 이미지 로드 및 표시
                    val inputStream = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    // RecyclerView의 해당 보드에 이미지를 설정하는 작업을 수행
                    boardAdapter.setBoardImage(board, bitmap)
                } else {
                    // 이미지 가져오기 실패 처리
                    Log.e("MainActivity", "Failed to load image: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 네트워크 호출 실패 처리
                Log.e("MainActivity", "Failed to fetch image: ${t.message}")
            }
        })
    }
}
