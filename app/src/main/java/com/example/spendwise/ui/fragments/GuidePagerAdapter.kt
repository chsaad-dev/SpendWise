package com.example.spendwise.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwise.R

data class GuideItem(val title: String, val description: String, val iconRes: Int)

class GuidePagerAdapter(private val items: List<GuideItem>) :
    RecyclerView.Adapter<GuidePagerAdapter.GuideViewHolder>() {

    class GuideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.iv_guide_icon)
        val tvTitle: TextView = view.findViewById(R.id.tv_guide_title)
        val tvDesc: TextView = view.findViewById(R.id.tv_guide_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guide_page, parent, false)
        return GuideViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvDesc.text = item.description
        
        // As a fallback simply styling the icon since we don't know the exact drawable names yet
        // In a real scenario we'd use item.iconRes. For now, we can use the default Android sym launcher icon
        holder.ivIcon.setImageResource(item.iconRes) 
    }

    override fun getItemCount(): Int = items.size
}
