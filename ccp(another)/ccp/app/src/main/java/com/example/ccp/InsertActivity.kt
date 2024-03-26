package com.example.ccp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.ccp.databinding.ActivityInsertBinding
import com.example.ccp.model.DataDTO
import com.example.ccp.util.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InsertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsertBinding
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCategories()

        binding.btnAddForm.setOnClickListener {
            addNewForm()
        }

        binding.btnSubmitAll.setOnClickListener {
            submitAllForms()
        }

        binding.btnUpload.setOnClickListener {
            openGallery()
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
                Log.e("InsertActivity", "Failed to load categories", t)
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
                Log.e("InsertActivity", "Failed to load ingredients", t)
            }
        })
    }

    private fun addNewForm() {
        val newFormView = LayoutInflater.from(this).inflate(R.layout.new_form_layout, null)
        binding.newFormsSection.addView(newFormView)

        // Load categories in the new form
        loadCategoriesForNewForm(newFormView)

        // Set OnClickListener for the remove button in the new form
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
                Log.e("InsertActivity", "Failed to load categories for new form", t)
            }
        })
    }

    private fun submitAllForms() {
        for (i in 0 until binding.newFormsSection.childCount) {
            val newFormView = binding.newFormsSection.getChildAt(i)

            val categoryId = newFormView.findViewById<View>(R.id.spinnerCategory)?.let {
                if (it is Spinner) {
                    it.selectedItem.toString()
                } else {
                    ""
                }
            }

            val name = newFormView.findViewById<View>(R.id.spinnerName)?.let {
                if (it is Spinner) {
                    it.selectedItem.toString()
                } else {
                    ""
                }
            }

            val unit = newFormView.findViewById<View>(R.id.etUnit)?.let {
                if (it is EditText) {
                    it.text.toString()
                } else {
                    ""
                }
            }

            Log.d("Form $i", "Category: $categoryId, Name: $name, Unit: $unit")
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            // 이미지 업로드 등의 작업 수행
            // 여기서 imageUri를 사용하여 선택한 이미지를 업로드하거나 필요한 처리를 수행합니다.
        }
    }
}
