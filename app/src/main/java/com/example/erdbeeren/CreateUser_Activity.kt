package com.example.erdbeeren

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.erdbeeren.data.AppDatabase
import com.example.erdbeeren.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateUser_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSaveUser = findViewById<Button>(R.id.btnSaveUser)
        val btnShowUsers = findViewById<Button>(R.id.btnShowUsers)
        val edUsername = findViewById<EditText>(R.id.edUsername)
        val edPassword = findViewById<EditText>(R.id.edPassword)
        val edConfirmPassword = findViewById<EditText>(R.id.edConfirmPassword)

        btnSaveUser.setOnClickListener {
            val username = edUsername.text.toString().trim()
            val password = edPassword.text.toString()
            val confirmPassword = edConfirmPassword.text.toString()

            // 1. Validar campos vacíos
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Validar coincidencia de contraseñas
            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Guardar usuario en Room y notificar el resultado
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val db = AppDatabase.getInstance(applicationContext)
                    db.userDao().insertUser(
                        UserEntity(username = username, password = password)
                    )

                    // Volver al hilo principal para notificar éxito y limpiar campos
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CreateUser_Activity,
                            "¡Usuario creado con éxito!",
                            Toast.LENGTH_SHORT
                        ).show()

                        edUsername.text.clear()
                        edPassword.text.clear()
                        edConfirmPassword.text.clear()
                    }
                } catch (e: Exception) {
                    // Notificar error en el hilo principal
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CreateUser_Activity,
                            "Error al crear usuario: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        btnShowUsers.setOnClickListener {
            val intent = Intent(this, ShowUsers_Activity::class.java)
            startActivity(intent)
        }
    }
}