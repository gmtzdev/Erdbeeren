package com.example.erdbeeren

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.erdbeeren.data.AppDatabase
import com.example.erdbeeren.data.PaymentEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SaleDetailActivity : AppCompatActivity() {
    private lateinit var adapter: PaymentAdapter
    private var currentSaleId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sale_detail)

        val mainView = findViewById<View>(R.id.main)
        val transitionName = intent.getStringExtra("TRANSITION_NAME")
        if (transitionName != null) {
            ViewCompat.setTransitionName(mainView, transitionName)
        }

        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(mainView)
            duration = 350L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(mainView)
            duration = 300L
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, maxOf(systemBars.bottom, ime.bottom))
            insets
        }

        val saleId = intent.getIntExtra("SALE_ID", 0)
        currentSaleId = saleId
        val clientName = intent.getStringExtra("CLIENT_NAME") ?: "Cliente"
        val phone = intent.getStringExtra("PHONE") ?: ""
        val kg = intent.getDoubleExtra("KG", 0.0)
        val price = intent.getDoubleExtra("PRICE", 0.0)
        val total = intent.getDoubleExtra("TOTAL", 0.0)
        var pending = intent.getDoubleExtra("PENDING", 0.0)

        findViewById<TextView>(R.id.tvDetailClientName).text = clientName
        findViewById<TextView>(R.id.tvDetailPhone).text = if (phone.isNotEmpty()) "Tel: $phone" else "Sin teléfono registrado"
        findViewById<TextView>(R.id.tvDetailKg).text = String.format(Locale.getDefault(), "%.1f kg", kg)
        findViewById<TextView>(R.id.tvDetailPrice).text = String.format(Locale.getDefault(), "$%.2f", price)
        findViewById<TextView>(R.id.tvDetailTotal).text = String.format(Locale.getDefault(), "$%.2f", total)
        findViewById<TextView>(R.id.tvDetailPending).text = String.format(Locale.getDefault(), "$%.2f", pending)

        findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<Button>(R.id.btnMerma).setOnClickListener {
            showRegisterDecreaseDialog(saleId)
        }

        findViewById<Button>(R.id.btnRegisterPayment).setOnClickListener {
            val etPayment = findViewById<TextInputEditText>(R.id.etPayment)
            val paymentAmount = etPayment.text.toString().toDoubleOrNull() ?: 0.0
            Log.d("HOLA", paymentAmount.toString())
            if (paymentAmount > 0) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(applicationContext)
                    val newPendingAmount = pending - paymentAmount

                    db.saleDao().updatePendingAmount(saleId, newPendingAmount)
                    db.paymentDao().insertPayment(PaymentEntity(saleId = saleId, userToPay = 1, amount = paymentAmount))

                    withContext(Dispatchers.Main) {
                        Log.d("HOLA", "Actualiza: $paymentAmount a $newPendingAmount")
                        findViewById<TextView>(R.id.tvDetailPending).text = String.format(Locale.getDefault(), "$%.2f", newPendingAmount)
                        pending = newPendingAmount
                        etPayment.text?.clear()
                        loadPayments(saleId)
                    }
                }
            }
        }

        val rvPayments = findViewById<RecyclerView>(R.id.rvPayments)
        rvPayments.layoutManager = LinearLayoutManager(this)
        adapter = PaymentAdapter(emptyList())
        rvPayments.adapter = adapter

        loadSaleData(saleId)
        loadPayments(saleId)
    }

    private fun loadSaleData(saleId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(applicationContext)
            val saleWithClient = db.saleDao().getSaleWithClientById(saleId)
            if (saleWithClient != null) {
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.tvDetailDecrease).text =
                        String.format(Locale.getDefault(), "%.1f", saleWithClient.sale.decrease)
                }
            }
        }
    }

    private fun showRegisterDecreaseDialog(saleId: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register_decrease, null)
        val etDecrease = dialogView.findViewById<TextInputEditText>(R.id.etDecreaseAmount)

        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val decreaseAmount = etDecrease.text.toString().toDoubleOrNull() ?: 0.0
                if (decreaseAmount > 0) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val db = AppDatabase.getInstance(applicationContext)
                        db.saleDao().updateDecreaseAmount(saleId, decreaseAmount)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@SaleDetailActivity,
                                "Merma registrada con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadSaleData(saleId)
                        }
                    }
                } else {
                    Toast.makeText(this, "Por favor ingresa una merma válida", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loadPayments(saleId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getInstance(applicationContext)
            val payments = db.paymentDao().getPaymentsWithUserBySale(saleId)
            withContext(Dispatchers.Main) {
                adapter.updatePayments(payments)
            }
        }
    }
}