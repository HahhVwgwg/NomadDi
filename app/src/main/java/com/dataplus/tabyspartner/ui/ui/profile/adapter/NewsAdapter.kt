package com.dataplus.tabyspartner.ui.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.networking.BASE_URL_OWN
import com.dataplus.tabyspartner.networking.NotificationElement
import com.dataplus.tabyspartner.networking.OwnNewsResponse

class NewsAdapter(private val data: List<NotificationElement>) : RecyclerView.Adapter<NewsHolder>() {

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
    private val image: ImageView = view.findViewById(R.id.image)

    fun bind(item: NotificationElement) {
        title.text = item.title
        news.text = item.description
        date.text = item.expiryDate
        if (item.image.isNullOrEmpty()) {
            image.visibility = View.GONE
        } else {
            try {
                image.visibility = View.VISIBLE
                Glide.with(image).load(item.image).optionalCenterCrop().into(image)
            } catch (e: Exception) {
                image.visibility = View.GONE
            }
        }
    }
}