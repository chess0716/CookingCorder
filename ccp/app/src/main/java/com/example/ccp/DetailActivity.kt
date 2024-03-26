package com.example.ccp


import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.ccp.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    private lateinit var detailTitleEditText: EditText
    private lateinit var detailWriterEditText: EditText
    private lateinit var detailContentEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailTitleEditText = findViewById(R.id.insertTitle)
        detailWriterEditText = findViewById(R.id.insertlWriter)
        detailContentEditText = findViewById(R.id.insertContent)

        // Intent에서 DTO 객체의 정보를 받아옴
        val boardTitle = intent.getStringExtra("board_title")
        val boardWriter = intent.getStringExtra("board_writer")
        val boardContent = intent.getStringExtra("board_content")

        // 받아온 정보를 EditText에 설정
        detailTitleEditText.setText(boardTitle)
        detailWriterEditText.setText(boardWriter)
        detailContentEditText.setText(boardContent)
    }
}
