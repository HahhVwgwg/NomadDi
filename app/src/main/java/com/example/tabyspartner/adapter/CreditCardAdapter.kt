package com.example.tabyspartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabyspartner.R
import com.example.tabyspartner.model.CreditCard
import kotlinx.android.synthetic.main.credit_card_item.view.*

class CreditCardAdapter (
        private val items: List<CreditCard> = listOf(),
        private val onItemClick: (CreditCard) -> Unit
) : RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder>(){
    private val limit = 1
    inner class CreditCardViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(item : CreditCard) {
           view.credit_card_name.text = item.creditCardName
           view.credit_card_number.text = item.creditCardNumber
            view.setOnClickListener {
                onItemClick(item)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder =
            CreditCardViewHolder( LayoutInflater.from(parent.context)
                    .inflate(R.layout.credit_card_item,parent,false));
    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        holder.bindItem(items[position])
    }
    override fun getItemCount(): Int {
        return if(items.size > limit){
            limit;
        } else {
            items.size;
        }
    }
}