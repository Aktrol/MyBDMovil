
import tkinter as tk

from tkinter import ttk, messagebox

class LoginView(tk.Toplevel):
    
    def __init__(self, parent, controller):
        
        super().__init__(parent)
        
        self.controller = controller
        
        self.title("Login - Gestor de Base de Datos")
        
        self.geometry("400x300")
        
        self.resizable(False, False)
        
        self.center_window()
        
        self.create_widgets()
        
        self.grab_set()
        
        # Intento inicial de obtener bases de datos
        self.after(100, self.try_get_databases)
        
    def center_window(self):
        
        self.update_idletasks()
        
        width = self.winfo_width()
        
        height = self.winfo_height()
        
        x = (self.winfo_screenwidth() // 2) - (width // 2)
        
        y = (self.winfo_screenheight() // 2) - (height // 2)
        
        self.geometry(f'+{x}+{y}')
    
    def try_get_databases(self):
        """Intenta obtener bases de datos con credenciales vacías"""
        dbs = self.controller.get_databases("", "")
        
        if dbs:
            
            self.db_combo['values'] = dbs
            
            if dbs:
                
                self.db_combo.current(0)
    
    def create_widgets(self):
        
        main_frame = ttk.Frame(self, padding=20)
        
        main_frame.pack(fill=tk.BOTH, expand=True)
        
        ttk.Label(main_frame, text="Usuario:").grid(row=0, column=0, padx=5, pady=5, sticky=tk.W)
        
        self.user_entry = ttk.Entry(main_frame)
        
        self.user_entry.grid(row=0, column=1, padx=5, pady=5, sticky=tk.EW)
        
        self.user_entry.focus()
        
        ttk.Label(main_frame, text="Contraseña:").grid(row=1, column=0, padx=5, pady=5, sticky=tk.W)
        
        self.password_entry = ttk.Entry(main_frame, show="*")
        
        self.password_entry.grid(row=1, column=1, padx=5, pady=5, sticky=tk.EW)
        
        ttk.Label(main_frame, text="Base de Datos:").grid(row=2, column=0, padx=5, pady=5, sticky=tk.W)
        
        # Combobox para seleccionar base de datos
        self.db_combo = ttk.Combobox(main_frame, state="readonly")
        
        self.db_combo.grid(row=2, column=1, padx=5, pady=5, sticky=tk.EW)
        
        # Botón para actualizar lista de bases de datos
        refresh_frame = ttk.Frame(main_frame)
        
        refresh_frame.grid(row=3, column=0, columnspan=2, pady=5, sticky=tk.EW)
        
        self.refresh_btn = ttk.Button(
            
            refresh_frame, 
            
            text="Actualizar Bases Disponibles",
            
            command=self.on_refresh
        )
        
        self.refresh_btn.pack(side=tk.RIGHT)
        
        
        button_frame = ttk.Frame(main_frame)
        
        button_frame.grid(row=4, column=0, columnspan=2, pady=15)
        
        self.login_btn = ttk.Button(button_frame, text="Conectar", command=self.on_login)
        
        self.login_btn.pack(side=tk.RIGHT, padx=5)
        
        self.cancel_btn = ttk.Button(button_frame, text="Cancelar", command=self.destroy)
        
        self.cancel_btn.pack(side=tk.RIGHT, padx=5)
        
        main_frame.columnconfigure(1, weight=1)
    
    def on_refresh(self):
        """Actualiza la lista de bases de datos con las credenciales actuales"""
        user = self.user_entry.get()
        
        password = self.password_entry.get()
        
        dbs = self.controller.get_databases(user, password)
        
        if dbs:
            
            self.db_combo['values'] = dbs
            
            if dbs:
                
                self.db_combo.current(0)
        else:
            
            messagebox.showwarning(
                "Advertencia", 
                "No se encontraron bases de datos disponibles\nVerifique las credenciales"
            )
    
    def on_login(self):
        
        user = self.user_entry.get()
        
        password = self.password_entry.get()
        
        db = self.db_combo.get()
        
        if not all([user, password, db]):
            
            messagebox.showerror("Error", "Todos los campos son obligatorios")
            
            return
        
        self.controller.handle_login(user, password, db)
    
    def show_error(self, message):
        
        messagebox.showerror("Error de conexión", message)
    
    def set_databases(self, databases):
        """Actualiza el combobox con la lista de bases de datos"""
        
        self.db_combo['values'] = databases
        
        if databases:
            
            self.db_combo.current(0)