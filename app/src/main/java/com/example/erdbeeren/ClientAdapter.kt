package com.example.erdbeeren

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.erdbeeren.data.ClientEntity

class ClientAdapter(private var clients: List<ClientEntity>) :
    RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClientName: TextView = itemView.findViewById(R.id.tvCardClientName)
        val tvClientPhone: TextView = itemView.findViewById(R.id.tvCardClientPhone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client_card, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.tvClientName.text = client.clientName
        holder.tvClientPhone.text = if (client.phoneNumber.isNotEmpty()) {
            "Tel: ${client.phoneNumber}"
        } else {
            "Sin teléfono registrado"
        }
    }

    override fun getItemCount(): Int = clients.size

    fun updateClients(newClients: List<ClientEntity>) {
        clients = newClients
        notifyDataSetChanged()
    }
}