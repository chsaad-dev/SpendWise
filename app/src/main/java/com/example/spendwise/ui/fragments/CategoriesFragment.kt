package com.example.spendwise.ui.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.data.Category
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class CategoriesFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvCategories = view.findViewById<RecyclerView>(R.id.rv_categories)
        val etNewCategory = view.findViewById<TextInputEditText>(R.id.et_new_category)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btn_add_category)
        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layout_empty_categories)

        val adapter = CategoryAdapter { category ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Delete \"${category.name}\"? Existing expenses using this category will keep their label.")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteCategory(category)
                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        rvCategories.layoutManager = LinearLayoutManager(context)
        rvCategories.adapter = adapter

        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
            layoutEmpty.visibility = if (categories.isNullOrEmpty()) View.VISIBLE else View.GONE
            rvCategories.visibility = if (categories.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        btnAdd.setOnClickListener {
            val name = etNewCategory.text.toString().trim()
            if (name.isNotBlank()) {
                viewModel.insertCategory(Category(name = name))
                etNewCategory.text?.clear()
            } else {
                Toast.makeText(context, "Enter a category name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class CategoryAdapter(private val onDeleteClick: (Category) -> Unit) :
        ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback()) {

        private val categoryColors = mapOf(
            "Food" to "#FF6B35",
            "Transport" to "#1B98E0",
            "Shopping" to "#E84393",
            "Entertainment" to "#A855F7",
            "Health" to "#10B981",
            "Others" to "#64748B"
        )

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val tvName: TextView = view.findViewById(R.id.tv_category_name)
            private val colorDot: View = view.findViewById(R.id.view_color_dot)
            private val btnDelete: MaterialButton = view.findViewById(R.id.btn_delete_category)

            fun bind(category: Category) {
                tvName.text = category.name

                // Color dot
                val color = if (category.colorHex != "#000000") category.colorHex
                    else categoryColors[category.name] ?: "#64748B"
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.OVAL
                drawable.setColor(Color.parseColor(color))
                colorDot.background = drawable

                btnDelete.setOnClickListener { onDeleteClick(category) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class DiffCallback : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
        }
    }
}
