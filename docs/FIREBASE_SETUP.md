# Configuración de Firebase para RECO

## 1. Crear el proyecto en Firebase Console

1. Entra en [Firebase Console](https://console.firebase.google.com/) y crea un proyecto (o usa uno existente).
2. Añade una app **Android** con el **package name**: `com.upb.reco.app` (debe coincidir con `applicationId` en `app/build.gradle.kts`).

## 2. Descargar `google-services.json`

1. En la configuración de la app Android, descarga `google-services.json`.
2. Colócalo en la carpeta **`app/`** del proyecto (junto a `app/build.gradle.kts`).
3. Sustituye el archivo **placeholder** que viene en el repositorio si aún existe.

## 3. Habilitar servicios

### Authentication

1. En **Build → Authentication → Sign-in method**, habilita:
   - **Correo electrónico/contraseña**
   - **Google** (necesitarás la huella SHA-1/256 de debug/release en la app de Firebase para el cliente Android)

### Firestore

1. Crea una base de datos **Firestore** en modo de prueba o con reglas acordes a tu entorno.
2. Colección esperada por la app: **`users`**, documentos con ID = UID de Firebase Auth.  
   Campos escritos en el registro:
   - `name` (string)
   - `email` (string)
   - `genres` (array de strings)
   - `keywords` (string)
   - `createdAt` (timestamp)

### Reglas de Firestore (ejemplo de desarrollo)

Ajusta según tu política de seguridad. Ejemplo mínimo para que solo el usuario lea/escriba su documento:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 4. Google Sign-In (Web client ID)

1. En Firebase, pestaña **Authentication → Sign-in method → Google**, copia el **Web client ID** (a veces aparece en la consola de credenciales de Google Cloud vinculada al proyecto).
2. Pégalo en `app/src/main/res/values/strings.xml` en el string **`default_web_client_id`**:

```xml
<string name="default_web_client_id" translatable="false">TU_WEB_CLIENT_ID.apps.googleusercontent.com</string>
```

3. Añade en Firebase la **huella SHA-1** (y opcionalmente SHA-256) de tu keystore de debug:

```bash
./gradlew signingReport
```

o:

```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

## 5. Apple Sign-In en Android

La app muestra un mensaje de “próximamente”. Integrar **Sign in with Apple** en Android requiere configuración adicional en Apple Developer y Firebase; no está incluido en esta base.

## Comprobación

Tras colocar el `google-services.json` real y habilitar Auth + Firestore, compila e instala:

```bash
./gradlew :app:assembleDebug
```

Inicia sesión o regístrate; en Firestore debería crearse `users/{uid}` al completar el registro.
