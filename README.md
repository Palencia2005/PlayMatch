# PlayMatch - Aplicación de Citas

PlayMatch es una aplicación de citas desarrollada en Android con Kotlin que permite a los usuarios registrarse, crear perfiles, hacer match con otros usuarios y chatear.

## Características

- ✅ Registro con correo electrónico o número de teléfono
- ✅ Verificación de cuenta mediante código
- ✅ Validación de correo/teléfono duplicados
- ✅ Inicio de sesión con correo y contraseña
- ✅ Configuración de perfil mediante preguntas
- ✅ Sistema de matching con otros usuarios
- ✅ Chat en tiempo real con usuarios con match

## Configuración del Proyecto

### 1. Configurar Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Agrega una aplicación Android:
   - Package name: `com.example.playmatch`
   - Descarga el archivo `google-services.json`
   - Reemplaza el archivo `app/google-services.json` del proyecto con el descargado

### 2. Habilitar servicios de Firebase

En Firebase Console, habilita los siguientes servicios:

- **Authentication**:
  - Email/Password
  - Phone Number
  
- **Firestore Database**: Crea una base de datos en modo de prueba

- **Storage** (opcional): Para almacenar fotos de perfil

### 3. Reglas de Firestore

Configura las siguientes reglas de seguridad en Firestore:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    match /profiles/{profileId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == profileId;
    }
    match /matches/{matchId} {
      allow read, write: if request.auth != null;
    }
    match /chats/{chatId} {
      allow read, write: if request.auth != null;
      match /messages/{messageId} {
        allow read, write: if request.auth != null;
      }
    }
  }
}
```

### 4. Configurar verificación por correo

Para la verificación por correo electrónico, necesitarás configurar un servicio de envío de correos. Opciones:

- **Firebase Functions**: Crea una Cloud Function que envíe el código
- **SendGrid**: Integra SendGrid para envío de correos
- **SMTP**: Configura un servidor SMTP

**Nota**: Actualmente, la verificación por correo genera un código pero no lo envía. Debes implementar el servicio de envío.

### 5. Compilar y ejecutar

1. Sincroniza el proyecto con Gradle
2. Ejecuta la aplicación en un dispositivo o emulador

## Estructura del Proyecto

```
app/src/main/java/com/example/playmatch/
├── data/
│   ├── model/          # Modelos de datos (User, Profile, Match, Message, Chat)
│   └── repository/     # Repositorios para acceso a datos
├── ui/
│   ├── auth/          # Pantallas de autenticación
│   ├── profile/       # Configuración de perfil
│   ├── discovery/     # Pantalla de matching
│   └── chat/         # Sistema de chat
└── MainActivity.kt   # Actividad principal
```

## Flujo de la Aplicación

1. **Registro**: El usuario se registra con nombre, correo o teléfono
2. **Verificación**: Se envía un código de verificación
3. **Perfil**: El usuario completa su perfil respondiendo preguntas
4. **Discovery**: El usuario ve otros perfiles y puede hacer match
5. **Chat**: Los usuarios con match pueden chatear

## Dependencias Principales

- Firebase Authentication
- Firebase Firestore
- Navigation Component
- ViewModel & LiveData
- Material Design Components
- Coroutines

## Notas Importantes

- La verificación por correo requiere configuración adicional
- Las fotos de perfil no están implementadas (puedes agregar Firebase Storage)
- El sistema de matching es básico (puedes mejorarlo con algoritmos de recomendación)
- Los mensajes se almacenan en Firestore (considera usar Firebase Realtime Database para mejor rendimiento)

## Próximos Pasos

- [ ] Implementar servicio de envío de correos para verificación
- [ ] Agregar subida de fotos de perfil
- [ ] Mejorar algoritmo de matching
- [ ] Agregar notificaciones push
- [ ] Implementar filtros de búsqueda
- [ ] Agregar reporte de usuarios






