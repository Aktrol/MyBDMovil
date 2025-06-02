#importacion de tkinter para la interfaz grafica
import tkinter as tk
#importa algunas cosas que se ocuparan en la interfaz grafica
from tkinter import ttk, scrolledtext, messagebox
#Se crea una clase para tener la interfaz grafica y esta ocupara el archivo controlador
class MainView(tk.Tk):
    #se crea una funcion principal 
    def __init__(self, controller):
        #
        super().__init__()
        #declara el cotrolador para la pagina principal
        self.controller = controller
        #Se declara como va a aparecer el titulo de la pagina principal
        self.title("Gestor de Base de Datos")
        #se declaran los parametros que va a tener la aplicacion 
        self.geometry("1020x780")
        #declara.- un protocolo para salir de la pantalla
        self.protocol("WM_DELETE_WINDOW", self.on_close)
        #declara los widgets
        self.create_widgets()
        #centra la pantalla creada para que cuando se ejecute este en medio
        self.center_window()
    #crea una nueva funcion para centrar las pantallas
    def center_window(self):
        self.update_idletasks()
        width = self.winfo_width()
        height = self.winfo_height()
        #hace los calculos en x para saber donde es el medio
        x = (self.winfo_screenwidth() // 2) - (width // 2)
        #hace los calculos en y para saber donde es el medio
        y = (self.winfo_screenheight() // 2) - (height // 2)
        #una vez con los calculos hace la geometria que tendra la pantalla para poder tener el estandar de la pantalla
        self.geometry(f'+{x}+{y}')
    
    def create_widgets(self):
        # Paneles principales
        main_paned = ttk.PanedWindow(self, orient=tk.HORIZONTAL)
        #
        main_paned.pack(fill=tk.BOTH, expand=True)
        
        # Panel izquierdo: Tablas
        left_frame = ttk.LabelFrame(main_paned, text="Estructura de la Base de Datos", padding=10)
        #
        self.tree = ttk.Treeview(left_frame, show="tree")
        #
        self.tree_scroll = ttk.Scrollbar(left_frame, orient=tk.VERTICAL, command=self.tree.yview)
        #
        self.tree.configure(yscrollcommand=self.tree_scroll.set)
        #
        self.tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        #
        self.tree_scroll.pack(side=tk.RIGHT, fill=tk.Y)
        #
        main_paned.add(left_frame, weight=1)
        
        # Panel derecho: Consulta y resultados
        right_frame = ttk.Frame(main_paned)
        #
        main_paned.add(right_frame, weight=3)
        
        # Área de consulta
        query_frame = ttk.LabelFrame(right_frame, text="Editor SQL", padding=10)
        #
        query_frame.pack(fill=tk.X, padx=10, pady=10)
        #
        self.query_text = scrolledtext.ScrolledText(query_frame, height=8, font=("Consolas", 10))
        #
        self.query_text.pack(fill=tk.BOTH, expand=True)
        #
        button_frame = ttk.Frame(query_frame)
        #
        button_frame.pack(fill=tk.X, pady=(10, 0))
        #
        self.clear_btn = ttk.Button(button_frame, text="Limpiar", command=self.on_clear)
        #
        self.clear_btn.pack(side=tk.RIGHT, padx=5)
        #
        self.execute_btn = ttk.Button(button_frame, text="Ejecutar", command=self.on_execute)
        #
        self.execute_btn.pack(side=tk.RIGHT, padx=5)
        
        # Resultados
        result_frame = ttk.LabelFrame(right_frame, text="Resultados", padding=10)
        #
        result_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=(0, 10))
        #
        self.result_tree = ttk.Treeview(result_frame)
        #
        self.result_scroll_y = ttk.Scrollbar(result_frame, orient=tk.VERTICAL, command=self.result_tree.yview)
        #
        self.result_scroll_x = ttk.Scrollbar(result_frame, orient=tk.HORIZONTAL, command=self.result_tree.xview)
        #
        self.result_tree.configure(yscrollcommand=self.result_scroll_y.set, xscrollcommand=self.result_scroll_x.set)
        #
        self.result_tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        #
        self.result_scroll_y.pack(side=tk.RIGHT, fill=tk.Y)
        #
        self.result_scroll_x.pack(side=tk.BOTTOM, fill=tk.X)
        
        # Barra de estado
        self.status_var = tk.StringVar(value=" Listo")
        #
        status_bar = ttk.Label(self, textvariable=self.status_var, relief=tk.SUNKEN, anchor=tk.W)
        #
        status_bar.pack(side=tk.BOTTOM, fill=tk.X)
    #
    def load_database_structure(self, tables):
        #
        self.tree.delete(*self.tree.get_children())
        #
        root = self.tree.insert("", "end", text=self.controller.db_name, open=True)
        #
        for table in tables:
            #
            table_node = self.tree.insert(root, "end", text=table, open=False)
            #
            columns = self.controller.get_columns(table)
            #
            for col_name, col_type in columns:
                #
                self.tree.insert(table_node, "end", text=f"{col_name} ({col_type})")
    #
    def on_execute(self):
        #
        query = self.query_text.get("1.0", tk.END).strip()
        #
        if not query:
            #
            messagebox.showwarning("Advertencia", "La consulta está vacía")
            #
            return
        #
        self.controller.execute_query(query)
    #
    def on_clear(self):
        #
        self.query_text.delete("1.0", tk.END)
        #
        self.clear_results()
    #
    def clear_results(self):
        #
        for item in self.result_tree.get_children():
            #
            self.result_tree.delete(item)
        #
        self.result_tree["columns"] = ()
        #
        self.status_var.set(" Resultados limpiados")
    #
    def show_results(self, columns, results):
        #
        self.clear_results()
        
        # Configurar columnas
        self.result_tree["columns"] = columns
        #
        self.result_tree.column("#0", width=0, stretch=tk.NO)
        #
        for col in columns:
            #
            self.result_tree.heading(col, text=col)
            #
            self.result_tree.column(col, width=100, anchor=tk.W)
        
        # Insertar datos
        for row in results:
            #
            self.result_tree.insert("", tk.END, values=row)
        #
        self.status_var.set(f" Resultados: {len(results)} filas")
    #
    def show_message(self, message):
        #
        self.status_var.set(f" {message}")
        #
        messagebox.showinfo("Información", message)
    #
    def show_error(self, message):
        #
        self.status_var.set(f" Error: {message}")
        #
        messagebox.showerror("Error", message)
    #
    def on_close(self):
        #
        if messagebox.askokcancel("Salir", "¿Está seguro que desea salir?"):
            #
            self.controller.close_connection()
            #
            self.destroy()