using BarberAppWeb.Models;
using Microsoft.AspNetCore.Mvc;
using System.Net.Http.Json;

namespace BarberAppWeb.Controllers
{
    public class ReservasController : Controller
    {
        private readonly IHttpClientFactory _httpClientFactory;

        public ReservasController(
            IHttpClientFactory httpClientFactory)
        {
            _httpClientFactory = httpClientFactory;
        }

        public async Task<IActionResult> Index()
        {
            var cliente =
                _httpClientFactory.CreateClient("BarberAPI");

            var respuesta = await cliente.GetAsync("api/Reservas");

            if (!respuesta.IsSuccessStatusCode)
            {
                TempData["Error"] =
                    "No se pudieron cargar las reservas.";

                return View(new List<Reserva>());
            }

            var reservas =
                await respuesta.Content.ReadFromJsonAsync<List<Reserva>>();

            return View(reservas ?? new List<Reserva>());
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Confirmar(int id)
        {
            var cliente =
                _httpClientFactory.CreateClient("BarberAPI");

            var respuesta = await cliente.PutAsync(
                $"api/Reservas/{id}/confirmar",
                null
            );

            if (respuesta.IsSuccessStatusCode)
            {
                TempData["Mensaje"] =
                    $"La reserva #{id} fue confirmada correctamente.";
            }
            else
            {
                TempData["Error"] =
                    $"No se pudo confirmar la reserva #{id}. " +
                    $"Código de respuesta: {(int)respuesta.StatusCode}.";
            }

            return RedirectToAction(nameof(Index));
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Cancelar(int id)
        {
            var cliente =
                _httpClientFactory.CreateClient("BarberAPI");

            var respuesta = await cliente.PutAsync(
                $"api/Reservas/{id}/cancelar",
                null
            );

            if (respuesta.IsSuccessStatusCode)
            {
                TempData["Mensaje"] =
                    $"La reserva #{id} fue cancelada correctamente.";
            }
            else
            {
                TempData["Error"] =
                    $"No se pudo cancelar la reserva #{id}. " +
                    $"Código de respuesta: {(int)respuesta.StatusCode}.";
            }

            return RedirectToAction(nameof(Index));
        }
    }
}