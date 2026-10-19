using Microsoft.EntityFrameworkCore;
using System.Text.Json.Serialization;
using BarberAppAPI.Models;

namespace BarberAppAPI.Data
{
    public class BarberAppContext : DbContext
    {
        public BarberAppContext(DbContextOptions<BarberAppContext> options)
            : base(options)
        {
        }

        public DbSet<Usuario> Usuarios { get; set; }
        public DbSet<Servicio> Servicios { get; set; }
        public DbSet<Reserva> Reservas { get; set; }
    }
}
