package com.dataplus.tabyspartner.ui.ui.withdraw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.networking.OwnWithdrawResponse
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val data: List<OwnWithdrawResponse>, private val mode: Int) :
    RecyclerView.Adapter<HistoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HistoryHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false),
        mode
    )

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

}

class HistoryHolder(view: View, private val mode: Int) : RecyclerView.ViewHolder(view) {

    private val sum: TextView = view.findViewById(R.id.sum)
    private val date: TextView = view.findViewById(R.id.date)
    private val card: TextView = view.findViewById(R.id.card)
    private val account: TextView = view.findViewById(R.id.account)

    fun bind(item: OwnWithdrawResponse) {
        card.text = itemView.context.getString(R.string.history_card_mask, item.card)
        account.text =
            itemView.context.getString(if (mode == 0) R.string.history_account else R.string.history_account_partner)
        date.text = SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault()
        ).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(item.create_at ?: "2021-01-01 13:00:00") ?: Date())
        sum.text = itemView.context.getString(R.string.income_sum, item.summa)
    }
}