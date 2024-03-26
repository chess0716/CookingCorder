package com.example.ccp

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.example.ccp.databinding.ItemIngredientBinding
import com.example.ccp.model.DataDTO

class InsertAdapter(private var ingredients: List<DataDTO>) : RecyclerView.Adapter<InsertAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(val binding: ItemIngredientBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        with(holder) {
            val ingredient = ingredients[position]
            Log.d("InsertAdapter", "Ingredient: $ingredient")

            // 카테고리 스피너 설정
            val categoryPosition = findPositionInSpinner(binding.categorySpinner, ingredient.category)
            if (categoryPosition != -1) {
                binding.categorySpinner.setSelection(categoryPosition)
                Log.d("InsertAdapter", "Category Spinner: Selected position - $categoryPosition")
            } else {
                Log.d("InsertAdapter", "Category Spinner: Category not found")
            }

            // 식재료 스피너 설정
            val ingredientPosition = findPositionInSpinner(binding.ingredientSpinner, ingredient.name)
            if (ingredientPosition != -1) {
                binding.ingredientSpinner.setSelection(ingredientPosition)
                Log.d("InsertAdapter", "Ingredient Spinner: Selected position - $ingredientPosition")
            } else {
                Log.d("InsertAdapter", "Ingredient Spinner: Ingredient not found")
            }
        }
    }



    override fun getItemCount(): Int = ingredients.size

    fun updateIngredients(newIngredients: List<DataDTO>) {
        ingredients = newIngredients
        notifyDataSetChanged()
    }
}
private fun findPositionInSpinner(spinner: Spinner, value: String): Int {
    val adapter = spinner.adapter
    if (adapter is ArrayAdapter<*>) {
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == value) {
                return i
            }
        }
    }
    return -1
}
