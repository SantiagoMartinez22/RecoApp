# Handoff Plan - RECO Segunda Mitad

## 1) Contexto general
Este documento es para transferencia a otra persona del equipo.

Estado del proyecto al 14-04-2026:
- Ya se implemento la primera mitad priorizada: Buscar, Detalle y Ajustes.
- Ya se corrigieron hardcoded criticos del flujo principal de usuario (nombre de Home y persistencia de registro).
- Persistencia local activa con DataStore compartido para tema y preferencias de usuario.
- Navegacion principal funcional: Home, Search, Settings, Detail, Credits.
- La pestaña Lists sigue pendiente de implementacion real (actualmente placeholder).

Objetivo de esta segunda mitad:
- Completar lo faltante sin perder consistencia funcional ni visual.
- Dejar la app lista para entrega academica sin datos falsos visibles.

## 2) Lo que YA esta hecho (para no retrabajar)
### Persistencia y auth
- DataStore compartido en data/preferences/RecoDataStore.kt.
- ThemePreferences usa ese DataStore compartido.
- UserPreferences creado con:
  - userName
  - favoritesIds
  - platformSubscriptions
  - notifRecommendations
  - notifReleases
- AuthRepository:
  - registra userName al crear cuenta
  - ya tiene signOut()
- MainActivity inyecta UserPreferences en AuthRepository.

### UI y navegacion
- SearchScreen + SearchViewModel implementados.
- DetailScreen + DetailViewModel implementados.
- SettingsScreen + SettingsViewModel implementados.
- RatingPill implementado.
- Ruta Detail con argumentos y ruta Credits agregadas.
- HomeScreen ya no tiene onSeeAll en no-op.
- HomeViewModel ya no usa nombre hardcodeado.

### Capa de datos TMDB
- DTOs de detalle agregados en TmdbDto.kt.
- Endpoints de detalle agregados en TmdbApi.kt.
- MovieRepository con getMovieDetail/getTvDetail + fallback demo por id.

## 3) Pendiente real (alcance de segunda mitad)
## 3.1 Bloque A - Limpieza de hardcoded residual (prioridad alta)
1. CreditsScreen aun muestra placeholders de equipo e institucion.
   - Archivo: ui/screens/credits/CreditsScreen.kt
   - Accion: reemplazar placeholders por datos reales del equipo o parametrizar valores.

2. SettingsScreen aun muestra correo fijo demo@reco.app.
   - Archivo: ui/screens/settings/SettingsScreen.kt
   - Accion: usar dato dinamico o fallback neutral (ej. "Sin correo configurado").

3. Confirmar politica de fallback en MovieRepository cuando no hay TMDB_API_KEY.
   - Archivo: data/repository/MovieRepository.kt
   - Accion: decidir entre mantener demo (modo academico) o retornar error controlado.

## 3.2 Bloque B - Lists completa (pendiente principal)
1. Crear ListsViewModel.
   - Archivo nuevo: ui/screens/lists/ListsViewModel.kt
   - Debe manejar:
     - favoritos desde UserPreferences.favoritesIds
     - toggle favorito
     - listas personalizadas

2. Crear ListsScreen.
   - Archivo nuevo: ui/screens/lists/ListsScreen.kt
   - Debe incluir:
     - seccion Mis listas
     - seccion Mis favoritos
     - accion + Nueva lista
     - soporte para abrir detalle desde item

3. Persistencia de listas personalizadas en UserPreferences.
   - Archivo: data/preferences/UserPreferences.kt
   - Recomendado: serializar mapa nombre->ids como JSON (o estructura equivalente estable).

4. Integrar Lists en MainScaffold.
   - Archivo: navigation/MainScaffold.kt
   - Accion: reemplazar placeholder actual por composable real.

## 3.3 Bloque C - Integraciones cruzadas (deseable para cierre)
1. Permitir toggle favorito desde DetailScreen.
2. Permitir toggle favorito desde SearchScreen.
3. Verificar que Home/Search/Lists navegan a Detail sin perder estado.

## 4) Orden recomendado de ejecucion
Fase 1 (rapida, obligatoria):
- Limpiar hardcoded residual de Credits y Settings.
- Cerrar decision de fallback TMDB.

Fase 2 (feature principal):
- Modelo y persistencia de listas en UserPreferences.
- ListsViewModel.
- ListsScreen.
- Wiring en MainScaffold.

Fase 3 (endurecimiento):
- Integrar favoritos en Search/Detail.
- QA manual de navegacion y persistencia.

## 5) Definition of Done (DoD)
Para considerar cerrada la segunda mitad, todo lo siguiente debe cumplirse:
1. La pestaña Lists ya no es placeholder.
2. Se pueden crear listas y persisten tras reiniciar app.
3. Favoritos se ven y actualizan desde al menos Lists y un segundo punto (Search o Detail).
4. No hay strings demo/falsos visibles en Settings/Credits.
5. Build sin errores Kotlin/Compose.
6. Flujo de signOut sigue funcionando.

## 6) QA checklist para quien recibe el handoff
1. Registro de usuario:
   - Registrar usuario nuevo.
   - Verificar saludo en Home con nombre persistido.

2. Search:
   - Buscar termino valido.
   - Filtrar por genero.
   - Abrir detalle desde resultado.

3. Detail:
   - Carga de informacion con y sin API key.
   - Boton trailer abre YouTube.

4. Settings:
   - Cambiar tema (Sistema/Claro/Oscuro) y verificar persistencia.
   - Probar switches de notificaciones/plataformas.
   - Probar cierre de sesion.

5. Lists (nuevo):
   - Crear lista.
   - Agregar/quitar favoritos.
   - Reiniciar app y verificar persistencia.

6. Credits:
   - Verificar datos reales y navegacion de regreso.

## 7) Riesgos conocidos y mitigacion
1. Riesgo: inconsistencia de datos si se mezclan IDs sin mediaType.
   - Mitigacion: mantener formato canonico mediaType:id en favoritos/listas.

2. Riesgo: comportamiento ambiguo sin TMDB_API_KEY.
   - Mitigacion: documentar claramente modo demo vs modo error.

3. Riesgo: deuda tecnica por placeholders de identidad (equipo/institucion).
   - Mitigacion: centralizar textos en recurso o config unica.

## 8) Nota operativa de build en este repo
- En Windows este workspace no tiene gradlew.bat.
- Para compilar desde terminal local se uso gradle del sistema.
- Si el equipo necesita wrapper en Windows, agregar gradlew.bat al repo.

## 9) Entregables esperados al cerrar segunda mitad
1. ListsScreen y ListsViewModel implementados.
2. UserPreferences extendido para listas personalizadas.
3. MainScaffold integrado con Lists real.
4. Hardcoded residual eliminado en Credits y Settings.
5. Documento breve de cambios realizados y decisiones tomadas.