package pe.edu.cibertec.barberapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val cliente = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val correo = findViewById<EditText>(R.id.edtCorreoLogin)
        val password = findViewById<EditText>(R.id.edtPasswordLogin)
        val btnLogin = findViewById<Button>(R.id.btnIniciarSesion)
        val btnRegistro = findViewById<Button>(R.id.btnIrRegistro)

        btnLogin.setOnClickListener {
            val email = correo.text.toString().trim()
            val clave = password.text.toString()

            if (email.isEmpty() || clave.isEmpty()) {
                Toast.makeText(
                    this,
                    "Ingresa tu correo y contraseña",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            lifecycleScope.launch {
                try {
                    val respuesta = withContext(Dispatchers.IO) {
                        val datos = JSONObject().apply {
                            put("correo", email)
                            put("password", clave)
                        }

                        val cuerpo = datos.toString().toRequestBody(
                            "application/json; charset=utf-8".toMediaType()
                        )

                        val solicitud = Request.Builder()
                            .url("http://10.154.26.214:5065/api/Usuarios/login")
                            .post(cuerpo)
                            .build()

                        cliente.newCall(solicitud).execute().use { resp ->
                            val texto = resp.body?.string().orEmpty()

                            if (!resp.isSuccessful) {
                                val mensaje = try {
                                    JSONObject(texto).optString(
                                        "mensaje",
                                        "Correo o contraseña incorrectos"
                                    )
                                } catch (_: Exception) {
                                    "Correo o contraseña incorrectos"
                                }

                                throw Exception(mensaje)
                            }

                            JSONObject(texto)
                        }
                    }

                    val idUsuario = respuesta.getInt("idUsuario")
                    val nombre = respuesta.optString("nombre", "Cliente")

                    getSharedPreferences(
                        "BarberAppSesion",
                        MODE_PRIVATE
                    ).edit()
                        .putInt("idUsuario", idUsuario)
                        .putString("nombre", nombre)
                        .putString("correo", email)
                        .putString("rol", respuesta.optString("rol", "CLIENTE"))
                        .apply()

                    Toast.makeText(
                        this@LoginActivity,
                        "Bienvenido, $nombre",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(
                            this@LoginActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        e.message ?: "Error de conexión",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    btnLogin.isEnabled = true
                }
            }
        }

        btnRegistro.setOnClickListener {
            startActivity(
                Intent(this, RegistroActivity::class.java)
            )
        }
    }
}