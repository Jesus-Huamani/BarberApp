using BarberAppWeb.Models;
using Microsoft.AspNetCore.Mvc;
using System.Net.Http.Json;

namespace BarberAppWeb.Controllers
{
    public class ServiciosController : Controller
    {
        private readonly IHttpClientFactory _httpClientFactory;

        public ServiciosController(IHttpClientFactory httpClientFactory)
        {
            _httpClientFactory = httpClientFactory;
        }

        public async Task<IActionResult> Index()
        {
            var cliente = _httpClientFactory.CreateClient("BarberAPI");

            var servicios = await cliente.GetFromJsonAsync<List<Servicio>>(
                "api/Servicios"
            );

            return View(servicios);
        }

        // Mostrar formulario para crear servicio
        public IActionResult Crear()
        {
            return View();
        }

        // Recibir formulario y crear servicio
        [HttpPost]
        public async Task<IActionResult> Crear(Servicio servicio)
        {
            if (!ModelState.IsValid)
            {
                return View(servicio);
            }

            var cliente = _httpClientFactory.CreateClient("BarberAPI");

            var respuesta = await cliente.PostAsJsonAsync(
                "api/Servicios",
                servicio
            );

            if (respuesta.IsSuccessStatusCode)
            {
                return RedirectToAction(nameof(Index));
            }

            return View(servicio);
        }



public async Task<IActionResult> Editar(int id)
        {
            var cliente = _httpClientFactory.CreateClient("BarberAPI");

            var servicio = await cliente.GetFromJsonAsync<Servicio>(
                $"api/Servicios/{id}"
            );

            if (servicio == null)
                return NotFound();

            return View(servicio);
        }

        [HttpPost]
        public async Task<IActionResult> Editar(Servicio servicio)
        {
            if (!ModelState.IsValid)
                return View(servicio);

            var cliente = _httpClientFactory.CreateClient("BarberAPI");

            var respuesta = await cliente.PutAsJsonAsync(
                $"api/Servicios/{servicio.IdServicio}",
                servicio
            );

            if (respuesta.IsSuccessStatusCode)
                return RedirectToAction(nameof(Index));

            return View(servicio);
        }

        public async Task<IActionResult> Eliminar(int id)
        {
            var cliente = _httpClientFactory.CreateClient("BarberAPI");

            var respuesta = await cliente.DeleteAsync(
                $"api/Servicios/{id}"
            );

            return RedirectToAction(nameof(Index));
        }



    }
}
