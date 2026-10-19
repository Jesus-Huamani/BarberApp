package pe.edu.cibertec.barberapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BarberDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Barberjh@AppLocal.db"
        private const val DATABASE_VERSION = 2

        // Tabla de servicios
        const val TABLA_SERVICIOS = "Servicios"
        const val COL_ID = "idServicio"
        const val COL_NOMBRE = "nombre"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_PRECIO = "precio"
        const val COL_DURACION = "duracion"
        const val COL_ESTADO = "estado"

        // Tabla de reservas
        const val TABLA_RESERVAS = "Reservas"
        const val COL_ID_RESERVA = "idReserva"
        const val COL_ID_USUARIO = "idUsuario"
        const val COL_CLIENTE = "nombreCliente"
        const val COL_SERVICIO_RESERVA = "nombreServicio"
        const val COL_FECHA = "fecha"
        const val COL_HORA = "hora"
        const val COL_ESTADO_RESERVA = "estado"
    }

    override fun onCreate(db: SQLiteDatabase) {
        crearTablaServicios(db)
        crearTablaReservas(db)
    }

    private fun crearTablaServicios(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLA_SERVICIOS (
                $COL_ID INTEGER PRIMARY KEY,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_DESCRIPCION TEXT,
                $COL_PRECIO REAL NOT NULL,
                $COL_DURACION INTEGER NOT NULL,
                $COL_ESTADO INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    private fun crearTablaReservas(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLA_RESERVAS (
                $COL_ID_RESERVA INTEGER PRIMARY KEY,
                $COL_ID_USUARIO INTEGER NOT NULL,
                $COL_CLIENTE TEXT NOT NULL,
                $COL_SERVICIO_RESERVA TEXT NOT NULL,
                $COL_FECHA TEXT NOT NULL,
                $COL_HORA TEXT NOT NULL,
                $COL_ESTADO_RESERVA TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(sql)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 2) {
            crearTablaReservas(db)
        }
    }

    // Guardar o actualizar un servicio
    fun guardarServicio(
        id: Int,
        nombre: String,
        descripcion: String?,
        precio: Double,
        duracion: Int,
        estado: Boolean
    ) {
        val valores = ContentValues().apply {
            put(COL_ID, id)
            put(COL_NOMBRE, nombre)
            put(COL_DESCRIPCION, descripcion)
            put(COL_PRECIO, precio)
            put(COL_DURACION, duracion)
            put(COL_ESTADO, if (estado) 1 else 0)
        }

        writableDatabase.use { db ->
            db.insertWithOnConflict(
                TABLA_SERVICIOS,
                null,
                valores,
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
    }

    // Obtener servicios activos
    fun obtenerServicios(): List<Servicio> {
        val lista = mutableListOf<Servicio>()

        readableDatabase.use { db ->
            db.rawQuery(
                "SELECT * FROM $TABLA_SERVICIOS WHERE $COL_ESTADO = 1",
                null
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    lista.add(
                        Servicio(
                            idServicio = cursor.getInt(
                                cursor.getColumnIndexOrThrow(COL_ID)
                            ),
                            nombre = cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_NOMBRE)
                            ),
                            descripcion = cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_DESCRIPCION)
                            ),
                            precio = cursor.getDouble(
                                cursor.getColumnIndexOrThrow(COL_PRECIO)
                            ),
                            duracion = cursor.getInt(
                                cursor.getColumnIndexOrThrow(COL_DURACION)
                            ),
                            estado = cursor.getInt(
                                cursor.getColumnIndexOrThrow(COL_ESTADO)
                            ) == 1
                        )
                    )
                }
            }
        }

        return lista
    }

    // Guardar o actualizar una reserva recibida de la API
    fun guardarReserva(
        idReserva: Int,
        idUsuario: Int,
        nombreCliente: String,
        nombreServicio: String,
        fecha: String,
        hora: String,
        estado: String
    ) {
        val valores = ContentValues().apply {
            put(COL_ID_RESERVA, idReserva)
            put(COL_ID_USUARIO, idUsuario)
            put(COL_CLIENTE, nombreCliente)
            put(COL_SERVICIO_RESERVA, nombreServicio)
            put(COL_FECHA, fecha)
            put(COL_HORA, hora)
            put(COL_ESTADO_RESERVA, estado)
        }

        writableDatabase.use { db ->
            db.insertWithOnConflict(
                TABLA_RESERVAS,
                null,
                valores,
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
    }

    // Obtener las reservas guardadas de un usuario
    fun obtenerReservas(idUsuario: Int): List<Reserva> {
        val lista = mutableListOf<Reserva>()

        readableDatabase.use { db ->
            db.query(
                TABLA_RESERVAS,
                null,
                "$COL_ID_USUARIO = ?",
                arrayOf(idUsuario.toString()),
                null,
                null,
                "$COL_FECHA DESC, $COL_HORA DESC"
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    lista.add(
                        Reserva(
                            idReserva = cursor.getInt(
                                cursor.getColumnIndexOrThrow(COL_ID_RESERVA)
                            ),
                            nombreCliente = cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_CLIENTE)
                            ),
                            nombreServicio = cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_SERVICIO_RESERVA)
                            ),
                            fecha = cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_FECHA)
                            ),
                            hora = cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_HORA)
                            ),
                            estado = cursor.getString(
                                cursor.getColumnIndexOrThrow(COL_ESTADO_RESERVA)
                            )
                        )
                    )
                }
            }
        }

        return lista
    }
}