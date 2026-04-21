# Commits sugeridos: base del proyecto Reco (Android + referencia web)

Este documento describe **varios commits atómicos por capa**, en un orden que respeta dependencias entre paquetes (`data` → `theme` → `componentes` → `pantallas` → `navegación` → `MainActivity`).

**Requisitos:** repositorio Git ya inicializado en la carpeta del proyecto. No incluyas `local.properties` (está en `.gitignore`). Tampoco se versiona `.idea/` ni `app/build/`.

**Comprobar compilación** (recomendado tras el commit que añade `MainActivity` y `RecoApplication`):

```bash
./gradlew :app:assembleDebug
```

---

## Commit 1 — Ignorar artefactos y secretos locales

**Mensaje:**

```
chore: añadir .gitignore para Gradle, Android Studio y keystores
```

**Archivos:**

- `.gitignore`

**Comandos:**

```bash
git add .gitignore
git commit -m "chore: añadir .gitignore para Gradle, Android Studio y keystores"
```

---

## Commit 2 — Gradle Wrapper y configuración raíz

**Mensaje:**

```
build: configurar Gradle Wrapper y proyecto raíz
```

**Archivos:**

- `gradlew`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradle/wrapper/gradle-wrapper.jar`
- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`

**Comandos:**

```bash
git add gradlew gradle/wrapper/gradle-wrapper.properties gradle/wrapper/gradle-wrapper.jar settings.gradle.kts build.gradle.kts gradle.properties
git commit -m "build: configurar Gradle Wrapper y proyecto raíz"
```

---

## Commit 3 — Módulo `app`: dependencias y ProGuard

**Mensaje:**

```
build(android): dependencias Kotlin Compose y reglas ProGuard del módulo app
```

**Archivos:**

- `app/build.gradle.kts`
- `app/proguard-rules.pro`

**Comandos:**

```bash
git add app/build.gradle.kts app/proguard-rules.pro
git commit -m "build(android): dependencias Kotlin Compose y reglas ProGuard del módulo app"
```

---

## Commit 4 — Manifiesto y recursos (tema, strings, icono)

**Mensaje:**

```
feat(android): manifiesto, tema y recursos de la aplicación
```

**Archivos:**

- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/drawable/ic_reco_launcher.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/themes.xml`

**Comandos:**

```bash
git add app/src/main/AndroidManifest.xml app/src/main/res/
git commit -m "feat(android): manifiesto, tema y recursos de la aplicación"
```

> **Nota:** hasta que existan las clases Kotlin referenciadas en el manifiesto, el proyecto **no compilará por completo**. Es normal en esta estrategia por capas.

---

## Commit 5 — Capa de datos (modelo, preferencias, repositorio)

**Mensaje:**

```
feat(data): modelo de usuario, preferencias de tema y repositorio de autenticación
```

**Archivos:**

- `app/src/main/java/com/reco/app/data/model/User.kt`
- `app/src/main/java/com/reco/app/data/preferences/ThemeMode.kt`
- `app/src/main/java/com/reco/app/data/preferences/ThemePreferences.kt`
- `app/src/main/java/com/reco/app/data/repository/AuthRepository.kt`

**Comandos:**

```bash
git add app/src/main/java/com/reco/app/data/
git commit -m "feat(data): modelo de usuario, preferencias de tema y repositorio de autenticación"
```

---

## Commit 6 — Tema Compose y estado de UI genérico

**Mensaje:**

```
feat(ui): tema Material 3, tipografía y utilidad UiState
```

**Archivos:**

- `app/src/main/java/com/reco/app/ui/theme/Color.kt`
- `app/src/main/java/com/reco/app/ui/theme/Shape.kt`
- `app/src/main/java/com/reco/app/ui/theme/Theme.kt`
- `app/src/main/java/com/reco/app/ui/theme/ThemeViewModel.kt`
- `app/src/main/java/com/reco/app/ui/theme/Type.kt`
- `app/src/main/java/com/reco/app/util/UiState.kt`

**Comandos:**

```bash
git add app/src/main/java/com/reco/app/ui/theme/ app/src/main/java/com/reco/app/util/UiState.kt
git commit -m "feat(ui): tema Material 3, tipografía y utilidad UiState"
```

---

## Commit 7 — Componentes reutilizables

**Mensaje:**

```
feat(ui): componentes de botones, campo de texto y chips
```

**Archivos:**

- `app/src/main/java/com/reco/app/ui/components/GenreChip.kt`
- `app/src/main/java/com/reco/app/ui/components/RecoPrimaryButton.kt`
- `app/src/main/java/com/reco/app/ui/components/RecoSecondaryButton.kt`
- `app/src/main/java/com/reco/app/ui/components/RecoTextField.kt`

**Comandos:**

```bash
git add app/src/main/java/com/reco/app/ui/components/
git commit -m "feat(ui): componentes de botones, campo de texto y chips"
```

---

## Commit 8 — Pantallas (splash, auth, home)

**Mensaje:**

```
feat(ui): pantallas splash, login, registro e inicio
```

**Archivos:**

- `app/src/main/java/com/reco/app/ui/screens/splash/SplashScreen.kt`
- `app/src/main/java/com/reco/app/ui/screens/auth/LoginScreen.kt`
- `app/src/main/java/com/reco/app/ui/screens/auth/LoginViewModel.kt`
- `app/src/main/java/com/reco/app/ui/screens/auth/RegisterScreen.kt`
- `app/src/main/java/com/reco/app/ui/screens/auth/RegisterViewModel.kt`
- `app/src/main/java/com/reco/app/ui/screens/home/HomeScreen.kt`

**Comandos:**

```bash
git add app/src/main/java/com/reco/app/ui/screens/
git commit -m "feat(ui): pantallas splash, login, registro e inicio"
```

---

## Commit 9 — Navegación

**Mensaje:**

```
feat(app): grafo de navegación Compose y rutas
```

**Archivos:**

- `app/src/main/java/com/reco/app/navigation/NavGraph.kt`
- `app/src/main/java/com/reco/app/navigation/Screen.kt`

**Comandos:**

```bash
git add app/src/main/java/com/reco/app/navigation/
git commit -m "feat(app): grafo de navegación Compose y rutas"
```

---

## Commit 10 — Punto de entrada de la aplicación

**Mensaje:**

```
feat(app): MainActivity, Application y arranque con tema y navegación
```

**Archivos:**

- `app/src/main/java/com/reco/app/MainActivity.kt`
- `app/src/main/java/com/reco/app/RecoApplication.kt`

**Comandos:**

```bash
git add app/src/main/java/com/reco/app/MainActivity.kt app/src/main/java/com/reco/app/RecoApplication.kt
git commit -m "feat(app): MainActivity, Application y arranque con tema y navegación"
```

**Verificación:**

```bash
./gradlew :app:assembleDebug
```

---

## Commit 11 — Documentación del proyecto y Firebase

**Mensaje:**

```
docs: README, plantilla local.properties y guía de Firebase
```

**Archivos:**

- `README.md`
- `local.properties.example`
- `docs/FIREBASE_SETUP.md`

**Comandos:**

```bash
git add README.md local.properties.example docs/FIREBASE_SETUP.md
git commit -m "docs: README, plantilla local.properties y guía de Firebase"
```

---

## Commit 12 — Referencia web del diseño (Figma / prototipo)

**Mensaje:**

```
docs(web): añadir referencia HTML del diseño para el cliente web
```

**Archivos:**

- `index.html`

**Comandos:**

```bash
git add index.html
git commit -m "docs(web): añadir referencia HTML del diseño para el cliente web"
```

---

## Resumen rápido (todos los commits en secuencia)

Si ya ejecutaste los pasos anteriores por separado, no repitas. Si quieres **copiar y pegar la secuencia completa** desde un estado sin commits (ajusta si ya hiciste algunos):

1. Commit 1 → `.gitignore`  
2. Commit 2 → Gradle raíz  
3. Commit 3 → `app/build.gradle.kts` + ProGuard  
4. Commit 4 → `AndroidManifest.xml` + `res/`  
5. Commit 5 → `data/`  
6. Commit 6 → `ui/theme/` + `UiState.kt`  
7. Commit 7 → `ui/components/`  
8. Commit 8 → `ui/screens/`  
9. Commit 9 → `navigation/`  
10. Commit 10 → `MainActivity` + `RecoApplication`  
11. Commit 11 → README + docs Firebase + ejemplo properties  
12. Commit 12 → `index.html`

---

## Variante: menos commits (misma base, historial más corto)

| Commit | Qué agrupa |
|--------|------------|
| A | Commits 1–3 (`.gitignore` + Gradle + módulo app) |
| B | Commits 4–10 (toda la app Android en un solo bloque: `app/src` completo) |
| C | Commits 11–12 (documentación + `index.html`) |

**Comandos ejemplo para la variante B** (tras tener commits 1–3 aplicados o equivalente):

```bash
git add app/src/
git commit -m "feat(android): aplicación Reco Compose (recursos, datos, UI y navegación)"
```

---

## Push al remoto (cuando exista)

```bash
git remote add origin <URL-de-tu-repo>
git branch -M main
git push -u origin main
```

Sustituye `<URL-de-tu-repo>` por la URL HTTPS o SSH de tu servidor Git.
