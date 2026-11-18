package com.example.erdbeeren

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.erdbeeren.data.SalesWithClient
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class PendingAccountAdapter(private var accounts: List<SalesWithClient>) :
    RecyclerView.Adapter<PendingAccountAdapter.PendingAccountViewHolder>() {

    class PendingAccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        val tvClientName: TextView = itemView.findViewById(R.id.tvClientName)
        val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        val tvPendingAmount: TextView = itemView.findViewById(R.id.tvPendingAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingAccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_account, parent, false)
        return PendingAccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingAccountViewHolder, position: Int) {
        val account = accounts[position]
        val transitionName = "card_transition_${account.sale.id}"
        ViewCompat.setTransitionName(holder.cardView, transitionName)

        holder.tvClientName.text = account.client.clientName
        holder.tvTotalAmount.text = String.format(Locale.getDefault(), "%.1f kg", account.sale.kg)
        holder.tvPendingAmount.text = String.format(Locale.getDefault(), "$%.2f", account.sale.total)

        holder.cardView.setOnClickListener {
            val context = holder.itemView.context
            if (context is Activity) {
                val intent = Intent(context, SaleDetailActivity::class.java).apply {
                    putExtra("SALE_ID", account.sale.id)
                    putExtra("CLIENT_NAME", account.client.clientName)
                    putExtra("PHONE", account.client.phoneNumber)
                    putExtra("KG", account.sale.kg)
                    putExtra("PRICE", account.sale.price)
                    putExtra("TOTAL", account.sale.total)
                    putExtra("PENDING", account.sale.pending)
                    putExtra("TRANSITION_NAME", transitionName)
                }

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context,
                    holder.cardView,
                    transitionName
                )
                context.startActivity(intent, options.toBundle())
            }
        }
    }

    override fun getItemCount(): Int = accounts.size

    fun updateAccounts(newAccounts: List<SalesWithClient>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }
}