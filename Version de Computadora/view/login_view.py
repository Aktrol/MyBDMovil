# Importa el módulo tkinter para la interfaz gráfica
import tkinter as tk

# Importa componentes adicionales de tkinter
from tkinter import ttk, messagebox

class LoginView(tk.Toplevel):
    """Ventana de inicio de sesión para conectar a bases de datos MySQL"""
    
    def __init__(self, parent, controller):
        """
        Inicializa la ventana de login
        
        Args:
            parent: Ventana padre (MainView)
            controller: Referencia al AppController para comunicación
        """
        # Inicializa como ventana secundaria (Toplevel)
        super().__init__(parent)
        
        # Guarda referencia al controlador principal
        self.controller = controller
        
        # Configuración básica de la ventana
        self.title("Login - Gestor de Base de Datos")
        self.geometry("400x300")
        self.resizable(False, False)  # Ventana no redimensionable
        
        # Centra la ventana en la pantalla
        self.center_window()
        
        # Construye los componentes de la interfaz
        self.create_widgets()
        
        # Hace esta ventana modal (captura todo el foco)
        self.grab_set()
        
        # Intento inicial de obtener bases de datos después de 100ms
        self.after(100, self.try_get_databases)
        
    def center_window(self):
        """Centra la ventana en la pantalla"""
        # Actualiza la geometría para obtener dimensiones reales
        self.update_idletasks()
        
        # Calcula dimensiones y posición
        width = self.winfo_width()
        height = self.winfo_height()
        x = (self.winfo_screenwidth() // 2) - (width // 2)
        y = (self.winfo_screenheight() // 2) - (height // 2)
        
        # Aplica nueva posición
        self.geometry(f'+{x}+{y}')
    
    def try_get_databases(self):
        """Intenta obtener bases de datos con credenciales vacías"""
        # Solicita bases de datos con usuario y contraseña vacíos
        dbs = self.controller.get_databases("", "")
        
        # Si se obtuvieron resultados
        if dbs:
            # Actualiza el combobox con las bases de datos
            self.db_combo['values'] = dbs
            
            # Selecciona la primera base de datos si hay disponibles
            if dbs:
                self.db_combo.current(0)
    
    def create_widgets(self):
        """Construye todos los componentes de la interfaz gráfica"""
        # Marco principal para organizar elementos
        main_frame = ttk.Frame(self, padding=20)
        main_frame.pack(fill=tk.BOTH, expand=True)
        
        # Etiqueta y campo para usuario
        ttk.Label(main_frame, text="Usuario:").grid(row=0, column=0, padx=5, pady=5, sticky=tk.W)
        self.user_entry = ttk.Entry(main_frame)
        self.user_entry.grid(row=0, column=1, padx=5, pady=5, sticky=tk.EW)
        self.user_entry.focus()  # Foco inicial en este campo
        
        # Etiqueta y campo para contraseña
        ttk.Label(main_frame, text="Contraseña:").grid(row=1, column=0, padx=5, pady=5, sticky=tk.W)
        self.password_entry = ttk.Entry(main_frame, show="*")  # Muestra asteriscos
        self.password_entry.grid(row=1, column=1, padx=5, pady=5, sticky=tk.EW)
        
        # Etiqueta y combobox para selección de base de datos
        ttk.Label(main_frame, text="Base de Datos:").grid(row=2, column=0, padx=5, pady=5, sticky=tk.W)
        self.db_combo = ttk.Combobox(main_frame, state="readonly")  # Solo lectura
        self.db_combo.grid(row=2, column=1, padx=5, pady=5, sticky=tk.EW)
        
        # Marco para botón de actualización
        refresh_frame = ttk.Frame(main_frame)
        refresh_frame.grid(row=3, column=0, columnspan=2, pady=5, sticky=tk.EW)
        
        # Botón para actualizar lista de bases de datos
        self.refresh_btn = ttk.Button(
            refresh_frame, 
            text="Actualizar Bases Disponibles",
            command=self.on_refresh  # Asocia función al evento click
        )
        self.refresh_btn.pack(side=tk.RIGHT)  # Alineado a la derecha
        
        # Marco para botones principales
        button_frame = ttk.Frame(main_frame)
        button_frame.grid(row=4, column=0, columnspan=2, pady=15)
        
        # Botón de conexión
        self.login_btn = ttk.Button(button_frame, text="Conectar", command=self.on_login)
        self.login_btn.pack(side=tk.RIGHT, padx=5)
        
        # Botón de cancelar/cerrar
        self.cancel_btn = ttk.Button(button_frame, text="Cancelar", command=self.destroy)
        self.cancel_btn.pack(side=tk.RIGHT, padx=5)
        
        # Configura expansión de la columna de entrada
        main_frame.columnconfigure(1, weight=1)
    
    def on_refresh(self):
        """Actualiza la lista de bases de datos con las credenciales actuales"""
        # Obtiene credenciales de los campos
        user = self.user_entry.get()
        password = self.password_entry.get()
        
        # Solicita bases de datos disponibles al controlador
        dbs = self.controller.get_databases(user, password)
        
        if dbs:
            # Actualiza combobox con nuevas bases
            self.db_combo['values'] = dbs
            
            # Selecciona la primera si hay disponibles
            if dbs:
                self.db_combo.current(0)
        else:
            # Muestra advertencia si no se encontraron bases
            messagebox.showwarning(
                "Advertencia", 
                "No se encontraron bases de datos disponibles\nVerifique las credenciales"
            )
    
    def on_login(self):
        """Maneja el evento de clic en el botón Conectar"""
        # Obtiene valores de los campos
        user = self.user_entry.get()
        password = self.password_entry.get()
        db = self.db_combo.get()
        
        # Valida que todos los campos estén completos
        if not all([user, password, db]):
            messagebox.showerror("Error", "Todos los campos son obligatorios")
            return
        
        # Pasa credenciales al controlador para manejar la conexión
        self.controller.handle_login(user, password, db)
    
    def show_error(self, message):
        """Muestra un mensaje de error en un cuadro de diálogo"""
        messagebox.showerror("Error de conexión", message)
    
    def set_databases(self, databases):
        """Actualiza el combobox con la lista de bases de datos"""
        self.db_combo['values'] = databases
        if databases:
            self.db_combo.current(0)  # Selecciona la primera opción