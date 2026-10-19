package pe.edu.cibertec.barberapp

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class ReservaAdapter(
    private val reservas: List<Reserva>,
    private val onCancelar: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val servicio: TextView =
            itemView.findViewById(R.id.txtServicioReserva)

        val cliente: TextView =
            itemView.findViewById(R.id.txtClienteReserva)

        val fecha: TextView =
            itemView.findViewById(R.id.txtFechaReserva)

        val estado: TextView =
            itemView.findViewById(R.id.txtEstadoReserva)

        val btnCancelar: Button =
            itemView.findViewById(R.id.btnCancelarReserva)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)

        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReservaViewHolder,
        position: Int
    ) {
        val reserva = reservas[position]

        holder.servicio.text = reserva.nombreServicio
        holder.cliente.text = "Cliente: ${reserva.nombreCliente}"
        holder.fecha.text =
            "Fecha: ${reserva.fecha} | Hora: ${reserva.hora}"

        val estadoActual = reserva.estado
            .trim()
            .uppercase(Locale.ROOT)

        holder.estado.text = "Estado: $estadoActual"

        val colorFondo: Int
        val colorTexto: Int

        when (estadoActual) {
            "CONFIRMADA" -> {
                colorFondo = Color.rgb(221, 244, 228)
                colorTexto = Color.rgb(33, 110, 57)
            }

            "CANCELADA" -> {
                colorFondo = Color.rgb(252, 228, 228)
                colorTexto = Color.rgb(179, 38, 30)
            }

            else -> {
                colorFondo = Color.rgb(255, 240, 217)
                colorTexto = Color.rgb(138, 99, 62)
            }
        }

        holder.estado.setTextColor(colorTexto)

        val fondo = GradientDrawable()
        fondo.setColor(colorFondo)
        fondo.cornerRadius =
            12 * holder.itemView.resources.displayMetrics.density

        holder.estado.background = fondo

        val paddingHorizontal =
            (10 * holder.itemView.resources.displayMetrics.density).toInt()
        val paddingVertical =
            (6 * holder.itemView.resources.displayMetrics.density).toInt()

        holder.estado.setPadding(
            paddingHorizontal,
            paddingVertical,
            paddingHorizontal,
            paddingVertical
        )

        // Solo se pueden cancelar las reservas pendientes.
        holder.btnCancelar.visibility =
            if (estadoActual == "PENDIENTE") View.VISIBLE
            else View.GONE

        holder.btnCancelar.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Cancelar reserva")
                .setMessage(
                    "¿Seguro que deseas cancelar la reserva de ${reserva.nombreServicio}?"
                )
                .setNegativeButton("No", null)
                .setPositiveButton("Sí, cancelar") { _, _ ->
                    onCancelar(reserva)
                }
                .show()
        }
    }

    override fun getItemCount(): Int = reservas.size
}