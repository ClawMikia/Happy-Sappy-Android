// File: ui/adapter/ExpenseAdapter.kt
package com.happysappy.app.ui.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.happysappy.app.R
import com.happysappy.app.data.entity.ExpenseEntity
import com.happysappy.app.databinding.ItemExpenseBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView Adapter for displaying expense items.
 * 
 * This adapter uses ListAdapter with DiffUtil for efficient list updates
 * and view recycling for optimal performance.
 */
class ExpenseAdapter(
    private val onDeleteClick: (ExpenseEntity) -> Unit
) : ListAdapter<ExpenseEntity, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpenseViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: ExpenseEntity) {
            // Format and display amount
            binding.tvAmount.text = String.format(Locale.getDefault(), "$%.2f", expense.amountSpent)
            
            // Display category
            binding.tvCategory.text = expense.category
            
            // Display note if available
            if (!expense.note.isNullOrBlank()) {
                binding.tvNote.text = expense.note
                binding.tvNote.visibility = View.VISIBLE
            } else {
                binding.tvNote.visibility = View.GONE
            }
            
            // Format and display date
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.tvDate.text = sdf.format(Date(expense.date))
            
            // Display frequency
            binding.tvFrequency.text = expense.frequency
            
            // Set category color
            setCategoryColor(expense.category)
            
            // Set up delete button click listener
            binding.btnDelete.setOnClickListener {
                onDeleteClick(expense)
            }
        }

        private fun setCategoryColor(category: String) {
            val color = getCategoryColor(category)
            
            // Set color for the category indicator
            val indicatorDrawable = binding.viewCategoryColor.background
            if (indicatorDrawable is GradientDrawable) {
                indicatorDrawable.setColor(color)
            }
            
            // Set color for the category chip
            val chipDrawable = binding.tvCategory.background
            if (chipDrawable is GradientDrawable) {
                chipDrawable.setColor(color)
            }
        }

        private fun getCategoryColor(category: String): Int {
            return when (category.lowercase()) {
                "food" -> binding.root.context.getColor(R.color.category_food)
                "transport" -> binding.root.context.getColor(R.color.category_transport)
                "shopping" -> binding.root.context.getColor(R.color.category_shopping)
                "entertainment" -> binding.root.context.getColor(R.color.category_entertainment)
                "bills" -> binding.root.context.getColor(R.color.category_bills)
                "health" -> binding.root.context.getColor(R.color.category_health)
                "education" -> binding.root.context.getColor(R.color.category_education)
                else -> binding.root.context.getColor(R.color.category_other)
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates.
     */
    class ExpenseDiffCallback : DiffUtil.ItemCallback<ExpenseEntity>() {
        override fun areItemsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
            return oldItem == newItem
        }
    }
}