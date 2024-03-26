package com.example.ccp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ccp.databinding.ActivityInsertBinding
import com.example.ccp.databinding.ItemIngredientBinding
import com.example.ccp.model.DataDTO
import com.example.ccp.util.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InsertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertBinding
    private lateinit var itemIngredientBinding: ItemIngredientBinding
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        itemIngredientBinding = ItemIngredientBinding.inflate(layoutInflater)

        setupCategorySpinner("1") // 카테고리 스피너 설정

        binding.chooseImageButton.setOnClickListener {
            openImageChooser()
        }

        binding.submitAllButton.setOnClickListener {
            // uploadDataWithImage() 함수 호출
        }
    }

    private fun setupCategorySpinner(categoryId: String) {
        RetrofitClient.ingrService.getNamesByCategory(categoryId)
            .enqueue(object : Callback<List<DataDTO>> {
                override fun onResponse(call: Call<List<DataDTO>>, response: Response<List<DataDTO>>) {
                    if (response.isSuccessful) {
                        val dataDTOList = response.body() ?: emptyList()
                        displayDataInSpinner(dataDTOList, itemIngredientBinding.categorySpinner)
                    } else {
                        Toast.makeText(applicationContext, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                    // 추가: 서버 응답 로그
                    Log.d("ServerResponse", "Response: $response")
                }

                override fun onFailure(call: Call<List<DataDTO>>, t: Throwable) {
                    Toast.makeText(applicationContext, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    // 추가: 서버 응답 실패 로그
                    Log.e("ServerResponse", "Failed to get server response", t)
                }
            })
    }


    private fun displayDataInSpinner(dataList: List<DataDTO>, spinner: Spinner) {
        val names = dataList.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)
        spinner.adapter = adapter
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.imageView.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
