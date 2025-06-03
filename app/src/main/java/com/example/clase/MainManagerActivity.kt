package com.example.clase // Define el paquete al que pertenece este archivo.

import android.database.Cursor // Importa la clase Cursor para iterar sobre los resultados de una consulta de base de datos.
import android.os.Bundle // Importa Bundle para manejar el estado de la actividad.
import android.util.Log // Importa Log para registrar mensajes de depuración y errores.
import android.view.View // Importa la clase base para todos los componentes de la interfaz de usuario.
import android.widget.Toast // Importa Toast para mostrar mensajes cortos al usuario.
import androidx.appcompat.app.AppCompatActivity // Importa AppCompatActivity, la clase base para actividades que usan características de compatibilidad de AndroidX.
import androidx.recyclerview.widget.LinearLayoutManager // Importa LinearLayoutManager para organizar elementos en un RecyclerView en una lista lineal.
import com.example.clase.adapter.QueryResultAdapter // Importa el adaptador para mostrar los resultados de las consultas SQL.
import com.example.clase.adapter.TableAdapter // Importa el adaptador para mostrar la lista de tablas de la base de datos.
import com.example.clase.database.DatabaseHelper // Importa DatabaseHelper para la gestión de la base de datos.
import com.example.clase.databinding.ActivityMainManagerBinding // Importa la clase de binding generada para el layout activity_main_manager.xml, utilizada para acceder a las vistas de forma segura.
 
/**
 * Actividad principal que permite a los usuarios interactuar con una base de datos SQLite.
 * Muestra las tablas existentes y sus campos, y permite ejecutar consultas SQL personalizadas,
 * mostrando los resultados o un mensaje de éxito/error.
 */
class MainManagerActivity : AppCompatActivity() {

    // Variable de enlace para acceder a las vistas del layout activity_main_manager.xml.
    private lateinit var binding: ActivityMainManagerBinding
    // Instancia de DatabaseHelper para interactuar con la base de datos SQLite.
    private lateinit var databaseHelper: DatabaseHelper
    // Nombre de la base de datos a la que la actividad está conectada.
    private lateinit var databaseName: String

    // Adaptador para mostrar la lista de tablas y sus campos en un RecyclerView.
    private lateinit var tableAdapter: TableAdapter
    // Adaptador para mostrar los resultados de las consultas SQL en un RecyclerView.
    private lateinit var queryResultAdapter: QueryResultAdapter

    /**
     * Llamado cuando la actividad es creada por primera vez.
     * Aquí se inicializa la vista de enlace (ViewBinding), se obtiene el nombre de la base de datos
     * de la actividad anterior, se configura el DatabaseHelper y se preparan los RecyclerViews.
     *
     * @param savedInstanceState Si la actividad se está recreando después de un cambio de configuración
     * (como la rotación de pantalla) o si se eliminó de la memoria, este Bundle contiene los datos
     * que se guardaron previamente por [onSaveInstanceState].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla el layout usando ViewBinding y establece la vista de contenido de la actividad.
        binding = ActivityMainManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtiene el nombre de la base de datos pasado desde LoginActivity, o usa "default.db" si no se proporciona.
        databaseName = intent.getStringExtra("DATABASE_NAME") ?: "default.db"
        // Muestra el nombre de la base de datos conectada en un TextView.
        binding.tvConnectedDb.text = "Conectado a: $databaseName"

        // Inicializa el DatabaseHelper con el contexto actual y el nombre de la base de datos.
        databaseHelper = DatabaseHelper(this, databaseName)

        // Configura los RecyclerViews para las tablas y los resultados de las consultas.
        setupTableRecyclerView()
        setupQueryResultRecyclerView()

        // Carga y muestra las tablas y sus campos desde la base de datos.
        loadTablesAndFields()

        // Configura un listener para el botón de ejecución de consulta.
        binding.btnExecuteQuery.setOnClickListener {
            executeQuery()
        }
    }

    /**
     * Configura el RecyclerView que mostrará la lista de tablas de la base de datos.
     * Establece un LinearLayoutManager y un TableAdapter inicial vacío.
     */
    private fun setupTableRecyclerView() {
        binding.rvTables.layoutManager = LinearLayoutManager(this)
        // Inicializa el adaptador con una lista vacía; los datos se cargarán más tarde.
        tableAdapter = TableAdapter(emptyList())
        binding.rvTables.adapter = tableAdapter
    }

    /**
     * Configura el RecyclerView que mostrará los resultados de las consultas SQL.
     * Establece un LinearLayoutManager y un QueryResultAdapter inicial vacío.
     */
    private fun setupQueryResultRecyclerView() {
        binding.rvQueryResults.layoutManager = LinearLayoutManager(this)
        // Inicializa el adaptador con datos vacíos para encabezados y filas.
        queryResultAdapter = QueryResultAdapter(emptyList(), emptyList())
        binding.rvQueryResults.adapter = queryResultAdapter
        // Muestra el mensaje de "No hay resultados" por defecto.
        binding.tvNoResults.visibility = View.VISIBLE
    }

    /**
     * Carga los nombres de todas las tablas y sus respectivos campos desde la base de datos
     * utilizando el [DatabaseHelper] y actualiza el [TableAdapter].
     */
    private fun loadTablesAndFields() {
        val tableNames = databaseHelper.getAllTableNames() // Obtiene todos los nombres de tabla.
        val tablesWithFields = mutableListOf<Pair<String, List<Pair<String, String>>>>() // Lista para almacenar tablas y sus campos.

        // Itera sobre cada nombre de tabla para obtener sus columnas.
        for (tableName in tableNames) {
            val columns = databaseHelper.getTableColumns(tableName) // Obtiene las columnas de la tabla actual.
            tablesWithFields.add(Pair(tableName, columns)) // Agrega la tabla y sus columnas a la lista.
        }
        // Crea un nuevo TableAdapter con los datos cargados y lo asigna al RecyclerView.
        tableAdapter = TableAdapter(tablesWithFields)
        binding.rvTables.adapter = tableAdapter
    }

    /**
     * Ejecuta la consulta SQL ingresada por el usuario.
     * Distingue entre consultas SELECT y otras consultas (INSERT, UPDATE, DELETE, CREATE, DROP, etc.).
     * Muestra los resultados en el [queryResultAdapter] o un mensaje de éxito/error.
     */
    private fun executeQuery() {
        val query = binding.etSqlQuery.text.toString().trim() // Obtiene la consulta del EditText.
        if (query.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese una consulta SQL.", Toast.LENGTH_SHORT).show()
            return // Sale si la consulta está vacía.
        }

        // Obtiene una instancia de la base de datos en modo escritura.
        // Se usa writableDatabase para todas las operaciones, ya que execSQL lo requiere.
        val db = databaseHelper.writableDatabase
        var cursor: Cursor? = null // Cursor para los resultados de las consultas SELECT.
        try {
            // Verifica si la consulta es de tipo SELECT (ignorando mayúsculas/minúsculas).
            if (query.startsWith("SELECT", true)) {
                cursor = db.rawQuery(query, null) // Ejecuta la consulta SELECT.
                displayResults(cursor) // Muestra los resultados obtenidos.
            } else {
                // Para consultas que no son SELECT (INSERT, UPDATE, DELETE, CREATE, DROP, etc.).
                db.execSQL(query) // Ejecuta la consulta.
                Toast.makeText(this, "Consulta ejecutada con éxito (No SELECT).", Toast.LENGTH_SHORT).show()
                // Limpia los resultados anteriores para consultas no SELECT.
                queryResultAdapter.updateResults(emptyList(), emptyList())
                binding.tvNoResults.text = "Consulta ejecutada con éxito."
                binding.tvNoResults.visibility = View.VISIBLE

                // Si la consulta fue CREATE TABLE o DROP TABLE, recarga la lista de tablas.
                if (query.startsWith("CREATE TABLE", true) || query.startsWith("DROP TABLE", true)) {
                    loadTablesAndFields()
                }
            }
        } catch (e: Exception) {
            // Captura y maneja cualquier error que ocurra durante la ejecución de la consulta.
            Toast.makeText(this, "Error al ejecutar la consulta: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("MainManagerActivity", "SQL Error: ${e.message}", e) // Registra el error en Logcat.
            // Limpia los resultados y muestra el mensaje de error en la UI.
            queryResultAdapter.updateResults(emptyList(), emptyList())
            binding.tvNoResults.text = "Error: ${e.message}"
            binding.tvNoResults.visibility = View.VISIBLE
        } finally {
            cursor?.close() // Asegura que el cursor se cierre si se abrió.
            // NOTA: No se cierra 'db' aquí (db.close()), ya que DatabaseHelper gestiona la vida de la base de datos
            // a través de sus métodos getReadableDatabase() y getWritableDatabase().
            // Cerrar 'db' aquí podría impedir operaciones subsiguientes.
        }
    }

    /**
     * Muestra los resultados de una consulta SELECT en el [queryResultAdapter].
     * Maneja casos donde el cursor es nulo o no contiene resultados.
     *
     * @param cursor El [Cursor] que contiene los resultados de la consulta SELECT.
     */
    private fun displayResults(cursor: Cursor?) {
        // Si el cursor es nulo, limpia los resultados y muestra un mensaje.
        if (cursor == null) {
            queryResultAdapter.updateResults(emptyList(), emptyList())
            binding.tvNoResults.text = "No hay resultados para mostrar."
            binding.tvNoResults.visibility = View.VISIBLE
            return
        }

        // Si el cursor no tiene filas (count == 0), limpia los resultados y muestra un mensaje.
        if (cursor.count == 0) {
            queryResultAdapter.updateResults(emptyList(), emptyList())
            binding.tvNoResults.text = "La consulta no devolvió resultados."
            binding.tvNoResults.visibility = View.VISIBLE
            return
        }

        // Si hay resultados, oculta el mensaje de "No hay resultados".
        binding.tvNoResults.visibility = View.GONE

        // Obtiene los nombres de las columnas del cursor para usarlos como encabezados.
        val headers = cursor.columnNames.toList()
        val data = mutableListOf<List<String>>() // Lista para almacenar las filas de datos.

        // Mueve el cursor a la primera fila y itera sobre todas las filas.
        if (cursor.moveToFirst()) {
            do {
                val row = mutableListOf<String>() // Lista para almacenar los valores de la fila actual.
                // Itera sobre cada columna de la fila actual.
                for (i in 0 until cursor.columnCount) {
                    // Obtiene el valor de la columna como String, si es nulo, usa "NULL".
                    row.add(cursor.getString(i) ?: "NULL")
                }
                data.add(row) // Añade la fila completa a la lista de datos.
            } while (cursor.moveToNext()) // Continúa mientras haya más filas.
        }

        // Actualiza el QueryResultAdapter con los nuevos encabezados y datos.
        queryResultAdapter.updateResults(headers, data)
    }

    /**
     * Llamado cuando la actividad está a punto de ser destruida.
     * Es un buen lugar para liberar recursos.
     * Aunque SQLiteOpenHelper gestiona la apertura/cierre de la base de datos internamente
     * (al llamar a readableDatabase/writableDatabase y luego cerrar el Cursor/SQLiteDatabase),
     * este comentario indica una buena práctica general. En este diseño, la base de datos
     * se cierra implícitamente cuando el Cursor o la instancia de SQLiteDatabase obtenidos
     * del DatabaseHelper son cerrados, y el propio DatabaseHelper no necesita un cierre explícito aquí.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Los recursos de la base de datos se manejan internamente por DatabaseHelper
        // y el cierre del Cursor/SQLiteDatabase después de cada operación.
    }
}
