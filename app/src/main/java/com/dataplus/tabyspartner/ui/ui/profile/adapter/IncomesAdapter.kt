package com.dataplus.tabyspartner.ui.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dataplus.tabyspartner.R

class IncomesAdapter(private val data: List<Pair<String, String>>) : RecyclerView.Adapter<IncomeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = IncomeHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_income, parent, false)
    )

    override fun onBindViewHolder(holder: IncomeHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

}

class IncomeHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val sum: TextView = view.findViewById(R.id.sum)
    private val period: TextView = view.findViewById(R.id.period)

    fun bind(item: Pair<String, String>) {
        period.text = item.first
        sum.text = itemView.context.getString(R.string.income_sum, item.second)
    }
}