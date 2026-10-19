using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BarberAppAPI.Models
{
    public class Reserva
    {
        [Key]
        public int IdReserva { get; set; }

        public int IdUsuario { get; set; }

        public int IdServicio { get; set; }

        public DateTime Fecha { get; set; }

        public TimeSpan Hora { get; set; }

        public string Estado { get; set; } = "PENDIENTE";

        // Relación con Usuario
        [ForeignKey("IdUsuario")]
        public Usuario? Usuario { get; set; }

        // Relación con Servicio
        [ForeignKey("IdServicio")]
        public Servicio? Servicio { get; set; }
    }
}