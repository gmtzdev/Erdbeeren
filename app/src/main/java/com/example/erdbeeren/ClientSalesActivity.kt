package com.example.erdbeeren

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erdbeeren.data.AppDatabase
import com.example.erdbeeren.data.ClientEntity
import com.example.erdbeeren.data.SalesWithClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClientSalesActivity : AppCompatActivity() {
    private lateinit var adapter: PendingAccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_sales)

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val clientId = intent.getIntExtra("CLIENT_ID", 0)
        val clientName = intent.getStringExtra("CLIENT_NAME") ?: "Cliente"

        findViewById<TextView>(R.id.tvClientSalesTitle).text = "Pendientes de $clientName"

        val rvClientSales = findViewById<RecyclerView>(R.id.rvClientSales)
        rvClientSales.layoutManager = LinearLayoutManager(this)
        adapter = PendingAccountAdapter(emptyList())
        rvClientSales.adapter = adapter

        loadClientSales(clientId, clientName)
    }

    private fun loadClientSales(clientId: Int, clientName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(applicationContext)
            val sales = db.saleDao().getSalesByClientId(clientId)
            val client = db.clientDao().getClientById(clientId) ?: ClientEntity(id = clientId, clientName = clientName)

            val salesWithClient = sales.map { sale ->
                SalesWithClient(
                    sale = sale,
                    client = client
                )
            }

            withContext(Dispatchers.Main) {
                adapter.updateAccounts(salesWithClient)
            }
        }
    }
}