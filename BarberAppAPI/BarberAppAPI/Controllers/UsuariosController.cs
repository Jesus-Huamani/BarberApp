using BarberAppAPI.Data;
using BarberAppAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Identity;

namespace BarberAppAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UsuariosController : ControllerBase
    {
        private readonly BarberAppContext _context;
        private readonly PasswordHasher<Usuario> _hasher = new();

        public UsuariosController(BarberAppContext context)
        {
            _context = context;
        }

        // POST: api/Usuarios/registro
        [HttpPost("registro")]
        public async Task<IActionResult> Registrar(
            RegistroUsuarioDto datos)
        {
            var correo = datos.Correo.Trim().ToLower();

            bool existe = await _context.Usuarios
                .AnyAsync(u => u.Correo.ToLower() == correo);

            if (existe)
            {
                return BadRequest(new
                {
                    mensaje = "Este correo ya está registrado."
                });
            }

            var usuario = new Usuario
            {
                Nombre = datos.Nombre.Trim(),
                Apellido = datos.Apellido.Trim(),
                Correo = correo,
                Telefono = datos.Telefono,
                Rol = "CLIENTE"
            };

            usuario.Password = _hasher.HashPassword(
                usuario, datos.Password);

            _context.Usuarios.Add(usuario);
            await _context.SaveChangesAsync();

            return Ok(new
            {
                mensaje = "Cuenta creada correctamente.",
                idUsuario = usuario.IdUsuario,
                nombre = usuario.Nombre,
                apellido = usuario.Apellido,
                correo = usuario.Correo,
                rol = usuario.Rol
            });
        }

        // POST: api/Usuarios/login
        [HttpPost("login")]
        public async Task<IActionResult> IniciarSesion(
            LoginUsuarioDto datos)
        {
            var correo = datos.Correo.Trim().ToLower();

            var usuario = await _context.Usuarios
                .FirstOrDefaultAsync(
                    u => u.Correo.ToLower() == correo);

            if (usuario == null)
            {
                return Unauthorized(new
                {
                    mensaje = "Correo o contraseña incorrectos."
                });
            }

            var resultado = _hasher.VerifyHashedPassword(
                usuario, usuario.Password, datos.Password);

            if (resultado == PasswordVerificationResult.Failed)
            {
                return Unauthorized(new
                {
                    mensaje = "Correo o contraseña incorrectos."
                });
            }

            return Ok(new
            {
                mensaje = "Inicio de sesión correcto.",
                idUsuario = usuario.IdUsuario,
                nombre = usuario.Nombre,
                apellido = usuario.Apellido,
                correo = usuario.Correo,
                rol = usuario.Rol
            });
        }

        // GET: api/Usuarios
        [HttpGet]
        public async Task<IActionResult> ObtenerUsuarios()
        {
            var usuarios = await _context.Usuarios
                .Select(u => new
                {
                    u.IdUsuario,
                    u.Nombre,
                    u.Apellido,
                    u.Correo,
                    u.Telefono,
                    u.Rol
                })
                .ToListAsync();

            return Ok(usuarios);
        }

        // PUT: api/Usuarios/1
        [HttpPut("{id}")]
        public async Task<IActionResult> ActualizarUsuario(
            int id, Usuario usuario)
        {
            if (id != usuario.IdUsuario)
            {
                return BadRequest();
            }

            return BadRequest(new
            {
                mensaje = "La actualización de usuarios debe implementarse mediante datos específicos para no modificar la contraseña o el rol accidentalmente."
            });
        }

        // DELETE: api/Usuarios/1
        [HttpDelete("{id}")]
        public async Task<IActionResult> EliminarUsuario(int id)
        {
            var usuario = await _context.Usuarios.FindAsync(id);

            if (usuario == null)
            {
                return NotFound();
            }

            _context.Usuarios.Remove(usuario);
            await _context.SaveChangesAsync();

            return Ok(new
            {
                mensaje = "Usuario eliminado correctamente."
            });
        }
    }
}