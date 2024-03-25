package com.example.ccp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ccp.adapter.BoardAdapter
import com.example.ccp.databinding.ActivityMainBinding
import com.example.ccp.model.BoardDTO
import com.example.ccp.service.ApiService
import com.example.ccp.util.RetrofitClient
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

        initRecyclerView()
        apiService = RetrofitClient.apiService
        loadBoards()
    }

    private fun initRecyclerView() {
        binding.recyclerViewBoards.layoutManager = LinearLayoutManager(this)
        boardAdapter = BoardAdapter(this, emptyList(), object : BoardAdapter.OnItemClickListener {
            override fun onItemClick(num: Int) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra("board_id", num)
                startActivity(intent)
            }
        })
        binding.recyclerViewBoards.adapter = boardAdapter
    }

    private fun loadBoards() {
        apiService.getAllBoards()?.enqueue(object : Callback<List<BoardDTO>?> {
            override fun onResponse(
                call: Call<List<BoardDTO>?>,
                response: Response<List<BoardDTO>?>
            ) {
                val boards = response.body() ?: emptyList()
                Log.d("MainActivity", "Boards: $boards")
                boardAdapter.setData(boards)
            }

            override fun onFailure(call: Call<List<BoardDTO>?>, t: Throwable) {
                Log.e("MainActivity", "Failed to load boards: ${t.message}")
            }
        })
    }
}
