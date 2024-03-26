package com.example.ccp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ccp.databinding.ActivityInsertBinding
import com.example.ccp.databinding.ActivityUpdateBinding

class InsertActivity : AppCompatActivity() {
    lateinit var binding: ActivityInsertBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelInsert.setOnClickListener{finish()}
    }
}