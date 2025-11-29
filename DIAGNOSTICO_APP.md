# 🔍 Diagnóstico: La App No Se Abre

## Pasos para Diagnosticar el Problema

### 1. Revisar Logcat en Android Studio

El problema más común es un crash silencioso. Para verlo:

1. Abre Android Studio
2. Ve a **View > Tool Windows > Logcat** (o presiona `Alt + 6`)
3. Conecta tu dispositivo o inicia un emulador
4. Filtra por:
   - **Tag**: `PlayMatch` o `PlayMatchApp` o `MainActivity`
   - **Level**: `Error` o `Fatal`
5. Intenta abrir la app
6. Busca errores en rojo

### 2. Errores Comunes y Soluciones

#### Error: "FirebaseApp initialization failed"
**Causa**: El archivo `google-services.json` tiene valores placeholder.

**Solución**:
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un proyecto o selecciona uno existente
3. Agrega una app Android con package: `com.example.playmatch`
4. Descarga `google-services.json` y reemplázalo en `app/google-services.json`

#### Error: "Resource not found" o "Missing resource"
**Causa**: Falta algún recurso (color, string, layout).

**Solución**: 
- Verifica que todos los recursos referenciados existan
- Ejecuta: `Build > Clean Project` y luego `Build > Rebuild Project`

#### Error: "ClassNotFoundException" o "NoClassDefFoundError"
**Causa**: Dependencias faltantes o incompatibles.

**Solución**:
1. `File > Sync Project with Gradle Files`
2. `Build > Clean Project`
3. `Build > Rebuild Project`

#### Error: "Navigation graph not found"
**Causa**: Problema con la navegación.

**Solución**: Verifica que `app/src/main/res/navigation/nav_graph.xml` exista y esté bien formado.

### 3. Verificar el APK

1. **Build > Build Bundle(s) / APK(s) > Build APK(s)**
2. Si falla, revisa los errores en la ventana "Build"
3. Si compila, instala manualmente:
   ```
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### 4. Probar en Modo Debug

1. Conecta tu dispositivo
2. Ve a **Run > Debug 'app'**
3. Revisa la consola de debug para ver errores

### 5. Verificar Permisos

Asegúrate de que el AndroidManifest.xml tenga:
- `<uses-permission android:name="android.permission.INTERNET" />`
- Permisos necesarios para tu app

### 6. Limpiar y Reconstruir

```bash
# En la terminal de Android Studio o PowerShell:
.\gradlew.bat clean
.\gradlew.bat build
```

### 7. Verificar Versión de Android

- **minSdk**: 24 (Android 7.0)
- Asegúrate de que tu dispositivo/emulador tenga al menos esta versión

### 8. Desinstalar Versión Anterior

Si instalaste una versión anterior que falla:
```bash
adb uninstall com.example.playmatch
```

Luego reinstala la app.

## Si Nada Funciona

1. **Comparte el Logcat completo** con los errores
2. **Comparte la versión de Android** de tu dispositivo
3. **Comparte la versión de Android Studio** que estás usando
4. **Verifica que el proyecto compile** sin errores

## Cambios Recientes Realizados

Se mejoró el manejo de errores para que la app no se cierre si Firebase no está configurado:
- ✅ `PlayMatchApplication`: Manejo robusto de errores de Firebase
- ✅ `MainActivity`: Manejo de errores mejorado
- ✅ `LoginFragment`: Manejo de errores mejorado
- ✅ `AuthRepository`: Manejo seguro cuando Firebase no está disponible
- ✅ `LoginViewModel`: Manejo de errores mejorado

La app debería poder abrirse incluso sin Firebase configurado, aunque las funciones de Firebase no funcionarán.

