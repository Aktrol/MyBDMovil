package com.example.clase.database // Define el paquete al que pertenece este archivo.

import android.content.Context // Importa la clase Context para acceder a recursos y servicios del sistema.
import android.database.sqlite.SQLiteDatabase // Importa la clase SQLiteDatabase para interactuar con la base de datos.
import android.database.sqlite.SQLiteOpenHelper // Importa SQLiteOpenHelper, una clase de ayuda para gestionar la creación y actualización de bases de datos.
import android.util.Log // Importa la clase Log para registrar mensajes de depuración y errores.

/**
 * Clase auxiliar para la gestión de la base de datos SQLite de la aplicación.
 * Extiende [SQLiteOpenHelper] para facilitar la creación, apertura y actualización
 * de la base de datos. Permite conectar a una base de datos existente o crear una nueva.
 *
 * @param context El contexto de la aplicación.
 * @param databaseName El nombre de la base de datos a abrir o crear.
 */
class DatabaseHelper(context: Context, databaseName: String) :
    SQLiteOpenHelper(context, databaseName, null, DATABASE_VERSION) {

    /**
     * Objeto complementario que contiene constantes estáticas de la clase.
     */
    companion object {
        private const val DATABASE_VERSION = 1 // La versión de la base de datos. Se incrementa para forzar onUpgrade.
        private const val TAG = "DatabaseHelper" // Etiqueta para el registro de logs.
    }

    /**
     * Llamado cuando la base de datos se crea por primera vez.
     * Aquí se deben crear las tablas iniciales si son necesarias.
     * Para esta aplicación, se asume que el usuario podría conectarse a una DB existente
     * o crear tablas mediante consultas.
     *
     * @param db La instancia de la base de datos [SQLiteDatabase] que acaba de ser creada.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "Database created: $databaseName") // Registra un mensaje cuando la base de datos es creada.
        // Ejemplo de cómo se podría crear una tabla 'users' por defecto si la base de datos es nueva y vacía:
        // db?.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT, email TEXT);")
    }

    /**
     * Llamado cuando la base de datos necesita ser actualizada.
     * Esto ocurre si la versión de la base de datos (DATABASE_VERSION) en el código
     * es mayor que la versión de la base de datos existente en el dispositivo.
     * Aquí es donde se manejan las migraciones de esquemas (ej. añadir/eliminar tablas, alterar columnas).
     *
     * @param db La instancia de la base de datos [SQLiteDatabase] que necesita ser actualizada.
     * @param oldVersion La versión de la base de datos anterior.
     * @param newVersion La nueva versión de la base de datos.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Database upgraded from version $oldVersion to $newVersion") // Registra el evento de actualización.
        // Ejemplo: Si se necesitara una actualización drástica, se podrían eliminar tablas antiguas
        // db?.execSQL("DROP TABLE IF EXISTS users;")
        // onCreate(db) // Y luego recrear las tablas con el nuevo esquema.
    }

    /**
     * Método auxiliar para obtener los nombres de todas las tablas en la base de datos.
     * Excluye tablas internas de SQLite como 'sqlite_%' y 'android_metadata'.
     *
     * @return Una [List] de [String] que contiene los nombres de las tablas.
     */
    fun getAllTableNames(): List<String> {
        val tableNames = mutableListOf<String>() // Lista mutable para almacenar los nombres de las tablas.
        val db = readableDatabase // Obtiene una instancia de la base de datos en modo lectura.
        var cursor: android.database.Cursor? = null // Cursor para iterar sobre los resultados de la consulta.
        try {
            // Consulta para obtener los nombres de las tablas del sistema 'sqlite_master'.
            cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_metadata';", null)
            cursor?.let { // Asegura que el cursor no sea nulo.
                if (it.moveToFirst()) { // Mueve el cursor a la primera fila si existen resultados.
                    do {
                        tableNames.add(it.getString(0)) // Añade el nombre de la tabla (primera columna) a la lista.
                    } while (it.moveToNext()) // Continúa mientras haya más filas.
                }
            }
        } catch (e: Exception) {
            // Captura cualquier excepción que ocurra durante la consulta y la registra.
            Log.e(TAG, "Error getting table names: ${e.message}")
        } finally {
            // Asegura que el cursor y la base de datos se cierren para liberar recursos.
            cursor?.close()
            db.close()
        }
        return tableNames // Devuelve la lista de nombres de tablas.
    }

    /**
     * Método auxiliar para obtener los nombres y tipos de las columnas de una tabla específica.
     * Utiliza la sentencia PRAGMA `table_info()` de SQLite.
     *
     * @param tableName El nombre de la tabla de la que se quieren obtener las columnas.
     * @return Una [List] de [Pair] donde cada par contiene el nombre de la columna ([String])
     * y su tipo ([String]).
     */
    fun getTableColumns(tableName: String): List<Pair<String, String>> {
        val columns = mutableListOf<Pair<String, String>>() // Lista mutable para almacenar los pares de nombre/tipo de columna.
        val db = readableDatabase // Obtiene una instancia de la base de datos en modo lectura.
        var cursor: android.database.Cursor? = null // Cursor para iterar sobre los resultados de la consulta.
        try {
            // Consulta PRAGMA table_info(tablename) que devuelve información sobre las columnas de la tabla.
            cursor = db.rawQuery("PRAGMA table_info('$tableName')", null)
            cursor?.let { // Asegura que el cursor no sea nulo.
                if (it.moveToFirst()) { // Mueve el cursor a la primera fila si existen resultados.
                    // Obtiene los índices de las columnas 'name' y 'type' en el resultado del PRAGMA.
                    val nameIndex = it.getColumnIndex("name")
                    val typeIndex = it.getColumnIndex("type")

                    if (nameIndex != -1 && typeIndex != -1) { // Verifica que las columnas existan.
                        do {
                            val name = it.getString(nameIndex) // Obtiene el nombre de la columna.
                            val type = it.getString(typeIndex) // Obtiene el tipo de la columna.
                            columns.add(Pair(name, type)) // Añade el par (nombre, tipo) a la lista.
                        } while (it.moveToNext()) // Continúa mientras haya más filas.
                    } else {
                        // Registra un error si las columnas esperadas no se encuentran.
                        Log.e(TAG, "Column 'name' or 'type' not found in PRAGMA table_info result for table: $tableName")
                    }
                }
            }
        } catch (e: Exception) {
            // Captura cualquier excepción que ocurra durante la consulta y la registra.
            Log.e(TAG, "Error getting columns for table $tableName: ${e.message}")
        } finally {
            // Asegura que el cursor y la base de datos se cierren para liberar recursos.
            cursor?.close()
            db.close()
        }
        return columns // Devuelve la lista de columnas.
    }
}
