#
import mysql.connector
#
from mysql.connector import Error
#
class Database:
    #
    def __init__(self):
        #
        self.connection = None
        #
        self.db_name = ""
    #
    def connect(self, host, user, password, db_name):
        #
        try:
            #
            self.connection = mysql.connector.connect(
                #
                host=host,
                
                user=user,
                
                password=password,
                
                database=db_name
            )
            
            self.db_name = db_name
            
            return True
        
        except Error as e:
            
            print(f"Error de conexión: {e}")
            
            return False
    
    def get_databases(self, host, user, password):
        
        """Obtiene lista de bases de datos disponibles"""
        
        try:
            
            connection = mysql.connector.connect(
                
                host=host,
                
                user=user,
                
                password=password
            )
            
            cursor = connection.cursor()
            
            cursor.execute("SHOW DATABASES")
            
            databases = [db[0] for db in cursor.fetchall() if db[0] not in ('information_schema', 'mysql', 'performance_schema', 'sys')]
            
            cursor.close()
            
            connection.close()
            
            return databases
        
        except Error as e:
            
            print(f"Error obteniendo bases de datos: {e}")
            
            return []
    
    def get_tables(self):
        
        if not self.connection:
            
            return []
        
        try:
            
            cursor = self.connection.cursor()
            
            cursor.execute(f"SHOW TABLES FROM {self.db_name}")
            
            return [table[0] for table in cursor.fetchall()]
        
        except Error as e:
            
            print(f"Error obteniendo tablas: {e}")
            
            return []
    
    def get_columns(self, table_name):
        
        if not self.connection:
            
            return []
        
        try:
            
            cursor = self.connection.cursor()
            
            cursor.execute(f"DESCRIBE {table_name}")
            
            return [(col[0], col[1]) for col in cursor.fetchall()]
        
        except Error as e:
            
            print(f"Error obteniendo columnas: {e}")
            
            return []
    
    def execute_query(self, query):
        
        if not self.connection:
            
            return None, "No hay conexión a la base de datos"
        
        try:
            
            cursor = self.connection.cursor()
            
            cursor.execute(query)
            
            if query.strip().lower().startswith("select"):
                
                result = cursor.fetchall()
                
                columns = [desc[0] for desc in cursor.description]
                
                return columns, result
            
            else:
                
                self.connection.commit()
                
                return None, f"Operación completada. Filas afectadas: {cursor.rowcount}"
                
        except Error as e:
            
            return None, f"Error SQL: {e}"
    
    def close(self):
        
        if self.connection and self.connection.is_connected():
            
            self.connection.close()