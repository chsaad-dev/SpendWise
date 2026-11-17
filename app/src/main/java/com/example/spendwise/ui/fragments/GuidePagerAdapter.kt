package com.example.spendwise.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R

data class GuideItem(val title: String, val description: String, val iconRes: Int = 0)

class GuidePagerAdapter(private val items: List<GuideItem>? = null) :
    RecyclerView.Adapter<GuidePagerAdapter.SlideViewHolder>() {

    class SlideViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = 3

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val layoutRes = when (viewType) {
            0 -> R.layout.item_onboarding_slide_1
            1 -> R.layout.item_onboarding_slide_2
            2 -> R.layout.item_onboarding_slide_3
            else -> R.layout.item_onboarding_slide_1
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        // Each slide features rich, dedicated XML mini-UI cards and theme tokens
    }
}
