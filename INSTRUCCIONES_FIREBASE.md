# ⚠️ IMPORTANTE: Configuración de Firebase Requerida

La aplicación **NO ARRANCARÁ** correctamente hasta que configures Firebase.

## Pasos para Configurar Firebase:

### 1. Crear Proyecto en Firebase Console
1. Ve a https://console.firebase.google.com/
2. Haz clic en "Agregar proyecto" o selecciona uno existente
3. Sigue los pasos del asistente

### 2. Agregar Aplicación Android
1. En el panel del proyecto, haz clic en el ícono de Android
2. Ingresa el **Package name**: `com.example.playmatch`
3. (Opcional) Ingresa un nombre de app y SHA-1
4. Haz clic en "Registrar app"

### 3. Descargar google-services.json
1. Descarga el archivo `google-services.json`
2. **REEMPLAZA** el archivo `app/google-services.json` en tu proyecto con el archivo descargado
3. El archivo actual es solo un placeholder y causará errores

### 4. Habilitar Servicios en Firebase Console

#### Authentication:
- Ve a Authentication > Sign-in method
- Habilita **Email/Password**
- Habilita **Phone** (opcional)

#### Firestore Database:
- Ve a Firestore Database
- Haz clic en "Crear base de datos"
- Selecciona modo de prueba (para desarrollo)
- Elige una ubicación

### 5. Sincronizar Proyecto
1. En Android Studio, haz clic en "Sync Now" cuando aparezca la notificación
2. O ve a File > Sync Project with Gradle Files

### 6. Probar la Aplicación
Después de configurar Firebase, la aplicación debería arrancar correctamente.

## Si la App Sigue Sin Arrancar:

1. **Revisa Logcat** en Android Studio:
   - Ve a View > Tool Windows > Logcat
   - Filtra por "PlayMatch" o "Firebase"
   - Busca errores en rojo

2. **Verifica el archivo google-services.json**:
   - Debe estar en `app/google-services.json`
   - No debe tener valores "YOUR_PROJECT_NUMBER" o "YOUR_API_KEY"
   - Debe tener el package name correcto: `com.example.playmatch`

3. **Limpia y reconstruye**:
   - Build > Clean Project
   - Build > Rebuild Project

4. **Verifica la conexión a internet**:
   - Firebase requiere conexión para inicializar






