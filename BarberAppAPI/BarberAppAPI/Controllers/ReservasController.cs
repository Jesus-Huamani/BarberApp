using BarberAppAPI.Data;
using BarberAppAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BarberAppAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ReservasController : ControllerBase
    {
        private readonly BarberAppContext _context;

        public ReservasController(BarberAppContext context)
        {
            _context = context;
        }

        // GET: api/Reservas
        [HttpGet]
        public async Task<IActionResult> ObtenerReservas()
        {
            var reservas = await _context.Reservas
                .Include(r => r.Usuario)
                .Include(r => r.Servicio)
                .ToListAsync();

            return Ok(reservas);
        }

        // GET: api/Reservas/5
        [HttpGet("{id}")]
        public async Task<IActionResult> ObtenerReserva(int id)
        {
            var reserva = await _context.Reservas
                .Include(r => r.Usuario)
                .Include(r => r.Servicio)
                .FirstOrDefaultAsync(r => r.IdReserva == id);

            if (reserva == null)
                return NotFound(new { mensaje = "Reserva no encontrada." });

            return Ok(reserva);
        }

        // POST: api/Reservas
        [HttpPost]
        public async Task<IActionResult> CrearReserva(Reserva reserva)
        {
            if (reserva.IdUsuario <= 0 || reserva.IdServicio <= 0)
            {
                return BadRequest(new
                {
                    mensaje = "Debes indicar un usuario y un servicio válidos."
                });
            }

            if (reserva.Fecha.Date < DateTime.Today)
            {
                return BadRequest(new
                {
                    mensaje = "No puedes reservar una fecha pasada."
                });
            }

            if (reserva.Hora < TimeSpan.Zero ||
                reserva.Hora >= TimeSpan.FromDays(1))
            {
                return BadRequest(new
                {
                    mensaje = "La hora de la reserva no es válida."
                });
            }

            var usuarioExiste = await _context.Usuarios
                .AnyAsync(u => u.IdUsuario == reserva.IdUsuario);

            if (!usuarioExiste)
            {
                return BadRequest(new
                {
                    mensaje = "El usuario indicado no existe."
                });
            }

            var servicio = await _context.Servicios
                .FirstOrDefaultAsync(s =>
                    s.IdServicio == reserva.IdServicio && s.Estado);

            if (servicio == null)
            {
                return BadRequest(new
                {
                    mensaje = "El servicio no existe o no está disponible."
                });
            }

            // El estado inicial siempre será PENDIENTE.
            reserva.IdReserva = 0;
            reserva.Estado = "PENDIENTE";
            reserva.Usuario = null;
            reserva.Servicio = null;

            _context.Reservas.Add(reserva);
            await _context.SaveChangesAsync();

            var reservaCreada = await _context.Reservas
                .Include(r => r.Usuario)
                .Include(r => r.Servicio)
                .FirstAsync(r => r.IdReserva == reserva.IdReserva);

            return CreatedAtAction(
                nameof(ObtenerReserva),
                new { id = reserva.IdReserva },
                reservaCreada
            );
        }

        // PUT: api/Reservas/5/confirmar
        [HttpPut("{id}/confirmar")]
        public async Task<IActionResult> ConfirmarReserva(int id)
        {
            var reserva = await _context.Reservas.FindAsync(id);

            if (reserva == null)
                return NotFound(new { mensaje = "Reserva no encontrada." });

            reserva.Estado = "CONFIRMADA";

            await _context.SaveChangesAsync();

            return Ok(reserva);
        }

        // PUT: api/Reservas/5/cancelar
        [HttpPut("{id}/cancelar")]
        public async Task<IActionResult> CancelarReserva(int id)
        {
            var reserva = await _context.Reservas.FindAsync(id);

            if (reserva == null)
                return NotFound(new { mensaje = "Reserva no encontrada." });

            reserva.Estado = "CANCELADA";

            await _context.SaveChangesAsync();

            return Ok(reserva);
        }
    }
}