package pe.edu.cibertec.barberapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pe.edu.cibertec.barberapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar los botones
        binding.btnServicios.setOnClickListener(this)
        binding.btnReservas.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            // Abrir la pantalla de servicios
            binding.btnServicios.id -> {
                val intent = Intent(
                    this,
                    ServiciosActivity::class.java
                )
                startActivity(intent)
            }

            // Abrir la pantalla de reservas
            binding.btnReservas.id -> {
                val intent = Intent(
                    this,
                    ReservasActivity::class.java
                )
                startActivity(intent)
            }
        }
    }
}
