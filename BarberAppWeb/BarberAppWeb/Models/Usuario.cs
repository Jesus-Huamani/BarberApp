namespace BarberAppWeb.Models
{
    public class Usuario
    {
        public int IdUsuario { get; set; }
        public string Nombre { get; set; } = "";
        public string Apellido { get; set; } = "";
        public string Correo { get; set; } = "";
        public string? Telefono { get; set; }
        public string Rol { get; set; } = "";
    }
}