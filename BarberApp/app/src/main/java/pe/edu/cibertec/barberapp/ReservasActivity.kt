
package pe.edu.cibertec.barberapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import pe.edu.cibertec.barberapp.databinding.ActivityReservasBinding

class ReservasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReservasBinding
    private lateinit var databaseHelper: BarberDatabaseHelper

    private val cliente = OkHttpClient()

    private val urlApi =
        "http://10.154.26.214:5065/api/Reservas"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReservasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = BarberDatabaseHelper(this)

        binding.recyclerReservas.layoutManager =
            LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()

        if (::binding.isInitialized) {
            cargarReservas()
        }
    }

    private fun cargarReservas() {
        val preferencias =
            getSharedPreferences("BarberAppSesion", MODE_PRIVATE)

        val idUsuario = preferencias.getInt("idUsuario", 0)

        if (idUsuario <= 0) {
            Toast.makeText(
                this,
                "Inicia sesión para consultar tus reservas",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val solicitud = Request.Builder()
            .url(urlApi)
            .get()
            .build()

        cliente.newCall(solicitud).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    mostrarReservasLocales(
                        idUsuario,
                        "Sin conexión con la API. Mostrando reservas guardadas en SQLite."
                    )
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { respuesta ->

                    if (!respuesta.isSuccessful) {
                        runOnUiThread {
                            mostrarReservasLocales(
                                idUsuario,
                                "La API no respondió correctamente. Mostrando datos locales."
                            )
                        }
                        return
                    }

                    try {
                        val contenido = respuesta.body?.string()
                            ?: throw IOException("Respuesta vacía")

                        val json = JSONArray(contenido)
                        val reservas = mutableListOf<Reserva>()

                        for (i in 0 until json.length()) {
                            val item = json.getJSONObject(i)

                            if (item.optInt("idUsuario") != idUsuario) {
                                continue
                            }

                            val usuario = item.optJSONObject("usuario")
                            val servicio = item.optJSONObject("servicio")

                            val partesNombre = listOfNotNull(
                                usuario?.optString("nombre")
                                    ?.takeIf {
                                        it.isNotBlank() && it != "null"
                                    },
                                usuario?.optString("apellido")
                                    ?.takeIf {
                                        it.isNotBlank() && it != "null"
                                    }
                            )

                            val nombreCliente =
                                partesNombre.joinToString(" ")
                                    .ifBlank { "Cliente" }

                            val nombreServicio =
                                servicio?.optString("nombre")
                                    ?.takeIf {
                                        it.isNotBlank() && it != "null"
                                    } ?: "Servicio no disponible"

                            val idReserva = item.getInt("idReserva")
                            val fecha = item.optString("fecha", "")
                            val hora = item.optString("hora", "")
                            val estado = item.optString(
                                "estado",
                                "PENDIENTE"
                            )

                            val reserva = Reserva(
                                idReserva = idReserva,
                                nombreCliente = nombreCliente,
                                nombreServicio = nombreServicio,
                                fecha = fecha,
                                hora = hora,
                                estado = estado
                            )

                            reservas.add(reserva)

                            // Guardar o actualizar la reserva en SQLite.
                            databaseHelper.guardarReserva(
                                idReserva = idReserva,
                                idUsuario = idUsuario,
                                nombreCliente = nombreCliente,
                                nombreServicio = nombreServicio,
                                fecha = fecha,
                                hora = hora,
                                estado = estado
                            )
                        }

                        runOnUiThread {
                            mostrarLista(reservas)

                            if (reservas.isEmpty()) {
                                Toast.makeText(
                                    this@ReservasActivity,
                                    "No tienes reservas registradas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    } catch (e: Exception) {
                        runOnUiThread {
                            mostrarReservasLocales(
                                idUsuario,
                                "Error al leer la API. Consultando SQLite."
                            )
                        }
                    }
                }
            }
        })
    }

    private fun mostrarReservasLocales(
        idUsuario: Int,
        mensaje: String
    ) {
        try {
            val reservas = databaseHelper.obtenerReservas(idUsuario)

            mostrarLista(reservas)

            Toast.makeText(
                this,
                if (reservas.isEmpty()) {
                    "$mensaje No hay reservas guardadas todavía."
                } else {
                    mensaje
                },
                Toast.LENGTH_LONG
            ).show()

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "No se pudieron consultar las reservas locales: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun mostrarLista(reservas: List<Reserva>) {
        binding.recyclerReservas.adapter =
            ReservaAdapter(reservas) { reserva ->
                cancelarReserva(reserva.idReserva)
            }
    }

    private fun cancelarReserva(idReserva: Int) {
        val cuerpo = "{}".toRequestBody(
            "application/json; charset=utf-8".toMediaType()
        )

        val solicitud = Request.Builder()
            .url("$urlApi/$idReserva/cancelar")
            .put(cuerpo)
            .build()

        cliente.newCall(solicitud).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@ReservasActivity,
                        "No se pudo cancelar: comprueba la conexión con la API",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { respuesta ->
                    runOnUiThread {
                        if (respuesta.isSuccessful) {
                            Toast.makeText(
                                this@ReservasActivity,
                                "Reserva cancelada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            cargarReservas()
                        } else {
                            Toast.makeText(
                                this@ReservasActivity,
                                "No se pudo cancelar. Código: ${respuesta.code}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::databaseHelper.isInitialized) {
            databaseHelper.close()
        }
    }
}