package com.example.ccp

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ccp.databinding.ActivityInsertBinding
import com.example.ccp.model.DataDTO
import com.example.ccp.model.IngrBoard
import com.example.ccp.service.ApiResponse
import com.example.ccp.service.ApiService
import com.example.ccp.util.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class InsertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsertBinding
    private var imageUrl: Uri? = null
    private lateinit var getContent: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { uploadImage(it) }
        }

        loadCategories()

        binding.btnAddForm.setOnClickListener {
            addNewForm()
        }

        binding.btnUpload.setOnClickListener {
            openGallery()
        }

        binding.btnSubmitAll.setOnClickListener {
            if (imageUrl != null) {
                submitAllForms()
                submitRecipe()
            } else {
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategories() {
        RetrofitClient.ingrService.getNamesByCategory("all").enqueue(object : Callback<List<DataDTO>> {
            override fun onResponse(call: Call<List<DataDTO>>, response: Response<List<DataDTO>>) {
                if (response.isSuccessful) {
                    val categories = response.body()?.map { it.category }?.distinct()
                    categories?.let {
                        val adapter = ArrayAdapter(this@InsertActivity, android.R.layout.simple_spinner_dropdown_item, it)
                        binding.spinnerCategory.adapter = adapter
                        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                val selectedCategory = it[position]
                                loadIngredients(selectedCategory)
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<DataDTO>>, t: Throwable) {
                handleNetworkError("Failed to load categories", t)
            }
        })
    }

    private fun loadIngredients(category: String) {
        RetrofitClient.ingrService.getNamesByCategory(category).enqueue(object : Callback<List<DataDTO>> {
            override fun onResponse(call: Call<List<DataDTO>>, response: Response<List<DataDTO>>) {
                if (response.isSuccessful) {
                    val ingredients = response.body()?.map { it.name }
                    ingredients?.let {
                        val adapter = ArrayAdapter(this@InsertActivity, android.R.layout.simple_spinner_dropdown_item, it)
                        binding.spinnerName.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<List<DataDTO>>, t: Throwable) {
                handleNetworkError("Failed to load ingredients", t)
            }
        })
    }

    private fun addNewForm() {
        val newFormView = layoutInflater.inflate(R.layout.new_form_layout, null)
        binding.newFormsSection.addView(newFormView)

        loadCategoriesForNewForm(newFormView)

        val removeButton = newFormView.findViewById<View>(R.id.btnRemoveForm)
        removeButton.setOnClickListener {
            binding.newFormsSection.removeView(newFormView)
        }
    }

    private fun loadCategoriesForNewForm(newFormView: View) {
        val spinnerCategory = newFormView.findViewById<View>(R.id.spinnerCategory)
        RetrofitClient.ingrService.getNamesByCategory("all").enqueue(object : Callback<List<DataDTO>> {
            override fun onResponse(call: Call<List<DataDTO>>, response: Response<List<DataDTO>>) {
                if (response.isSuccessful) {
                    val categories = response.body()?.map { it.category }?.distinct()
                    categories?.let {
                        val adapter = ArrayAdapter(this@InsertActivity, android.R.layout.simple_spinner_dropdown_item, it)
                        if (spinnerCategory is AdapterView<*>) {
                            (spinnerCategory as AdapterView<*>).adapter = adapter
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<DataDTO>>, t: Throwable) {
                handleNetworkError("Failed to load categories for new form", t)
            }
        })
    }


    private fun submitAllForms() {
        val forms = mutableListOf<IngrBoard>()
        for (i in 0 until binding.newFormsSection.childCount) {
            val newFormView = binding.newFormsSection.getChildAt(i)
            val title = binding.etTitle.text.toString()
            val category = newFormView.findViewById<Spinner>(R.id.spinnerCategory)?.selectedItem.toString()
            val name = newFormView.findViewById<Spinner>(R.id.spinnerName)?.selectedItem.toString()

            val unitString = newFormView.findViewById<EditText>(R.id.etUnit)?.text.toString()
            val unit = try {
                unitString.toInt()
            } catch (e: NumberFormatException) {
                0 // 변환 실패 시 0을 사용
            }

            forms.add(IngrBoard().apply {
                this.title = title
                this.category = category
                this.name = name
                this.unit = unit
                this.imageUrl = imageUrl
            })
        }

        RetrofitClient.ingrService.submitAllForms(forms).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Toast.makeText(this@InsertActivity, it.message, Toast.LENGTH_SHORT).show()

                        if (it.isSuccess) {
                            // 성공 처리
                            Toast.makeText(this@InsertActivity, "Operation successful!", Toast.LENGTH_LONG).show()
                        } else {
                            // 실패 처리
                            Toast.makeText(this@InsertActivity, "Operation failed. Please try again.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    // 서버 에러 처리
                    Toast.makeText(this@InsertActivity, "Server error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // 네트워크 오류 등의 실패 처리
                Toast.makeText(this@InsertActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun openGallery() {
        getContent.launch("image/*")
    }

    private fun uploadImage(uri: Uri) {
        imageUrl = uri
        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
    }

    private fun handleNetworkError(message: String, throwable: Throwable) {
        Log.e("InsertActivity", message, throwable)
        Toast.makeText(this@InsertActivity, "Network error occurred. Please check your internet connection.", Toast.LENGTH_SHORT).show()
    }
    private fun submitRecipe() {
        imageUrl?.let { uri ->
            val title = createPartFromString(binding.etTitle.text.toString())
            val content = createPartFromString(binding.etContent.text.toString())
            val imagePart = prepareFilePart("image", uri)

            RetrofitClient.ingrService.submitRecipe(title, content, imagePart).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        // 성공 처리 로직
                    } else {
                        // 실패 처리 로직
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // 네트워크 오류 처리 로직
                }
            })
        }
    }

    private fun createPartFromString(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
        val file = File(cacheDir, "temp_image").apply {
            contentResolver.openInputStream(fileUri)?.use { inputStream ->
                outputStream().use { fileOutputStream ->
                    inputStream.copyTo(fileOutputStream)
                }
            }
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}
