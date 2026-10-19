package pe.edu.cibertec.barberapp

data class Reserva(
    val idReserva: Int,
    val nombreCliente: String,
    val nombreServicio: String,
    val fecha: String,
    val hora: String,
    val estado: String
)
