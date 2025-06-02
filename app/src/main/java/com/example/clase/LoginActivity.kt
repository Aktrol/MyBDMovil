package com.example.clase // Define el paquete al que pertenece este archivo.

import android.content.Intent // Importa la clase Intent para iniciar otras actividades.
import android.os.Bundle // Importa Bundle para manejar el estado de la actividad.
import android.widget.Toast // Importa Toast para mostrar mensajes cortos al usuario.
import androidx.appcompat.app.AppCompatActivity // Importa AppCompatActivity, la clase base para actividades que usan características de compatibilidad de AndroidX.
import com.example.clase.database.DatabaseHelper // Importa DatabaseHelper para la gestión de la base de datos.
import com.example.clase.databinding.ActivityLoginBinding // Importa la clase de binding generada para el layout activity_login.xml, utilizada para acceder a las vistas de forma segura.

/**
 * Actividad de inicio de sesión que permite al usuario ingresar un nombre de usuario,
 * contraseña y el nombre de la base de datos a la que desea conectarse.
 * Valida las credenciales y el nombre de la base de datos antes de intentar la conexión.
 * Si la conexión es exitosa, navega a la [MainManagerActivity].
 */
class LoginActivity : AppCompatActivity() {

    // Variable de enlace para acceder a las vistas del layout activity_login.xml.
    private lateinit var binding: ActivityLoginBinding

    // Credenciales de usuario y contraseña codificadas para simplificar.
    // EN UNA APLICACIÓN REAL: No se deben usar credenciales codificadas.
    // En su lugar, se deberían usar SharedPreferences, Android Keystore,
    // o un sistema de autenticación de backend seguro.
    private val VALID_USERNAME = "admin"
    private val VALID_PASSWORD = "password123"

    /**
     * Llamado cuando la actividad es creada por primera vez.
     * Aquí se inicializa la vista de enlace (ViewBinding) y se configura el listener del botón de inicio de sesión.
     *
     * @param savedInstanceState Si la actividad se está recreando después de un cambio de configuración
     * (como la rotación de pantalla) o si se eliminó de la memoria, este Bundle contiene los datos
     * que se guardaron previamente por [onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla el layout usando ViewBinding y establece la vista de contenido de la actividad.
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura un listener para el botón de inicio de sesión (btnLogin).
        // Cuando se hace clic en el botón, se llama a la función performLogin().
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
    }

    /**
     * Realiza la lógica de inicio de sesión.
     * Obtiene los valores de usuario, contraseña y nombre de la base de datos de los campos de texto.
     * Realiza validaciones básicas y, si son exitosas, intenta conectar con la base de datos
     * y navegar a la [MainManagerActivity].
     */
    private fun performLogin() {
        // Obtiene el texto de los campos de usuario, contraseña y nombre de la base de datos,
        // eliminando espacios en blanco al inicio y al final.
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val databaseName = binding.etDatabaseName.text.toString().trim()

        // 1. Validación de campos vacíos.
        if (username.isEmpty() || password.isEmpty() || databaseName.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return // Sale de la función si algún campo está vacío.
        }

        // 2. Validación de credenciales (comprobación simple codificada).
        if (username != VALID_USERNAME || password != VALID_PASSWORD) {
            Toast.makeText(this, "Usuario o contraseña incorrectos.", Toast.LENGTH_SHORT).show()
            return // Sale de la función si las credenciales no coinciden.
        }

        // 3. Validación básica del nombre de la base de datos (debe terminar en ".db").
        if (!databaseName.endsWith(".db")) {
            Toast.makeText(this, "El nombre de la base de datos debe terminar en .db", Toast.LENGTH_LONG).show()
            return // Sale de la función si el nombre no termina en ".db".
        }

        // Intenta abrir/crear la base de datos para validar su ruta/nombre.
        // La creación implícita del archivo de la base de datos ocurre si no existe.
        try {
            // Crea una instancia de DatabaseHelper con el contexto de la aplicación y el nombre de la base de datos.
            val dbHelper = DatabaseHelper(applicationContext, databaseName)
            // Intenta obtener una instancia de la base de datos en modo escritura.
            // Esto verificará si la ruta es válida y si la base de datos se puede abrir/crear.
            val db = dbHelper.writableDatabase
            db.close() // Cierra la base de datos inmediatamente después de abrirla.

            // Muestra un mensaje de éxito si la conexión a la base de datos fue exitosa.
            Toast.makeText(this, "Conexión a la base de datos '$databaseName' exitosa.", Toast.LENGTH_SHORT).show()

            // Si la conexión fue exitosa, navega a la MainManagerActivity.
            val intent = Intent(this, MainManagerActivity::class.java).apply {
                // Pasa el nombre de la base de datos a la MainManagerActivity como un extra.
                putExtra("DATABASE_NAME", databaseName)
            }
            startActivity(intent) // Inicia la MainManagerActivity.
            finish() // Cierra LoginActivity para que el usuario no pueda volver a ella con el botón de retroceso.
        } catch (e: Exception) {
            // Captura cualquier excepción que ocurra durante el intento de conexión a la base de datos.
            // Muestra un mensaje de error detallado al usuario.
            Toast.makeText(this, "Error al conectar con la base de datos: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace() // Imprime la pila de llamadas del error para depuración.
        }
    }
}
