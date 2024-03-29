package com.dataplus.tabyspartner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.model.CreditCard
import com.dataplus.tabyspartner.networking.CardOtp
import com.dataplus.tabyspartner.ui.ui.withdraw.CardFormActivity
import com.dataplus.tabyspartner.utils.DatabaseHandler
import kotlinx.android.synthetic.main.card_item.view.*


class CreditCardAdapter(
    private val items: MutableList<CardOtp> = mutableListOf(),
    private val onItemClick: (CardOtp) -> Unit,
    private val onDelete: (CardOtp) -> Unit,
    private val databaseHandler: DatabaseHandler,
    private val context: Context?,
) : RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder>() {
    private val limit = 3

    inner class CreditCardViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("RestrictedApi", "SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.P)
        fun bindItem(item: CardOtp) {
            view.card_item_name.text = item.cardName
            view.card_item_number.text = item.lastFour
            view.deleteCardBtn.setOnClickListener {
                onDelete(item)
            }
            view.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder =
        CreditCardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_item, parent, false)
        );

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    fun getContext(): Context? {
        return context
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun deleteCard(position: Int) {
        val cardItem = items[position]
        databaseHandler.deleteCardItem(cardItem.id)
        items.remove(cardItem)
        notifyItemRemoved(position)
    }

    public fun editCard(position: Int) {
        val cardItem = items[position]
        val bundle = Bundle()
        bundle.putString("card_name", cardItem.cardName)
        bundle.putString("card_number", cardItem.lastFour)
        val cardFormActivity = CardFormActivity()
    }

    override fun getItemCount(): Int {
        return items.size
    }
}