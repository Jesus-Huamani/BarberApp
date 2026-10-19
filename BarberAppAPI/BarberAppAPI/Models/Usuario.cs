using System.ComponentModel.DataAnnotations;

namespace BarberAppAPI.Models
{
    public class Usuario
    {
        [Key]
        public int IdUsuario { get; set; }

        public string Nombre { get; set; } = "";
        public string Apellido { get; set; } = "";
        public string Correo { get; set; } = "";
        public string Password { get; set; } = "";
        public string? Telefono { get; set; }
        public string Rol { get; set; } = "CLIENTE";

        public ICollection<Reserva> Reservas { get; set; } = new List<Reserva>();
    }
}