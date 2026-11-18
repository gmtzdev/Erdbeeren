package com.example.erdbeeren

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.erdbeeren.data.PaymentEntity

class PaymentAdapter(private var payments: List<PaymentEntity> ) :
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
        val payment = payments[position]
        holder.tvAmount.text = "Monto: $${payment.amount}"
        holder.tvUserToPay.text = "Recibe: ${payment.userToPay}"
        holder.tvPaymentDate.text = "Fecha: ${payment.date}"
    }

    override fun getItemCount(): Int = payments.size

    fun updatePayments(newPayments: List<PaymentEntity>){
        payments = newPayments
        notifyDataSetChanged()
    }
}