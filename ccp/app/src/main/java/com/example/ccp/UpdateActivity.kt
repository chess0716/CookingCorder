package com.example.ccp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ccp.databinding.ActivityUpdateBinding

class UpdateActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancelUpdate.setOnClickListener{finish()}
    }
}