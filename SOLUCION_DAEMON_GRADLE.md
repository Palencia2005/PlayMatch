# 🔧 Solución: Error del Daemon de Gradle

## Problema
El daemon de Gradle se termina inesperadamente con el error:
```
The daemon has terminated unexpectedly on startup attempt #1 with error code: 0
```

## Soluciones Aplicadas

### 1. ✅ Aumento de Memoria
Se aumentó la memoria del daemon de Gradle de 2048m a 4096m en `gradle.properties`:
```
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
```

### 2. ✅ Limpieza de Caché
Se limpió el caché de Gradle que podría estar corrupto.

### 3. ✅ Configuración del Daemon
Se habilitaron las siguientes opciones:
- `org.gradle.daemon=true` - Habilita el daemon
- `org.gradle.parallel=true` - Compilación en paralelo
- `org.gradle.caching=true` - Caché de compilación

## Pasos Adicionales Si El Problema Persiste

### Opción 1: Reiniciar Android Studio
1. Cierra completamente Android Studio
2. Abre el Administrador de Tareas (Ctrl + Shift + Esc)
3. Busca procesos de Java/Gradle y termínalos
4. Reinicia Android Studio

### Opción 2: Verificar Versión de Java
El proyecto requiere Java 11 o superior. Verifica:
1. En Android Studio: **File > Project Structure > SDK Location**
2. Verifica que **JDK location** apunte a Java 11 o superior
3. O en terminal: `java -version`

### Opción 3: Deshabilitar el Daemon Temporalmente
Si el problema persiste, puedes deshabilitar el daemon temporalmente:
1. Edita `gradle.properties`
2. Cambia: `org.gradle.daemon=false`
3. Sincroniza el proyecto

### Opción 4: Reinstalar Gradle Wrapper
```bash
.\gradlew.bat wrapper --gradle-version 8.13 --distribution-type bin
```

### Opción 5: Verificar Antivirus
A veces el antivirus bloquea el daemon de Gradle:
1. Agrega una excepción para la carpeta `.gradle` en tu antivirus
2. O desactiva temporalmente el antivirus para probar

### Opción 6: Limpiar Proyecto Completamente
```bash
# Detener todos los daemons
.\gradlew.bat --stop

# Limpiar proyecto
.\gradlew.bat clean

# Limpiar build
Remove-Item -Recurse -Force .\app\build -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force .\build -ErrorAction SilentlyContinue

# Reconstruir
.\gradlew.bat build
```

## Verificar Si Funciona

1. En Android Studio: **File > Sync Project with Gradle Files**
2. Si hay errores, revisa la ventana "Build" en la parte inferior
3. Intenta compilar: **Build > Make Project**

## Si Nada Funciona

1. **Comparte el error completo** de la ventana "Build" en Android Studio
2. **Verifica la versión de Java**: `java -version` en terminal
3. **Verifica la versión de Android Studio** que estás usando
4. **Revisa si hay otros procesos de Gradle** corriendo en el Administrador de Tareas

