package com.dataplus.tabyspartner.ui.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.networking.OwnNewsResponse

class NewsAdapter(private val data: List<OwnNewsResponse>) : RecyclerView.Adapter<NewsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NewsHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
    )

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

}

class NewsHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val title: TextView = view.findViewById(R.id.title)
    private val date: TextView = view.findViewById(R.id.date)
    private val news: TextView = view.findViewById(R.id.text)

    fun bind(item: OwnNewsResponse) {
        title.text = item.zagolovok
        news.text = item.text
        date.text = item.date_in
    }
}