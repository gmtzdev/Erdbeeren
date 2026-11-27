package com.example.erdbeeren

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.erdbeeren.data.AppDatabase
import com.example.erdbeeren.data.ClientEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateClientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_client)
        val mainView = findViewById<View>(R.id.main)
        val initialPaddingLeft = mainView.paddingLeft
        val initialPaddingTop = mainView.paddingTop
        val initialPaddingRight = mainView.paddingRight
        val initialPaddingBottom = mainView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                initialPaddingLeft + systemBars.left,
                initialPaddingTop + systemBars.top,
                initialPaddingRight + systemBars.right,
                initialPaddingBottom + maxOf(systemBars.bottom, ime.bottom)
            )
            insets
        }

        val btnSaveClient = findViewById<Button>(R.id.btnSaveClient)
        val edUsername = findViewById<EditText>(R.id.edClientname)
        val edPhone = findViewById<EditText>(R.id.edPhone)

        val updateButtonState = {
            val isValid = edUsername.text.toString().trim().isNotEmpty() &&
                    edPhone.text.toString().trim().isNotEmpty()
            btnSaveClient.isEnabled = isValid
            if (isValid) {
                btnSaveClient.backgroundTintList = ColorStateList.valueOf(getColor(R.color.red))
                btnSaveClient.setTextColor(getColor(android.R.color.white))
            } else {
                btnSaveClient.backgroundTintList = ColorStateList.valueOf(getColor(R.color.disablered))
                btnSaveClient.setTextColor(getColor(R.color.disabledtext))
            }
        }

        edUsername.doAfterTextChanged { updateButtonState() }
        edPhone.doAfterTextChanged { updateButtonState() }

        btnSaveClient.setOnClickListener {
            val username = edUsername.text.toString().trim()
            val phoneNumber = edPhone.text.toString().trim()

            // 1. Validar campos vacíos
            if (username.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Guardar cliente en Room y notificar el resultado
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val db = AppDatabase.getInstance(applicationContext)
                    db.clientDao().insertClient(
                        ClientEntity(clientName = username, phoneNumber = phoneNumber)
                    )

                    // Volver al hilo principal para notificar éxito, cerrar actividad y regresar
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CreateClientActivity,
                            "¡Cliente creado con éxito!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } catch (e: Exception) {
                    // Notificar error en el hilo principal
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CreateClientActivity,
                            "Error al crear cliente: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}