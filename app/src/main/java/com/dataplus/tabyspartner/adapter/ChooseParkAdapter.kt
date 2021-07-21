package com.dataplus.tabyspartner.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.networking.ParkElement
import kotlinx.android.synthetic.main.item_choose_park.view.*


class ChooseParkAdapter(
    private val items: MutableList<ParkElement> = mutableListOf(),
) : RecyclerView.Adapter<ChooseParkAdapter.ViewHolder>() {
    private var position = -1

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("RestrictedApi", "SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.P)
        fun bindItem(item: ParkElement, itemPosition: Int) {
            view.park_name.text = item.parkName
            view.checkbox.visibility = if (itemPosition == this@ChooseParkAdapter.position) View.VISIBLE else View.INVISIBLE
            view.setOnClickListener{
                this@ChooseParkAdapter.position = itemPosition
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_choose_park, parent, false)
        );

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], position)
    }

    public fun getParkPositionFleet(): Long {
        if (position == -1)
            throw AssertionError()
        return items[position].fleet
    }

    override fun getItemCount(): Int {
        return items.size
    }
}