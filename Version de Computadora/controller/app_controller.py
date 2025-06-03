# Importa la clase Database del módulo model.database para manejar operaciones de base de datos
from model.database import Database

# Importa la clase LoginView del módulo view.login_view para la interfaz de inicio de sesión
from view.login_view import LoginView

# Importa la clase MainView del módulo view.main_view para la interfaz principal
from view.main_view import MainView

class AppController:
    """Controlador principal que coordina la lógica entre el modelo y las vistas"""
    
    def __init__(self):
        """Inicializa la aplicación, creando instancias y configurando vistas"""
        
        # Instancia para manejar operaciones de base de datos
        self.db = Database()
        
        # Nombre de la base de datos actual (inicialmente vacío)
        self.db_name = ""
        
        # Crear ventana principal oculta inicialmente
        self.main_view = MainView(self)  # Pasa referencia al controlador
        
        # Oculta la ventana principal hasta que el login sea exitoso
        self.main_view.withdraw()
        
        # Crear y mostrar ventana de login
        self.login_view = LoginView(self.main_view, self)  # Ventana padre y controlador
        
        # Inicia el loop principal de la aplicación en la ventana de login
        self.login_view.mainloop()
    
    def get_databases(self, user, password):
        """Obtiene bases de datos disponibles usando credenciales de usuario"""
        # Solicita al modelo la lista de bases de datos disponibles
        return self.db.get_databases("localhost", user, password)
    
    def handle_login(self, user, password, db_name):
        """Maneja el proceso de autenticación y conexión a la base de datos"""
        
        # Intenta conectar a la base de datos especificada
        if self.db.connect("localhost", user, password, db_name):
            # Almacena el nombre de la base de datos seleccionada
            self.db_name = db_name
            
            # Cierra y elimina la ventana de login
            self.login_view.destroy()
            
            # Obtiene las tablas de la base de datos conectada
            tables = self.db.get_tables()
            
            # Carga la estructura de la base de datos en la vista principal
            self.main_view.load_database_structure(tables)
            
            # Muestra la ventana principal previamente oculta
            self.main_view.deiconify()
        
        else:
            # Muestra mensaje de error si la conexión falla
            self.login_view.show_error("Error de conexión. Verifique las credenciales")
    
    def get_columns(self, table_name):
        """Obtiene las columnas de una tabla específica"""
        # Solicita al modelo la metadata de las columnas
        return self.db.get_columns(table_name)
    
    def execute_query(self, query):
        """Ejecuta una consulta SQL y maneja los resultados"""
        
        # Ejecuta la consulta a través del modelo
        columns, result = self.db.execute_query(query)
        
        # Verifica si es una consulta que no retorna datos (ej: INSERT, UPDATE)
        if columns is None:
            # Muestra mensaje informativo en la vista principal
            self.main_view.show_message(result)
        
        else:
            # Muestra resultados en formato de tabla en la vista principal
            self.main_view.show_results(columns, result)
    
    def close_connection(self):
        """Cierra la conexión con la base de datos al salir de la aplicación"""
        self.db.close()