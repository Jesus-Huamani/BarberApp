using BarberAppAPI.Data;
using BarberAppAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BarberAppAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ServiciosController : ControllerBase
    {
        private readonly BarberAppContext _context;

        public ServiciosController(BarberAppContext context)
        {
            _context = context;
        }

        // GET: api/Servicios
        [HttpGet]
        public async Task<IActionResult> ObtenerServicios()
        {
            var servicios = await _context.Servicios.ToListAsync();

            return Ok(servicios);
        }

        
// GET: api/Servicios/1
[HttpGet("{id}")]
public async Task<IActionResult> ObtenerServicio(int id)
        {
            var servicio = await _context.Servicios.FindAsync(id);

            if (servicio == null)
            {
                return NotFound();
            }

            return Ok(servicio);
        }


        // POST: api/Servicios
        [HttpPost]
        public async Task<IActionResult> CrearServicio(Servicio servicio)
        {
            _context.Servicios.Add(servicio);

            await _context.SaveChangesAsync();

            return Ok(servicio);
        }

        // PUT: api/Servicios/1
        [HttpPut("{id}")]
        public async Task<IActionResult> ActualizarServicio(int id, Servicio servicio)
        {
            if (id != servicio.IdServicio)
            {
                return BadRequest();
            }

            _context.Entry(servicio).State = EntityState.Modified;

            await _context.SaveChangesAsync();

            return Ok(servicio);
        }

        // DELETE: api/Servicios/1
        [HttpDelete("{id}")]
        public async Task<IActionResult> EliminarServicio(int id)
        {
            var servicio = await _context.Servicios.FindAsync(id);

            if (servicio == null)
            {
                return NotFound();
            }

            _context.Servicios.Remove(servicio);

            await _context.SaveChangesAsync();

            return Ok(new
            {
                mensaje = "Servicio eliminado correctamente"
            });
        }
    }
}