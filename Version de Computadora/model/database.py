# Importa el conector oficial de MySQL para Python
import mysql.connector

# Importa la clase Error para manejar excepciones específicas de MySQL
from mysql.connector import Error

class Database:
    """Clase que maneja todas las operaciones de conexión y consulta con MySQL"""
    
    def __init__(self):
        """Inicializa la conexión como nula y el nombre de BD vacío"""
        # Almacenará la conexión activa a la base de datos
        self.connection = None
        
        # Nombre de la base de datos actual
        self.db_name = ""
    
    def connect(self, host, user, password, db_name):
        """Establece conexión con una base de datos MySQL específica"""
        try:
            # Crea una nueva conexión usando los parámetros proporcionados
            self.connection = mysql.connector.connect(
                host=host,          # Dirección del servidor
                user=user,           # Nombre de usuario
                password=password,   # Contraseña
                database=db_name     # Base de datos a utilizar
            )
            
            # Guarda el nombre de la base de datos para referencia futura
            self.db_name = db_name
            
            # Retorna True para indicar conexión exitosa
            return True
        
        except Error as e:
            # Maneja errores de conexión mostrando el mensaje
            print(f"Error de conexión: {e}")
            
            # Retorna False para indicar fallo en la conexión
            return False
    
    def get_databases(self, host, user, password):
        """Obtiene lista de bases de datos disponibles en el servidor"""
        try:
            # Crea una conexión temporal sin especificar base de datos
            connection = mysql.connector.connect(
                host=host,
                user=user,
                password=password
            )
            
            # Crea un cursor para ejecutar consultas
            cursor = connection.cursor()
            
            # Ejecuta comando SQL para listar bases de datos
            cursor.execute("SHOW DATABASES")
            
            # Filtra bases de datos del sistema que no son relevantes para el usuario
            databases = [db[0] for db in cursor.fetchall() 
                         if db[0] not in ('information_schema', 'mysql', 'performance_schema', 'sys')]
            
            # Cierra cursor y conexión temporal
            cursor.close()
            connection.close()
            
            return databases
        
        except Error as e:
            # Maneja errores mostrando mensaje y retorna lista vacía
            print(f"Error obteniendo bases de datos: {e}")
            return []
    
    def get_tables(self):
        """Obtiene lista de tablas en la base de datos actualmente conectada"""
        # Verifica si hay conexión activa
        if not self.connection:
            return []
        
        try:
            cursor = self.connection.cursor()
            
            # Ejecuta comando para listar tablas de la base de datos actual
            cursor.execute(f"SHOW TABLES FROM {self.db_name}")
            
            # Extrae nombres de tablas del resultado
            return [table[0] for table in cursor.fetchall()]
        
        except Error as e:
            print(f"Error obteniendo tablas: {e}")
            return []
    
    def get_columns(self, table_name):
        """Obtiene metadatos de columnas para una tabla específica"""
        if not self.connection:
            return []
        
        try:
            cursor = self.connection.cursor()
            
            # Obtiene estructura de la tabla (nombre columna + tipo dato)
            cursor.execute(f"DESCRIBE {table_name}")
            
            # Retorna lista de tuplas (nombre_columna, tipo_dato)
            return [(col[0], col[1]) for col in cursor.fetchall()]
        
        except Error as e:
            print(f"Error obteniendo columnas: {e}")
            return []
    
    def execute_query(self, query):
        """Ejecuta una consulta SQL y maneja diferentes tipos de resultados"""
        if not self.connection:
            return None, "No hay conexión a la base de datos"
        
        try:
            cursor = self.connection.cursor()
            
            # Ejecuta la consulta proporcionada
            cursor.execute(query)
            
            # Verifica si es una consulta SELECT
            if query.strip().lower().startswith("select"):
                # Obtiene todos los resultados
                result = cursor.fetchall()
                
                # Obtiene nombres de columnas de la metadata
                columns = [desc[0] for desc in cursor.description]
                
                # Retorna columnas + resultados
                return columns, result
            
            else:
                # Para consultas no SELECT (INSERT, UPDATE, DELETE, etc.)
                # Confirma los cambios en la base de datos
                self.connection.commit()
                
                # Retorna mensaje con conteo de filas afectadas
                return None, f"Operación completada. Filas afectadas: {cursor.rowcount}"
                
        except Error as e:
            # Retorna mensaje de error para consultas fallidas
            return None, f"Error SQL: {e}"
    
    def close(self):
        """Cierra la conexión con la base de datos si está activa"""
        if self.connection and self.connection.is_connected():
            self.connection.close()