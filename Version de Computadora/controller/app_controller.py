
from model.database import Database

from view.login_view import LoginView

from view.main_view import MainView

class AppController:
    
    def __init__(self):
        
        self.db = Database()
        
        self.db_name = ""
        
        # Crear ventana principal oculta
        self.main_view = MainView(self)
        
        self.main_view.withdraw()
        
        # Mostrar ventana de login
        self.login_view = LoginView(self.main_view, self)
        
        self.login_view.mainloop()
    
    def get_databases(self, user, password):
        """Obtiene bases de datos disponibles con las credenciales dadas"""
        return self.db.get_databases("localhost", user, password)
    
    def handle_login(self, user, password, db_name):
        
        if self.db.connect("localhost", user, password, db_name):
            
            self.db_name = db_name
            
            self.login_view.destroy()
            
            # Cargar estructura de la base de datos
            tables = self.db.get_tables()
            
            self.main_view.load_database_structure(tables)
            
            self.main_view.deiconify()
        
        else:
            
            self.login_view.show_error("Error de conexión. Verifique las credenciales")
    
    def get_columns(self, table_name):
        
        return self.db.get_columns(table_name)
    
    def execute_query(self, query):
        
        columns, result = self.db.execute_query(query)
        
        if columns is None:
            
            self.main_view.show_message(result)
        
        else:
            
            self.main_view.show_results(columns, result)
    
    def close_connection(self):
        
        self.db.close()