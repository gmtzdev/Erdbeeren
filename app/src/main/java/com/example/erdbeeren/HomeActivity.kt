package com.example.erdbeeren

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erdbeeren.core.SessionManager
import com.example.erdbeeren.data.AppDatabase
import com.example.erdbeeren.data.ClientEntity
import com.example.erdbeeren.data.PendingAccount
import com.example.erdbeeren.data.SaleEntity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: FrameLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        fragmentContainer = findViewById(R.id.fragmentContainer)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showHomeSection()
                    true
                }
                R.id.nav_sales -> {
                    showSalesSection()
                    true
                }
                R.id.nav_clients -> {
                    showClientsSection()
                    true
                }
                R.id.nav_settings -> {
                    showSettingsSection()
                    true
                }
                else -> false
            }
        }

        // Cargar vista inicial (Inicio)
        showHomeSection()
    }

    private fun showHomeSection() {
        fragmentContainer.removeAllViews()
        val view = LayoutInflater.from(this).inflate(R.layout.layout_home_content, fragmentContainer, false)

        val rvPendingAccounts = view.findViewById<RecyclerView>(R.id.rvPendingAccounts)
        rvPendingAccounts.layoutManager = LinearLayoutManager(this)

        // Consultamos las ventas
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(applicationContext)
            val salesWithClient = db.saleDao().getSalesWithClient()

            withContext(Dispatchers.Main) {
                val sampleData = salesWithClient

                rvPendingAccounts.adapter = PendingAccountAdapter(sampleData)
                fragmentContainer.addView(view)
            }
        }
    }

    private fun showSalesSection() {
        fragmentContainer.removeAllViews()
        val view = LayoutInflater.from(this).inflate(R.layout.layout_sales, fragmentContainer, false)

        val chipGroupSaleType = view.findViewById<ChipGroup>(R.id.chipGroupSaleType)
        val tilKg = view.findViewById<TextInputLayout>(R.id.tilKg)
        val actvClient = view.findViewById<MaterialAutoCompleteTextView>(R.id.actvSalesClient)
        val etKg = view.findViewById<TextInputEditText>(R.id.etKg)
        val etPrice = view.findViewById<TextInputEditText>(R.id.etPrice)
        val btnRegister = view.findViewById<Button>(R.id.btnRegisterSale)
        val txtTotal = view.findViewById<TextView>(R.id.txtTotal)

        var selectedClient: ClientEntity? = null
        var currentSaleType = "KILOS"

        // Manejar el cambio de tipo de venta en el menú con scroll horizontal
        chipGroupSaleType.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedId = checkedIds.firstOrNull()
            when (selectedId) {
                R.id.chipSaleGeneral -> {
                    currentSaleType = "KILOS"
                    tilKg.hint = "Fresa (Kg)"
                }
                R.id.chipSaleBox -> {
                    currentSaleType = "CAJA"
                    tilKg.hint = "Cantidad Cajas"
                }
                R.id.chipSaleWholesale -> {
                    currentSaleType = "MAYOREO"
                    tilKg.hint = "Lotes / Toneladas"
                }
                R.id.chipSaleCredit -> {
                    currentSaleType = "CREDITO"
                    tilKg.hint = "Fresa (Kg)"
                }
                R.id.chipSaleSpecial -> {
                    currentSaleType = "ESPECIAL"
                    tilKg.hint = "Cantidad Especial"
                }
            }
        }

        // Cargar los objetos ClientEntity desde la base de datos Room
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(applicationContext)
            val clients = db.clientDao().getAllClients()

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@HomeActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    clients
                )
                actvClient.setAdapter(adapter)
            }
        }

        // Al seleccionar un cliente de la lista, guardamos el objeto exacto con su ID
        actvClient.setOnItemClickListener { parent, _, position, _ ->
            selectedClient = parent.getItemAtPosition(position) as? ClientEntity
        }

        btnRegister.setOnClickListener {
            val kgStr = etKg.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()

            val client = selectedClient
            if (client == null) {
                Snackbar.make(view, "Por favor, selecciona un cliente válido de la lista", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (kgStr.isEmpty() || priceStr.isEmpty()) {
                Snackbar.make(view, "Por favor, ingresa los datos completos", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val kg = kgStr.toDoubleOrNull() ?: 0.0
            val price = priceStr.toDoubleOrNull() ?: 0.0
            val total = calcTotalPrice(kg, price)

            val sale = SaleEntity(
                clientId = client.id,
                kg = kg,
                price = price,
                total = if (total.isNaN()) 0.0 else total,
                pending = if (total.isNaN()) 0.0 else total
            )

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(applicationContext)
                db.saleDao().insertSale(sale)

                withContext(Dispatchers.Main) {
                    Snackbar.make(view, "¡Venta ($currentSaleType) registrada con éxito para ${client.clientName}!", Snackbar.LENGTH_SHORT).show()
                    actvClient.text?.clear()
                    etKg.text?.clear()
                    etPrice.text?.clear()
                    txtTotal.text = "---"
                    selectedClient = null
                }
            }
        }

        etKg.setOnKeyListener { _, _, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                if (etKg.text.toString().isEmpty() || etPrice.text.toString().isEmpty()) {
                    Log.d("UTNA_TEST", "No entra a calcular")
                    txtTotal.text = "---"
                    return@setOnKeyListener false
                }
                val kg = etKg.text.toString().toDoubleOrNull() ?: 0.0
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val total = calcTotalPrice(kg, price)
                if (!total.isNaN()) {
                    txtTotal.text = total.toString()
                } else {
                    Log.d("UTNA_TEST", "No se calcula")
                }
            }
            false
        }

        etPrice.setOnKeyListener { _, _, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                if (etKg.text.toString().isEmpty() || etPrice.text.toString().isEmpty()) {
                    Log.d("UTNA_TEST", "No entra a calcular")
                    txtTotal.text = "---"
                    return@setOnKeyListener false
                }
                val kg = etKg.text.toString().toDoubleOrNull() ?: 0.0
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val total = calcTotalPrice(kg, price)
                if (!total.isNaN()) {
                    txtTotal.text = total.toString()
                } else {
                    Log.d("UTNA_TEST", "No se calcula")
                }
            }
            false
        }

        fragmentContainer.addView(view)
    }

    private fun calcTotalPrice(kg: Double, price: Double): Double {
        if (kg.isNaN() || price.isNaN()) {
            return Double.NaN
        }
        return kg * price
    }

    private fun showClientsSection() {
        fragmentContainer.removeAllViews()
        val view = LayoutInflater.from(this).inflate(R.layout.layout_clients, fragmentContainer, false)

        val rvClients = view.findViewById<RecyclerView>(R.id.rvClients)
        val fabAddClient = view.findViewById<FloatingActionButton>(R.id.fabAddClient)

        rvClients.layoutManager = LinearLayoutManager(this)
        val clientAdapter = ClientAdapter(emptyList())
        rvClients.adapter = clientAdapter

        // Cargar clientes desde Room DB
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(applicationContext)
            val clientList = db.clientDao().getAllClients()

            withContext(Dispatchers.Main) {
                clientAdapter.updateClients(clientList)
            }
        }

        fabAddClient.setOnClickListener {
            startActivity(Intent(this, CreateClientActivity::class.java))
        }

        fragmentContainer.addView(view)
    }

    private fun showSettingsSection() {
        fragmentContainer.removeAllViews()
        val view = LayoutInflater.from(this).inflate(R.layout.layout_settings, fragmentContainer, false)

        val sessionManager = SessionManager(this)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            lifecycleScope.launch {
                sessionManager.logout()
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        fragmentContainer.addView(view)
    }
}