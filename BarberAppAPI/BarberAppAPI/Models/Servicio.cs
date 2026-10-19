using System.ComponentModel.DataAnnotations;

namespace BarberAppAPI.Models
{
    public class Servicio
    {
        [Key]
        public int IdServicio { get; set; }

        public string Nombre { get; set; } = "";
        public string? Descripcion { get; set; }
        public decimal Precio { get; set; }
        public int Duracion { get; set; }
        public bool Estado { get; set; } = true;

        public ICollection<Reserva> Reservas { get; set; } = new List<Reserva>();
    }
}