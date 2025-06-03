package com.example.clase.adapter // Define el paquete al que pertenece este archivo.

import android.graphics.Typeface // Importa la clase Typeface para estilos de fuente.
import android.view.Gravity // Importa la clase Gravity para alinear el contenido de las vistas.
import android.view.LayoutInflater // Importa LayoutInflater para inflar diseños XML en objetos View.
import android.view.View // Importa la clase base para todos los componentes de la interfaz de usuario.
import android.view.ViewGroup // Importa ViewGroup, la clase base para los diseños que contienen otras vistas.
import android.widget.LinearLayout // Importa LinearLayout para organizar vistas en una sola dirección.
import android.widget.TextView // Importa TextView para mostrar texto.
//sadfasdfasdf
import androidx.core.content.ContextCompat // Importa ContextCompat para obtener recursos de color de forma compatible con versiones anteriores de Android.
import androidx.recyclerview.widget.RecyclerView // Importa RecyclerView, una vista eficiente para mostrar grandes conjuntos de datos.
import com.example.clase.R // Importa la clase R generada automáticamente que contiene IDs de recursos.
import com.example.clase.databinding.ItemQueryRowBinding // Asumo que esta clase es generada por ViewBinding para item_query_row.xml. Aunque no se usa directamente en este código, se mantiene si es parte de un uso futuro o contextual.

/**
 * Adaptador para RecyclerView que muestra los resultados de una consulta (por ejemplo, de una base de datos)
 * en un formato tabular. Incluye una fila de encabezados y filas de datos, alternando colores de fondo
 * para las filas de datos para mejorar la legibilidad.
 *
 * @param headers Una lista de cadenas que representan los encabezados de las columnas.
 * @param data Una lista de listas de cadenas, donde cada lista interna representa una fila de datos.
 */
class QueryResultAdapter(private var headers: List<String>, private var data: List<List<String>>) :
    RecyclerView.Adapter<QueryResultAdapter.QueryResultViewHolder>() {

    // Constantes para el espaciado y los tipos de vista.
    private val CELL_PADDING = 16 // Relleno en píxeles para cada celda (TextView).
    private val VIEW_TYPE_HEADER = 0 // Tipo de vista para la fila de encabezados.
    private val VIEW_TYPE_DATA = 1 // Tipo de vista para las filas de datos.

    /**
     * Devuelve el tipo de vista para el elemento en la posición dada.
     * Utilizado por RecyclerView para determinar qué ViewHolder usar.
     * La primera posición (0) es siempre para el encabezado, si hay encabezados.
     *
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     * @return [VIEW_TYPE_HEADER] si es la primera posición y hay encabezados, de lo contrario [VIEW_TYPE_DATA].
     */
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_DATA
    }

    /**
     * Crea y devuelve un [QueryResultViewHolder] nuevo cuando RecyclerView necesita uno nuevo.
     * Este método infla el diseño `item_query_row.xml` para cada fila.
     *
     * @param parent El ViewGroup al que se adjuntará la nueva vista después de ser inflada.
     * @param viewType El tipo de vista del nuevo View.
     * @return Una nueva instancia de [QueryResultViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_query_row, parent, false)
        return QueryResultViewHolder(view)
    }

    /**
     * Rellena un [QueryResultViewHolder] con datos en la posición especificada.
     * Este método se encarga de crear dinámicamente TextViews para cada celda
     * y aplicarle estilos (negrita, colores de fondo/texto) según si es una fila de encabezado
     * o una fila de datos (alternando colores para par/impar).
     *
     * @param holder El ViewHolder que debe actualizarse para representar el contenido del elemento
     * en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    override fun onBindViewHolder(holder: QueryResultViewHolder, position: Int) {
        // Limpia cualquier vista existente en el contenedor para evitar duplicados al reciclar.
        holder.llQueryRowContainer.removeAllViews()

        val rowData: List<String>
        // Determina si la fila actual es la fila de encabezados.
        val isHeaderRow = (position == 0 && headers.isNotEmpty())

        // Asigna los datos correctos para la fila actual (encabezados o datos de fila).
        if (isHeaderRow) {
            rowData = headers
        } else {
            // Ajusta la posición si hay una fila de encabezado.
            rowData = data[position - (if (headers.isNotEmpty()) 1 else 0)]
        }

        val context = holder.itemView.context // Obtiene el contexto para acceder a los recursos.

        // Define los parámetros de diseño para cada TextView (celda).
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // Ancho: ajustar al contenido.
            LinearLayout.LayoutParams.WRAP_CONTENT  // Alto: ajustar al contenido.
        )
        lp.setMargins(0, 0, CELL_PADDING, 0) // Establece un margen a la derecha para espaciar las celdas.

        // Itera sobre los valores de cada celda en la fila actual.
        rowData.forEachIndexed { index, cellValue ->
            // Crea un nuevo TextView para cada celda.
            val textView = TextView(context).apply {
                layoutParams = lp // Aplica los parámetros de diseño definidos.
                text = cellValue // Establece el texto de la celda.
                setPadding(CELL_PADDING, CELL_PADDING / 2, CELL_PADDING, CELL_PADDING / 2) // Aplica relleno.
                gravity = Gravity.CENTER_VERTICAL // Centra el texto verticalmente.
                textSize = 14f // Establece el tamaño del texto.

                // Aplica estilos diferentes para la fila de encabezados y las filas de datos.
                if (isHeaderRow) {
                    setTypeface(null, Typeface.BOLD) // Texto en negrita para los encabezados.
                    // Establece el color de texto y fondo para los encabezados usando colores de colors.xml.
                    setTextColor(ContextCompat.getColor(context, R.color.header_text_color))
                    setBackgroundColor(ContextCompat.getColor(context, R.color.header_bg_color))
                } else {
                    // Establece el color de texto para las filas de datos.
                    setTextColor(ContextCompat.getColor(context, R.color.row_text_color))
                    // Alterna los colores de fondo para las filas de datos (par/impar).
                    if (index % 2 == 0) { // Si el índice de la celda es par
                        setBackgroundColor(ContextCompat.getColor(context, R.color.row_bg_color_even))
                    } else { // Si el índice de la celda es impar
                        setBackgroundColor(ContextCompat.getColor(context, R.color.row_bg_color_odd))
                    }
                }
            }
            // Agrega el TextView al contenedor de la fila.
            holder.llQueryRowContainer.addView(textView)
        }
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos del adaptador.
     * Incluye una fila adicional para los encabezados si la lista de encabezados no está vacía.
     *
     * @return El número total de filas a mostrar.
     */
    override fun getItemCount(): Int {
        // Si hay encabezados, es el tamaño de los datos + 1 (para la fila de encabezados).
        // De lo contrario, es solo el tamaño de los datos.
        return if (headers.isNotEmpty()) data.size + 1 else data.size
    }

    /**
     * Actualiza los datos que el adaptador está mostrando y notifica a RecyclerView
     * que el conjunto de datos ha cambiado, lo que provocará que la vista se actualice.
     *
     * @param newHeaders La nueva lista de encabezados.
     * @param newData La nueva lista de listas de datos.
     */
    fun updateResults(newHeaders: List<String>, newData: List<List<String>>) {
        this.headers = newHeaders // Actualiza los encabezados del adaptador.
        this.data = newData // Actualiza los datos del adaptador.
        notifyDataSetChanged() // Notifica a RecyclerView que los datos han cambiado para que se redibuje.
    }

    /**
     * Clase interna ViewHolder que contiene referencias a las vistas de cada elemento de la fila.
     * Proporciona un acceso directo a las vistas para que no se tengan que buscar repetidamente (findViewById)
     * cada vez que se recicla un ViewHolder.
     *
     * @param itemView La vista de la fila individual (inflada de `item_query_row.xml`).
     */
    class QueryResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencia al LinearLayout que contendrá las celdas (TextViews) de la fila.
        val llQueryRowContainer: LinearLayout = itemView.findViewById(R.id.ll_query_row_container)
    }
}
