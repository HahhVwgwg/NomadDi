package com.example.tabyspartner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabyspartner.R
import com.example.tabyspartner.model.CreditCard
import kotlinx.android.synthetic.main.card_item.view.*

class CreditCardAdapter (

        private val items: List<CreditCard> = listOf(),
        private val onItemClick: (CreditCard) -> Unit
) : RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder>(){
    private val limit = 3
    inner class CreditCardViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(item : CreditCard) {
           view.card_item_name.text = item.creditCardName
            view.card_item_number.text = item.creditCardNumber
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder =
            CreditCardViewHolder( LayoutInflater.from(parent.context)
                    .inflate(R.layout.card_item,parent,false));
    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        holder.bindItem(items[position])
    }
    override fun getItemCount(): Int {
//        return if(items.size > limit){
//            limit;
//        } else {
//            items.size;
//        }
        return items.size
    }
}