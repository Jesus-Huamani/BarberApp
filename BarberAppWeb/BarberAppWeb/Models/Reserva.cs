namespace BarberAppWeb.Models
{
    public class Reserva
    {
        public int IdReserva { get; set; }

        public int IdUsuario { get; set; }

        public int IdServicio { get; set; }

        public DateTime Fecha { get; set; }

        public TimeSpan Hora { get; set; }

        public string Estado { get; set; } = "PENDIENTE";

        public Usuario? Usuario { get; set; }

        public Servicio? Servicio { get; set; }
    }
}