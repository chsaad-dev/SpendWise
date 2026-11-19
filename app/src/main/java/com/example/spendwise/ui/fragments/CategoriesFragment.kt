package com.example.spendwise.ui.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R
import com.example.spendwise.data.Category
import com.example.spendwise.ui.dialogs.CategoryBudgetBottomSheet
import com.example.spendwise.ui.dialogs.DeleteCategoryBottomSheet
import com.example.spendwise.ui.viewmodel.MainViewModel
import com.example.spendwise.utils.CurrencyHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CategoriesFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var categoryAdapter: CategoryAdapter? = null

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

        val adapter = CategoryAdapter(
            onDeleteClick = { category ->
                DeleteCategoryBottomSheet.newInstance(category) {
                    viewModel.deleteCategory(category)
                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show()
                }.show(childFragmentManager, DeleteCategoryBottomSheet.TAG)
            },
            onEditClick = { category ->
                CategoryBudgetBottomSheet.newInstance(
                    category = category,
                    onSave = { value ->
                        viewModel.updateCategory(category.copy(monthlyBudget = value))
                        Toast.makeText(context, "Budget saved", Toast.LENGTH_SHORT).show()
                    },
                    onRemove = {
                        viewModel.updateCategory(category.copy(monthlyBudget = 0.0))
                        Toast.makeText(context, "Budget limit removed", Toast.LENGTH_SHORT).show()
                    }
                ).show(childFragmentManager, CategoryBudgetBottomSheet.TAG)
            }
        )

        categoryAdapter = adapter
        rvCategories.layoutManager = LinearLayoutManager(context)
        rvCategories.adapter = adapter

        fun updateEmptyState(categories: List<Category>?) {
            val isLoaded = viewModel.isInitialDataLoaded.value == true
            val isEmpty = categories.isNullOrEmpty()
            layoutEmpty.visibility = if (isLoaded && isEmpty) View.VISIBLE else View.GONE
            rvCategories.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }

        viewModel.isInitialDataLoaded.observe(viewLifecycleOwner) {
            updateEmptyState(viewModel.allCategories.value)
        }

        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            if (categories != null) {
                viewModel.markInitialDataLoaded()
            }
            adapter.submitList(categories)
            updateEmptyState(categories)
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

    override fun onResume() {
        super.onResume()
        categoryAdapter?.notifyDataSetChanged()
    }

    class CategoryAdapter(
        private val onDeleteClick: (Category) -> Unit,
        private val onEditClick: (Category) -> Unit
    ) :
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
            private val tvBudget: TextView = view.findViewById(R.id.tv_category_budget)
            private val badgeLayout: FrameLayout = view.findViewById(R.id.layout_category_badge)
            private val ivIcon: ImageView = view.findViewById(R.id.iv_category_icon)
            private val colorDot: View? = view.findViewById(R.id.view_color_dot)
            private val btnDelete: MaterialButton = view.findViewById(R.id.btn_delete_category)

            fun bind(category: Category) {
                tvName.text = category.name

                // Category color
                val color = if (category.colorHex != "#000000" && category.colorHex.isNotBlank()) category.colorHex
                else categoryColors[category.name] ?: "#64748B"
                val parsedColor = try {
                    Color.parseColor(color)
                } catch (e: Exception) {
                    Color.parseColor("#64748B")
                }

                // 15% opacity rounded square badge (40dp x 40dp, 12dp corner radius)
                val badgeBg = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = itemView.context.resources.displayMetrics.density * 12f
                    val alpha15 = (255 * 0.15).toInt()
                    val colorWithAlpha = Color.argb(
                        alpha15,
                        Color.red(parsedColor),
                        Color.green(parsedColor),
                        Color.blue(parsedColor)
                    )
                    setColor(colorWithAlpha)
                }
                badgeLayout.background = badgeBg

                // Crisp category vector icon
                val iconRes = when (category.name.lowercase().trim()) {
                    "food" -> R.drawable.ic_cat_food
                    "transport" -> R.drawable.ic_cat_transport
                    "shopping" -> R.drawable.ic_cat_shopping
                    "entertainment" -> R.drawable.ic_cat_entertainment
                    "health" -> R.drawable.ic_cat_health
                    else -> R.drawable.ic_cat_others
                }
                ivIcon.setImageResource(iconRes)
                ivIcon.setColorFilter(parsedColor)

                // Optional legacy dot compatibility
                colorDot?.visibility = View.GONE

                // Interactive Budget Pill Badge
                if (category.monthlyBudget > 0) {
                    val fmt = CurrencyHelper.getCurrencyFormat(itemView.context)
                    val formatted = if (category.monthlyBudget % 1.0 == 0.0) {
                        fmt.apply { maximumFractionDigits = 0 }.format(category.monthlyBudget)
                    } else {
                        fmt.format(category.monthlyBudget)
                    }
                    tvBudget.text = "$formatted / mo"
                    tvBudget.setBackgroundResource(R.drawable.bg_pill_budget_active)
                    tvBudget.setTextColor(ContextCompat.getColor(itemView.context, R.color.brand_primary))
                    tvBudget.setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    tvBudget.text = "+ Set Budget"
                    tvBudget.setBackgroundResource(R.drawable.bg_pill_budget_empty)
                    tvBudget.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_secondary))
                    tvBudget.setTypeface(null, android.graphics.Typeface.NORMAL)
                }

                itemView.setOnClickListener { onEditClick(category) }
                tvBudget.setOnClickListener { onEditClick(category) }
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
