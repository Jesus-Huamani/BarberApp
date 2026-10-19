package pe.edu.cibertec.barberapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import pe.edu.cibertec.barberapp.databinding.ActivityServiciosBinding

class ServiciosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiciosBinding

    private lateinit var databaseHelper: BarberDatabaseHelper

    private val cliente = OkHttpClient()

    private val urlApi =
        "http://10.154.26.214:5065/api/Servicios"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityServiciosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = BarberDatabaseHelper(this)

        binding.recyclerServicios.layoutManager =
            LinearLayoutManager(this)

        cargarServicios()
    }

    private fun cargarServicios() {
        val solicitud = Request.Builder()
            .url(urlApi)
            .get()
            .build()

        cliente.newCall(solicitud).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mostrarServiciosLocales(
                    "Sin conexión con la API. Mostrando datos locales."
                )
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {

                    if (!it.isSuccessful) {
                        mostrarServiciosLocales(
                            "Error de API. Mostrando datos locales."
                        )
                        return
                    }

                    try {
                        val contenido = it.body?.string()
                            ?: throw IOException("La respuesta está vacía")

                        val json = JSONArray(contenido)
                        val servicios = mutableListOf<Servicio>()

                        for (i in 0 until json.length()) {
                            val item = json.getJSONObject(i)

                            val servicio = Servicio(
                                idServicio = item.getInt("idServicio"),
                                nombre = item.getString("nombre"),
                                descripcion = if (
                                    item.isNull("descripcion")
                                ) {
                                    null
                                } else {
                                    item.getString("descripcion")
                                },
                                precio = item.getDouble("precio"),
                                duracion = item.getInt("duracion"),
                                estado = item.getBoolean("estado")
                            )

                            if (servicio.estado) {
                                servicios.add(servicio)
                            }
                        }

                        // Guardar los servicios recibidos en SQLite
                        for (servicio in servicios) {
                            databaseHelper.guardarServicio(
                                id = servicio.idServicio,
                                nombre = servicio.nombre,
                                descripcion = servicio.descripcion ?: "",
                                precio = servicio.precio,
                                duracion = servicio.duracion,
                                estado = servicio.estado
                            )
                        }

                        runOnUiThread {
                            binding.recyclerServicios.adapter =
                                ServicioAdapter(servicios)

                            if (servicios.isEmpty()) {
                                Toast.makeText(
                                    this@ServiciosActivity,
                                    "No hay servicios disponibles",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ServiciosActivity,
                                    "Servicios guardados en SQLite",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    } catch (e: Exception) {
                        mostrarServiciosLocales(
                            "Error al procesar la API. Consultando SQLite."
                        )
                    }
                }
            }
        })
    }

    private fun mostrarServiciosLocales(mensaje: String) {
        try {
            val servicios = databaseHelper.obtenerServicios()

            runOnUiThread {
                binding.recyclerServicios.adapter =
                    ServicioAdapter(servicios)

                Toast.makeText(
                    this@ServiciosActivity,
                    if (servicios.isEmpty()) {
                        "$mensaje No hay servicios guardados."
                    } else {
                        mensaje
                    },
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(
                    this@ServiciosActivity,
                    "Error al consultar SQLite: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        databaseHelper.close()
        super.onDestroy()
    }
}