package com.example.erdbeeren

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.erdbeeren.core.SessionManager
import com.example.erdbeeren.data.AppDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Usar applicationContext para evitar retención de contexto de actividad
        sessionManager = SessionManager(applicationContext)

        // Verificar si el usuario ya inició sesión previamente
        lifecycleScope.launch {
            val isLoggedIn = sessionManager.isLoggedIn.first()
            if (isLoggedIn) {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
                return@launch
            }
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(systemBars.bottom, ime.bottom)
            )
            insets
        }

        val btnSingIn = findViewById<Button>(R.id.btnSingIn)
        val usernameEditText = findViewById<EditText>(R.id.etUsername)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scrollView.post {
                    scrollView.smoothScrollTo(0, passwordEditText.bottom)
                }
            }
        }

        btnSingIn.setOnClickListener { view ->
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            // 1. Validar campos vacíos
            if (username.isEmpty() || password.isEmpty()) {
                Snackbar.make(
                    view,
                    "Por favor, ingresa tu usuario y contraseña",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // 2. Verificar credenciales en Room y guardar la sesión
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(applicationContext)
                val user = db.userDao().login(username, password)

                if (user != null) {
                    // Guardar datos de la sesión en DataStore (DESCOMENTADO)
                    sessionManager.saveSession(user.id.toString(), user.username)

                    withContext(Dispatchers.Main) {
                        Snackbar.make(
                            view,
                            "¡Inicio de sesión exitoso! Bienvenido ${user.username}",
                            Snackbar.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Snackbar.make(
                            view,
                            "Usuario o contraseña incorrectos",
                            Snackbar.LENGTH_LONG
                        ).setAction("Crear cuenta") {
                            val intent = Intent(this@MainActivity, CreateUser_Activity::class.java)
                            startActivity(intent)
                        }.show()
                    }
                }
            }
        }
    }
}