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

class RegistroActivity : AppCompatActivity() {

    private val cliente = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val nombre = findViewById<EditText>(R.id.edtNombreRegistro)
        val apellido = findViewById<EditText>(R.id.edtApellidoRegistro)
        val correo = findViewById<EditText>(R.id.edtCorreoRegistro)
        val telefono = findViewById<EditText>(R.id.edtTelefonoRegistro)
        val password = findViewById<EditText>(R.id.edtPasswordRegistro)

        val btnCrear = findViewById<Button>(R.id.btnCrearCuenta)
        val btnVolver = findViewById<Button>(R.id.btnVolverLogin)

        btnVolver.setOnClickListener {
            finish()
        }

        btnCrear.setOnClickListener {
            val nom = nombre.text.toString().trim()
            val ape = apellido.text.toString().trim()
            val email = correo.text.toString().trim()
            val tel = telefono.text.toString().trim()
            val clave = password.text.toString()

            if (nom.isEmpty() || ape.isEmpty() ||
                email.isEmpty() || clave.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Completa nombre, apellido, correo y contraseña",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (clave.length < 6) {
                password.error = "Usa al menos 6 caracteres"
                return@setOnClickListener
            }

            btnCrear.isEnabled = false

            lifecycleScope.launch {
                try {
                    val respuesta = withContext(Dispatchers.IO) {
                        val datos = JSONObject().apply {
                            put("nombre", nom)
                            put("apellido", ape)
                            put("correo", email)
                            put("password", clave)
                            put("telefono", tel.ifEmpty { JSONObject.NULL })
                        }

                        val cuerpo = datos.toString().toRequestBody(
                            "application/json; charset=utf-8".toMediaType()
                        )

                        val solicitud = Request.Builder()
                            .url("http://10.154.26.214:5065/api/Usuarios/registro")
                            .post(cuerpo)
                            .build()

                        cliente.newCall(solicitud).execute().use { resp ->
                            val texto = resp.body?.string().orEmpty()

                            if (!resp.isSuccessful) {
                                val mensaje = try {
                                    val json = JSONObject(texto)
                                    json.optString(
                                        "mensaje",
                                        json.optString(
                                            "title",
                                            "No se pudo crear la cuenta. Verifica los datos."
                                        )
                                    )
                                } catch (_: Exception) {
                                    "No se pudo crear la cuenta. Verifica los datos."
                                }

                                throw Exception(mensaje)
                            }

                            texto
                        }
                    }

                    Toast.makeText(
                        this@RegistroActivity,
                        "Cuenta creada correctamente. Ya puedes iniciar sesión.",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(
                        this@RegistroActivity,
                        LoginActivity::class.java
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegistroActivity,
                        e.message ?: "Error de conexión con el servidor",
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    btnCrear.isEnabled = true
                }
            }
        }
    }
}