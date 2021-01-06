package com.example.tabyspartner.adapter

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.tabyspartner.R
import com.example.tabyspartner.model.CreditCard
import com.example.tabyspartner.ui.ui.withdraw.CardFormActivity
import com.example.tabyspartner.ui.ui.withdraw.WithDrawFragment
import com.example.tabyspartner.utils.DatabaseHandler
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.card_item.view.*


class CreditCardAdapter(

    private val items: MutableList<CreditCard> = mutableListOf(),
    private val onItemClick: (CreditCard) -> Unit,
//    private val onDelete: (CreditCard) -> Unit
    private val databaseHandler: DatabaseHandler,
    private val context: Context?,
    private val fragment: WithDrawFragment
) : RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder>() {
    private val limit = 3

    inner class CreditCardViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("RestrictedApi")
        @RequiresApi(Build.VERSION_CODES.P)
        fun bindItem(item: CreditCard) {
            view.card_item_name.text = item.creditCardName
            view.card_item_number.text = item.creditCardNumber
            view.deleteCardBtn.setOnClickListener {
                MaterialAlertDialogBuilder(context!!)
                    .setTitle(context.resources.getString(R.string.delete_card_title))
                    .setMessage(context.resources.getString(R.string.delete_card_message))
                    .setNegativeButton(context.resources.getString(R.string.decline)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(context.resources.getString(R.string.accept)) { dialog, which ->
                        databaseHandler.deleteCardItem(item.id)
//                        fragment.requireActivity().fr
                    }
                    .show()
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
        bundle.putString("card_name", cardItem.creditCardName)
        bundle.putString("card_number", cardItem.creditCardNumber)
        val cardFormActivity = CardFormActivity()
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