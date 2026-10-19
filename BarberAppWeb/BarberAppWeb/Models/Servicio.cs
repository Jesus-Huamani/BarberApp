namespace BarberAppWeb.Models
{
    public class Servicio
    {
        public int IdServicio { get; set; }
        public string Nombre { get; set; } = "";
        public string? Descripcion { get; set; }
        public decimal Precio { get; set; }
        public int Duracion { get; set; }
        public bool Estado { get; set; }
    }
}