package com.example.erdbeeren

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.erdbeeren.core.DateFormatter
import com.example.erdbeeren.data.PaymentWithUser

class PaymentAdapter(private var payments: List<PaymentWithUser>) :
    RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>(){

    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAmount =  itemView.findViewById<TextView>(R.id.tvAmount)
        val tvUserToPay = itemView.findViewById<TextView>(R.id.tvUserToPay)
        val tvPaymentDate = itemView.findViewById<TextView>(R.id.tvPaymentDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PaymentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int){
        val paymentWithUser = payments[position]
        holder.tvAmount.text = "Monto: $${paymentWithUser.payment.amount}"
        holder.tvUserToPay.text = "Recibe: ${paymentWithUser.user.username}"

        val formattedDate = DateFormatter.format(paymentWithUser.payment.date)
        holder.tvPaymentDate.text = "Fecha: $formattedDate"
    }

    override fun getItemCount(): Int = payments.size

    fun updatePayments(newPayments: List<PaymentWithUser>){
        payments = newPayments
        notifyDataSetChanged()
    }
}