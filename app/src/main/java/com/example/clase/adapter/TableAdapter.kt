package com.example.clase.adapter // Define el paquete al que pertenece este archivo.

import android.view.LayoutInflater // Importa LayoutInflater para inflar diseños XML en objetos View.
import android.view.View // Importa la clase base para todos los componentes de la interfaz de usuario.
import android.view.ViewGroup // Importa ViewGroup, la clase base para los diseños que contienen otras vistas.
import android.widget.LinearLayout // Importa LinearLayout para organizar vistas en una sola dirección.
import android.widget.TextView // Importa TextView para mostrar texto.
import androidx.recyclerview.widget.RecyclerView // Importa RecyclerView, una vista eficiente para mostrar grandes conjuntos de datos.

/**
 * Adaptador para RecyclerView que muestra una lista de tablas de base de datos,
 * incluyendo sus nombres y los campos (nombre y tipo) que contienen.
 *
 * @param tables Una lista de pares, donde cada par representa una tabla.
 * El primer elemento del par es el nombre de la tabla (String).
 * El segundo elemento es una lista de pares que representan los campos de la tabla,
 * donde cada campo es un par de (nombre del campo: String, tipo del campo: String).
 */
class TableAdapter(private val tables: List<Pair<String, List<Pair<String, String>>>>) :
    RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    /**
     * Crea y devuelve un [TableViewHolder] nuevo cuando RecyclerView necesita uno nuevo.
     * Este método infla el diseño `item_table.xml` para cada elemento de la lista.
     *
     * @param parent El ViewGroup al que se adjuntará la nueva vista después de ser inflada.
     * @param viewType El tipo de vista del nuevo View (no se usa en este adaptador ya que solo hay un tipo de vista).
     * @return Una nueva instancia de [TableViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        // Asegúrate de que R esté correctamente referenciado a tu paquete base (com.example.clase)
        val view = LayoutInflater.from(parent.context).inflate(com.example.clase.R.layout.item_table, parent, false)
        return TableViewHolder(view)
    }

    /**
     * Rellena un [TableViewHolder] con datos en la posición especificada.
     * Este método se encarga de mostrar el nombre de la tabla y, dinámicamente,
     * agrega TextViews para cada uno de sus campos.
     *
     * @param holder El ViewHolder que debe actualizarse para representar el contenido del elemento
     * en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        // Desestructura el par de la tabla en su nombre y la lista de campos.
        val (tableName, fields) = tables[position]
        // Establece el nombre de la tabla en el TextView correspondiente.
        holder.tvTableName.text = tableName

        // Limpia cualquier vista de campo previa en el contenedor para evitar duplicados al reciclar el ViewHolder.
        holder.llTableFieldsContainer.removeAllViews()

        // Verifica si la tabla tiene campos definidos.
        if (fields.isEmpty()) {
            // Si no hay campos, muestra un mensaje indicándolo.
            val tv = TextView(holder.itemView.context)
            tv.text = "No hay campos definidos."
            tv.textSize = 12f
            holder.llTableFieldsContainer.addView(tv)
        } else {
            // Si hay campos, itera sobre ellos y crea un TextView para cada uno.
            fields.forEach { (fieldName, fieldType) ->
                val tv = TextView(holder.itemView.context)
                // Formatea el texto para mostrar el nombre y el tipo del campo.
                tv.text = "- $fieldName ($fieldType)"
                tv.textSize = 14f
                holder.llTableFieldsContainer.addView(tv) // Agrega el TextView al contenedor.
            }
        }
    }

    /**
     * Devuelve el número total de elementos (tablas) en el conjunto de datos del adaptador.
     *
     * @return El número total de tablas a mostrar.
     */
    override fun getItemCount(): Int = tables.size

    /**
     * Clase interna ViewHolder que contiene referencias a las vistas de cada elemento de la fila (tabla).
     * Proporciona un acceso directo a las vistas para que no se tengan que buscar repetidamente (findViewById)
     * cada vez que se recicla un ViewHolder, mejorando el rendimiento.
     *
     * @param itemView La vista de la fila individual (inflada de `item_table.xml`).
     */
    class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencia al TextView que muestra el nombre de la tabla.
        // Asegúrate de que R.id.tv_table_name exista en item_table.xml.
        val tvTableName: TextView = itemView.findViewById(com.example.clase.R.id.tv_table_name)
        // Referencia al LinearLayout que contendrá la lista de campos de la tabla.
        // Asegúrate de que R.id.ll_table_fields_container exista en item_table.xml.
        val llTableFieldsContainer: LinearLayout = itemView.findViewById(com.example.clase.R.id.ll_table_fields_container)
    }
}