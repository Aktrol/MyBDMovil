# Importación de tkinter para la interfaz gráfica
import tkinter as tk

# Importa componentes específicos de tkinter que se utilizarán
from tkinter import ttk, scrolledtext, messagebox

class MainView(tk.Tk):
    """Ventana principal de la aplicación que muestra la estructura de la base de datos y permite ejecutar consultas"""
    
    def __init__(self, controller):
        """
        Inicializa la ventana principal
        
        Args:
            controller: Referencia al AppController para comunicación
        """
        super().__init__()
        # Guarda referencia al controlador principal
        self.controller = controller
        
        # Configuración básica de la ventana
        self.title("Gestor de Base de Datos")
        self.geometry("1020x780")  # Tamaño inicial de la ventana
        
        # Maneja el cierre de la ventana para cerrar la conexión correctamente
        self.protocol("WM_DELETE_WINDOW", self.on_close)
        
        # Construye los componentes de la interfaz
        self.create_widgets()
        
        # Centra la ventana en la pantalla
        self.center_window()
    
    def center_window(self):
        """Centra la ventana en la pantalla"""
        # Actualiza la geometría para obtener dimensiones reales
        self.update_idletasks()
        width = self.winfo_width()
        height = self.winfo_height()
        
        # Calcula posición central
        x = (self.winfo_screenwidth() // 2) - (width // 2)
        y = (self.winfo_screenheight() // 2) - (height // 2)
        
        # Aplica nueva posición
        self.geometry(f'+{x}+{y}')
    
    def create_widgets(self):
        """Construye todos los componentes de la interfaz gráfica"""
        # Panel principal dividido en dos secciones
        main_paned = ttk.PanedWindow(self, orient=tk.HORIZONTAL)
        main_paned.pack(fill=tk.BOTH, expand=True)
        
        # ----- Panel izquierdo: Estructura de la base de datos -----
        left_frame = ttk.LabelFrame(main_paned, text="Estructura de la Base de Datos", padding=10)
        
        # Treeview para mostrar tablas y columnas
        self.tree = ttk.Treeview(left_frame, show="tree")  # Muestra solo la estructura jerárquica
        self.tree_scroll = ttk.Scrollbar(left_frame, orient=tk.VERTICAL, command=self.tree.yview)
        self.tree.configure(yscrollcommand=self.tree_scroll.set)
        
        # Organización de widgets en el panel izquierdo
        self.tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        self.tree_scroll.pack(side=tk.RIGHT, fill=tk.Y)
        
        # Añade panel izquierdo al panel principal
        main_paned.add(left_frame, weight=1)  # Peso 1 para relación 1:3 con panel derecho
        
        # ----- Panel derecho: Consulta y resultados -----
        right_frame = ttk.Frame(main_paned)
        main_paned.add(right_frame, weight=3)  # Mayor peso para más espacio
        
        # Área de consulta SQL
        query_frame = ttk.LabelFrame(right_frame, text="Editor SQL", padding=10)
        query_frame.pack(fill=tk.X, padx=10, pady=10)  # Anchura completa con margen
        
        # Editor de texto para escribir consultas SQL
        self.query_text = scrolledtext.ScrolledText(query_frame, height=8, font=("Consolas", 10))
        self.query_text.pack(fill=tk.BOTH, expand=True)
        
        # Panel de botones para el editor SQL
        button_frame = ttk.Frame(query_frame)
        button_frame.pack(fill=tk.X, pady=(10, 0))  # Margen superior
        
        # Botón para limpiar el editor
        self.clear_btn = ttk.Button(button_frame, text="Limpiar", command=self.on_clear)
        self.clear_btn.pack(side=tk.RIGHT, padx=5)
        
        # Botón para ejecutar la consulta
        self.execute_btn = ttk.Button(button_frame, text="Ejecutar", command=self.on_execute)
        self.execute_btn.pack(side=tk.RIGHT, padx=5)
        
        # ----- Área de resultados -----
        result_frame = ttk.LabelFrame(right_frame, text="Resultados", padding=10)
        result_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=(0, 10))  # Rellena todo el espacio
        
        # Treeview para mostrar resultados en formato tabla
        self.result_tree = ttk.Treeview(result_frame)
        
        # Barras de desplazamiento
        self.result_scroll_y = ttk.Scrollbar(result_frame, orient=tk.VERTICAL, command=self.result_tree.yview)
        self.result_scroll_x = ttk.Scrollbar(result_frame, orient=tk.HORIZONTAL, command=self.result_tree.xview)
        
        # Configuración de scrollbars
        self.result_tree.configure(
            yscrollcommand=self.result_scroll_y.set,
            xscrollcommand=self.result_scroll_x.set
        )
        
        # Organización de widgets en el área de resultados
        self.result_tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        self.result_scroll_y.pack(side=tk.RIGHT, fill=tk.Y)
        self.result_scroll_x.pack(side=tk.BOTTOM, fill=tk.X)
        
        # ----- Barra de estado -----
        self.status_var = tk.StringVar(value=" Listo")  # Variable para mensajes de estado
        status_bar = ttk.Label(self, textvariable=self.status_var, relief=tk.SUNKEN, anchor=tk.W)
        status_bar.pack(side=tk.BOTTOM, fill=tk.X)  # Barra en la parte inferior
    
    def load_database_structure(self, tables):
        """Carga la estructura de la base de datos en el panel izquierdo"""
        # Limpia cualquier contenido previo
        self.tree.delete(*self.tree.get_children())
        
        # Crea nodo raíz con el nombre de la base de datos
        root = self.tree.insert("", "end", text=self.controller.db_name, open=True)
        
        # Añade cada tabla como nodo hijo
        for table in tables:
            table_node = self.tree.insert(root, "end", text=table, open=False)
            
            # Obtiene columnas de cada tabla
            columns = self.controller.get_columns(table)
            
            # Añade cada columna como nodo hijo de la tabla
            for col_name, col_type in columns:
                self.tree.insert(table_node, "end", text=f"{col_name} ({col_type})")
    
    def on_execute(self):
        """Maneja el evento de ejecución de consulta SQL"""
        # Obtiene el texto completo del editor SQL
        query = self.query_text.get("1.0", tk.END).strip()
        
        # Verifica si la consulta está vacía
        if not query:
            messagebox.showwarning("Advertencia", "La consulta está vacía")
            return
        
        # Pasa la consulta al controlador para su ejecución
        self.controller.execute_query(query)
    
    def on_clear(self):
        """Limpia el editor SQL y los resultados previos"""
        # Borra contenido del editor SQL
        self.query_text.delete("1.0", tk.END)
        
        # Limpia resultados anteriores
        self.clear_results()
    
    def clear_results(self):
        """Limpia el área de resultados"""
        # Elimina todas las filas de la tabla de resultados
        for item in self.result_tree.get_children():
            self.result_tree.delete(item)
        
        # Reinicia las columnas
        self.result_tree["columns"] = ()
        
        # Actualiza estado
        self.status_var.set(" Resultados limpiados")
    
    def show_results(self, columns, results):
        """Muestra resultados de una consulta SELECT en formato tabla"""
        # Limpia resultados previos
        self.clear_results()
        
        # Configura las columnas del Treeview
        self.result_tree["columns"] = columns
        self.result_tree.column("#0", width=0, stretch=tk.NO)  # Oculta columna principal
        
        # Configura encabezados de columna
        for col in columns:
            self.result_tree.heading(col, text=col)  # Título de columna
            self.result_tree.column(col, width=100, anchor=tk.W)  # Ancho y alineación
        
        # Inserta los datos en la tabla
        for row in results:
            self.result_tree.insert("", tk.END, values=row)
        
        # Actualiza barra de estado con conteo de filas
        self.status_var.set(f" Resultados: {len(results)} filas")
    
    def show_message(self, message):
        """Muestra un mensaje informativo en la barra de estado y como diálogo"""
        self.status_var.set(f" {message}")
        messagebox.showinfo("Información", message)
    
    def show_error(self, message):
        """Muestra un mensaje de error en la barra de estado y como diálogo"""
        self.status_var.set(f" Error: {message}")
        messagebox.showerror("Error", message)
    
    def on_close(self):
        """Maneja el cierre de la ventana principal"""
        # Confirma si el usuario realmente quiere salir
        if messagebox.askokcancel("Salir", "¿Está seguro que desea salir?"):
            # Cierra la conexión a la base de datos
            self.controller.close_connection()
            
            # Destruye la ventana principal
            self.destroy()