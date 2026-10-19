package pe.edu.cibertec.barberapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServicioAdapter(
    private val servicios: List<Servicio>
) : RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder>() {

    class ServicioViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val nombre: TextView = itemView.findViewById(R.id.txtNombre)
        val descripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        val precio: TextView = itemView.findViewById(R.id.txtPrecio)
        val duracion: TextView = itemView.findViewById(R.id.txtDuracion)
        val btnReservar: Button = itemView.findViewById(R.id.btnReservar)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_servicio, parent, false)

        return ServicioViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ServicioViewHolder,
        position: Int
    ) {
        val servicio = servicios[position]

        holder.nombre.text = servicio.nombre
        holder.descripcion.text = servicio.descripcion ?: "Sin descripción"
        holder.precio.text = "Precio: S/ %.2f".format(servicio.precio)
        holder.duracion.text = "Duración: ${servicio.duracion} minutos"

        holder.btnReservar.setOnClickListener {
            val contexto = holder.itemView.context

            val intent = Intent(contexto, CrearReservaActivity::class.java)
            intent.putExtra("idServicio", servicio.idServicio)
            intent.putExtra("nombreServicio", servicio.nombre)
            intent.putExtra("precioServicio", servicio.precio)

            contexto.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = servicios.size
}