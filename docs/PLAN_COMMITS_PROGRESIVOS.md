# Plan de commits progresivos - RecoApp

Este plan propone una subida ordenada y por funcionalidad, evitando un commit monolítico.

## Commit 1 - Persistencia: guardar correo de usuario
Objetivo: persistir el correo del usuario y registrarlo al autenticar.

Archivos:
- app/src/main/java/com/reco/app/data/preferences/UserPreferences.kt (solo bloques de email)
- app/src/main/java/com/reco/app/data/repository/AuthRepository.kt

Comandos:
```bash
git add -p app/src/main/java/com/reco/app/data/preferences/UserPreferences.kt
git add app/src/main/java/com/reco/app/data/repository/AuthRepository.kt
git commit -m "feat(persistencia): guardar correo de usuario en DataStore"
```

## Commit 2 - Persistencia: listas personalizadas en DataStore
Objetivo: agregar soporte de listas personalizadas con serialización/deserialización.

Archivos:
- app/src/main/java/com/reco/app/data/preferences/UserPreferences.kt (resto de cambios)

Comandos:
```bash
git add app/src/main/java/com/reco/app/data/preferences/UserPreferences.kt
git commit -m "feat(persistencia): añadir listas personalizadas y operaciones CRUD"
```

## Commit 3 - Ajustes: mostrar correo real y validar reset
Objetivo: eliminar valores hardcodeados y usar el correo persistido para acciones de cuenta.

Archivos:
- app/src/main/java/com/reco/app/ui/screens/settings/SettingsViewModel.kt
- app/src/main/java/com/reco/app/ui/screens/settings/SettingsScreen.kt

Comandos:
```bash
git add app/src/main/java/com/reco/app/ui/screens/settings/SettingsViewModel.kt
git add app/src/main/java/com/reco/app/ui/screens/settings/SettingsScreen.kt
git commit -m "feat(ajustes): usar correo persistido y validar restablecimiento"
```

## Commit 4 - Listas: crear ViewModel
Objetivo: implementar la lógica de estado para favoritos y listas personalizadas.

Archivos:
- app/src/main/java/com/reco/app/ui/screens/lists/ListsViewModel.kt

Comandos:
```bash
git add app/src/main/java/com/reco/app/ui/screens/lists/ListsViewModel.kt
git commit -m "feat(listas): crear ListsViewModel con carga y gestión de listas"
```

## Commit 5 - Listas: crear pantalla y acciones UI
Objetivo: construir la interfaz de Listas con creación, agregado y eliminación de ítems.

Archivos:
- app/src/main/java/com/reco/app/ui/screens/lists/ListsScreen.kt

Comandos:
```bash
git add app/src/main/java/com/reco/app/ui/screens/lists/ListsScreen.kt
git commit -m "feat(listas): implementar ListsScreen con gestión de listas y favoritos"
```

## Commit 6 - Navegación: integrar módulo de listas
Objetivo: conectar ListsScreen al flujo principal y resolver factories relacionadas.

Archivos:
- app/src/main/java/com/reco/app/navigation/MainScaffold.kt

Comandos:
```bash
git add app/src/main/java/com/reco/app/navigation/MainScaffold.kt
git commit -m "feat(navegacion): integrar pantalla de listas en el scaffold principal"
```

## Commit 7 - UI base de favoritos en tarjetas
Objetivo: extender la tarjeta reutilizable para mostrar y alternar estado de favorito.

Archivos:
- app/src/main/java/com/reco/app/ui/components/PosterCard.kt

Comandos:
```bash
git add app/src/main/java/com/reco/app/ui/components/PosterCard.kt
git commit -m "feat(ui): agregar control visual de favoritos en PosterCard"
```

## Commit 8 - Favoritos desde búsqueda
Objetivo: habilitar toggle de favoritos directamente en resultados de búsqueda.

Archivos:
- app/src/main/java/com/reco/app/ui/screens/search/SearchViewModel.kt
- app/src/main/java/com/reco/app/ui/screens/search/SearchScreen.kt

Comandos:
```bash
git add app/src/main/java/com/reco/app/ui/screens/search/SearchViewModel.kt
git add app/src/main/java/com/reco/app/ui/screens/search/SearchScreen.kt
git commit -m "feat(busqueda): permitir marcar y desmarcar favoritos en resultados"
```

## Commit 9 - Favoritos desde detalle
Objetivo: permitir alternar favoritos dentro de la pantalla de detalle.

Archivos:
- app/src/main/java/com/reco/app/ui/screens/detail/DetailViewModel.kt
- app/src/main/java/com/reco/app/ui/screens/detail/DetailScreen.kt

Comandos:
```bash
git add app/src/main/java/com/reco/app/ui/screens/detail/DetailViewModel.kt
git add app/src/main/java/com/reco/app/ui/screens/detail/DetailScreen.kt
git commit -m "feat(detalle): añadir toggle de favoritos en vista de detalle"
```

## Commit 10 - Limpieza y documentación final
Objetivo: eliminar placeholders visibles y agregar documento de handoff.

Archivos:
- app/src/main/java/com/reco/app/ui/screens/credits/CreditsScreen.kt
- .cursor/plans/# Handoff Plan - RECO Segunda Mitad.md

Comandos:
```bash
git add app/src/main/java/com/reco/app/ui/screens/credits/CreditsScreen.kt
git add ".cursor/plans/# Handoff Plan - RECO Segunda Mitad.md"
git commit -m "chore: limpiar créditos y documentar handoff de segunda mitad"
```

## Verificación y subida final
Comandos recomendados al terminar la secuencia:

```bash
git status
git log --oneline -n 12
git push origin <tu-rama>
```

## Nota
Este plan ya está definido en 10 commits exactos. Si necesitas que además queden agrupados por hitos de entrega (por ejemplo, Sprint 1 y Sprint 2), se puede generar una versión alternativa manteniendo los mismos 10 commits.
