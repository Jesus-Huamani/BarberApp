using System.ComponentModel.DataAnnotations;

namespace BarberAppAPI.Models
{
    public class RegistroUsuarioDto
    {
        [Required]
        public string Nombre { get; set; } = "";

        [Required]
        public string Apellido { get; set; } = "";

        [Required]
        [EmailAddress]
        public string Correo { get; set; } = "";

        [Required]
        [MinLength(6)]
        public string Password { get; set; } = "";

        public string? Telefono { get; set; }
    }

    public class LoginUsuarioDto
    {
        [Required]
        [EmailAddress]
        public string Correo { get; set; } = "";

        [Required]
        public string Password { get; set; } = "";
    }
}

