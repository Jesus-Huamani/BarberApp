package pe.edu.cibertec.barberapp

data class Servicio(
    val idServicio: Int,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val duracion: Int,
    val estado: Boolean
)
