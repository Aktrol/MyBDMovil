# Consulta de Bases de Datos - Versión Móvil y Escritorio

Este proyecto ofrece una solución multiplataforma para consultar bases de datos, con una **versión móvil para Android** y una **versión de escritorio para computadoras**.

## Características Principales

### Versión Android (Android Studio)
- 📱 Interfaz móvil optimizada para dispositivos táctiles
- 🔍 Consultas SQL intuitivas con historial
- 📊 Visualización de resultados en formato tabla
- 🔐 Gestión segura de credenciales
- 📂 Administración de conexiones guardadas

### Versión Escritorio (Python)
- 💻 Interfaz gráfica moderna con PyQt/Tkinter
- 🚀 Soporte para múltiples motores de bases de datos
- 📝 Editor SQL avanzado con resaltado de sintaxis
- 📡 Conexión a bases de datos locales y remotas

## Requisitos del Sistema

### Para la versión Android:
- Android 8.0 (Oreo) o superior
- 2 GB de RAM mínimo
- 50 MB de espacio libre

### Para la versión de escritorio:
- Python 3.8+
- Sistemas compatibles:
  - Windows 10/11
  - macOS 10.15+
  - Linux (distribuciones basadas en Debian/Ubuntu)

## Instalación

### Versión Android:
1. Descarga el APK desde [Releases](https://github.com/tuusuario/turepo/releases)
2. Habilita "Orígenes desconocidos" en ajustes de seguridad
3. Instala la aplicación
4. Concede los permisos necesarios

## Uso Básico

1. **Configurar conexión:**
   - Ingresa los detalles de tu servidor de base de datos
   - Prueba la conexión antes de guardar

2. **Ejecutar consultas:**
   - Escribe tus consultas SQL en el editor
   - Selecciona la base de datos objetivo
   - Ejecuta con el botón ▶️ o Ctrl+Enter

## Estructura del Proyecto
```
├── android-app/          # Versión móvil (Android Studio)
│   ├── app/              # Código principal de la aplicación
│   ├── gradle/           # Configuración de Gradle
│   └── ...               
```
