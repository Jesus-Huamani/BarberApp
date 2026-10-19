package pe.edu.cibertec.barberapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import pe.edu.cibertec.barberapp.databinding.ActivityCrearReservaBinding

class CrearReservaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearReservaBinding
    private val cliente = OkHttpClient()

    private val urlApi = "http://10.154.26.214:5065/api/Reservas"

    private val calendario = Calendar.getInstance()
    private var fechaSeleccionada = false
    private var horaSeleccionada = false

    private var idServicio = 0
    private var nombreServicio = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCrearReservaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idServicio = intent.getIntExtra("idServicio", 0)
        nombreServicio = intent.getStringExtra("nombreServicio") ?: ""

        binding.txtServicioReserva.text = nombreServicio

        binding.btnSeleccionarFecha.setOnClickListener {
            seleccionarFecha()
        }

        binding.btnSeleccionarHora.setOnClickListener {
            seleccionarHora()
        }

        binding.btnConfirmarReserva.setOnClickListener {
            crearReserva()
        }
    }

    private fun seleccionarFecha() {
        val hoy = Calendar.getInstance()

        val dialogo = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendario.set(year, month, dayOfMonth)
                fechaSeleccionada = true

                val formato = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

                binding.btnSeleccionarFecha.text =
                    formato.format(calendario.time)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )

        dialogo.datePicker.minDate = hoy.timeInMillis
        dialogo.show()
    }

    private fun seleccionarHora() {
        val horaActual = Calendar.getInstance()

        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendario.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendario.set(Calendar.MINUTE, minute)
                horaSeleccionada = true

                binding.btnSeleccionarHora.text =
                    String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            },
            horaActual.get(Calendar.HOUR_OF_DAY),
            horaActual.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun crearReserva() {
        if (idServicio <= 0) {
            Toast.makeText(this, "No se identificó el servicio", Toast.LENGTH_LONG).show()
            return
        }

        if (!fechaSeleccionada) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show()
            return
        }

        if (!horaSeleccionada) {
            Toast.makeText(this, "Selecciona una hora", Toast.LENGTH_SHORT).show()
            return
        }

        val preferencias = getSharedPreferences("BarberAppSesion", MODE_PRIVATE)
        val idUsuario = preferencias.getInt("idUsuario", 0)

        if (idUsuario <= 0) {
            Toast.makeText(
                this,
                "No se encontró tu sesión. Inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val formatoFecha = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val formatoHora = SimpleDateFormat(
            "HH:mm:ss",
            Locale.US
        )

        val fecha = formatoFecha.format(calendario.time)
        val hora = formatoHora.format(calendario.time)

        val datos = JSONObject().apply {
            put("idUsuario", idUsuario)
            put("idServicio", idServicio)
            put("fecha", "${fecha}T00:00:00")
            put("hora", hora)
            put("estado", "PENDIENTE")
        }

        binding.btnConfirmarReserva.isEnabled = false
        binding.btnConfirmarReserva.text = "Enviando..."

        lifecycleScope.launch {
            try {
                val respuesta = withContext(Dispatchers.IO) {
                    val cuerpo = datos.toString().toRequestBody(
                        "application/json; charset=utf-8".toMediaType()
                    )

                    val solicitud = Request.Builder()
                        .url(urlApi)
                        .post(cuerpo)
                        .build()

                    cliente.newCall(solicitud).execute().use { response ->
                        val contenido = response.body?.string().orEmpty()

                        if (!response.isSuccessful) {
                            throw IOException(
                                "Error ${response.code}: $contenido"
                            )
                        }

                        contenido
                    }
                }

                Toast.makeText(
                    this@CrearReservaActivity,
                    "¡Reserva creada correctamente!",
                    Toast.LENGTH_LONG
                ).show()

                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@CrearReservaActivity,
                    "No se pudo crear la reserva: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {
                binding.btnConfirmarReserva.isEnabled = true
                binding.btnConfirmarReserva.text = "CONFIRMAR RESERVA"
            }
        }
    }
}