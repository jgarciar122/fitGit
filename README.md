# 💪 FitGit — App Android de Gestión de Rutinas de Ejercicio

## 📋 Descripción general

FitGit es una aplicación Android nativa desarrollada en Java con Android Studio. Permite al usuario buscar ejercicios desde una API externa, organizarlos en rutinas personalizadas y gestionar dichas rutinas. La app sigue la arquitectura MVVM (Model-View-ViewModel) y utiliza Room como base de datos local.

---

## 🏗️ Arquitectura del proyecto

El proyecto sigue el patrón **MVVM + Repository**, recomendado por Google para apps Android modernas.

```
UI (Fragments / Activities)
        ↓
   ViewModel
        ↓
   Repositorio
        ↓
  DAO (Room) / API (Retrofit)
        ↓
Base de datos local / API remota
```

---

## 📁 Estructura de paquetes

```
com.example.fitgit
│
├── adapter
│   ├── AdaptadorEjercicios.java          # RecyclerView de ejercicios (modo añadir / modo quitar)
│   ├── AdaptadorEjerciciosSesion.java    # RecyclerView para mostrar ejercicios + series en el historial
│   ├── AdaptadorHistorial.java           # RecyclerView de entrenamientos agrupados (expandibles)
│   ├── AdaptadorRutinas.java             # RecyclerView de rutinas con contador de ejercicios
│   └── AdaptadorSerie.java               # RecyclerView editable (kg/reps) en tiempo real
│
├── api
│   ├── ClienteRetrofit.java              # Configuración del cliente HTTP con Retrofit
│   └── ServicioEjercicios.java           # Interfaz con los endpoints de la API (ExerciseDB)
│
├── database
│   ├── AppDatabase.java                  # Singleton de la base de datos Room (v4)
│   ├── Converters.java                   # Conversor de tipos para Room (ej: List<String>, Date)
│   ├── EjercicioDao.java                 # Operaciones CRUD para tabla_ejercicios
│   ├── RutinaDao.java                    # Operaciones CRUD para rutinas y cross_ref
│   └── SesionDao.java                    # Operaciones CRUD para sesiones y series_registro
│
├── model
│   ├── Ejercicio.java                    # Entidad Room: tabla_ejercicios
│   ├── EjercicioConSeries.java           # POJO: Relación de un ejercicio con sus series registradas
│   ├── EntrenamientoDia.java             # POJO: Agrupa ejercicios y series de una misma sesión
│   ├── PuntoGrafica.java                 # POJO: Fecha + kg máximo para gráficas de evolución
│   ├── Rutina.java                       # Entidad Room: rutinas
│   ├── RutinaConConteo.java              # POJO: rutina con número de ejercicios (COUNT + LEFT JOIN)
│   ├── RutinaEjercicioCrossRef.java      # Tabla intermedia many-to-many: rutina_ejercicio_cross_ref
│   ├── SerieRegistro.java                # Entidad Room: series_registro (kg y reps)
│   ├── Sesion.java                       # Entidad Room: sesiones (entrenamiento de un día)
│   └── SesionConDetalle.java             # POJO: JOIN complejo entre sesiones, rutinas, series y ejercicios
│
├── repository
│   ├── RepositorioEjercicio.java         # Fuente única para ejercicios (Room + Retrofit)
│   ├── RepositorioRutina.java            # Fuente única para rutinas (Room)
│   └── RepositorioSesion.java            # Fuente única para registro de progreso e historial (Room)
│
├── ui
│   ├── AnadirEjercicioBottomSheet.java   # Diálogo para añadir ejercicios a rutina (con buscador)
│   ├── DetalleRutinaFragment.java        # Lista de ejercicios dentro de una rutina (modo quitar)
│   ├── DetallesEjercicioActivity.java    # Vista de detalle con GIF y botón añadir/registrar progreso
│   ├── EjerciciosFragment.java           # Buscador general de ejercicios con filtros por músculo
│   ├── LoginActivity.java                # Pantalla de inicio de sesión (Firebase Auth + Google)
│   ├── MainActivity.java                 # Actividad principal con navegación manual inferior
│   ├── PerfilFragment.java               # Pantalla de perfil de usuario y cierre de sesión
│   ├── ProgresoFragment.java             # Pantalla de resumen semanal e historial expandible
│   ├── RegistroActivity.java             # Pantalla de registro de nuevo usuario
│   ├── RegistroSeriesBottomSheet.java    # Diálogo para registrar kg/reps de un ejercicio
│   └── RutinasFragment.java              # Lista de rutinas del usuario con opción de crear/eliminar
│
└── viewmodel
    ├── EjercicioViewModel.java           # Lógica UI de ejercicios (filtros reactivos y buscador)
    ├── RutinaViewModel.java              # Lógica UI CRUD de rutinas filtrado por userId
    └── SesionViewModel.java              # Lógica UI para progreso, historial y evolución
```

---

## 🗄️ Base de datos (Room)

### Tablas

| Tabla | Descripción |
|---|---|
| `tabla_ejercicios` | Ejercicios cacheados desde la API |
| `rutinas` | Rutinas creadas por el usuario |
| `rutina_ejercicio_cross_ref` | Relación many-to-many entre rutinas y ejercicios |

### Relaciones

Una rutina puede tener **muchos ejercicios** y un ejercicio puede pertenecer a **muchas rutinas**. Esto se gestiona con la tabla intermedia `RutinaEjercicioCrossRef`.

---

## 🌐 API externa

- **Proveedor:** ExerciseDB (vía RapidAPI)
- **Cliente HTTP:** Retrofit 2
- **Carga de GIFs:** Glide con headers personalizados (API key + User-Agent)
- **Estrategia de caché:** Si Room ya tiene ejercicios, no se llama a la API. Solo se llama la primera vez que la base de datos está vacía.

---

## 🧩 Componentes clave

### AdaptadorEjercicios
Tiene dos modos que se activan con `setEsModoQuitar(boolean)`:
- **Modo añadir** (false): muestra botón "Añadir a una rutina" — usado en `EjerciciosFragment`
- **Modo quitar** (true): muestra botón "Quitar de la rutina" — usado en `DetalleRutinaFragment`

Al abrir el detalle desde una rutina, se pasa `ya_en_rutina = true` por Intent para que el botón muestre "✓ Ya está en la rutina" y se deshabilite.

### RutinaConConteo
No es una entidad de Room, es un POJO que recibe el resultado de una query con `COUNT()` y `LEFT JOIN` para saber cuántos ejercicios tiene cada rutina sin necesidad de múltiples consultas.

### Navegación
La app usa `FragmentManager` manualmente (sin Navigation Component). El contenedor principal es `nav_host_fragment` en `MainActivity`.

---

## 🛠️ Stack tecnológico

| Tecnología | Uso |
|---|---|
| Java | Lenguaje principal |
| Android Studio | IDE |
| Room | Base de datos local (SQLite) |
| LiveData | Datos reactivos entre capas |
| ViewModel | Supervivencia al ciclo de vida |
| Retrofit 2 | Llamadas a la API REST |
| Glide | Carga de imágenes y GIFs |
| Material Components | UI moderna (chips, botones, diálogos, cards) |
| ViewBinding | Acceso seguro a vistas |

---

## 🤖 Contexto para IAs — Cómo ayudarme con este proyecto

Si eres una IA y estoy pidiéndote ayuda con este proyecto, ten en cuenta lo siguiente:

**El proyecto es una app Android nativa en Java**, no Kotlin, no Flutter, no React Native.

**Arquitectura:** MVVM con Repository. Cualquier lógica de base de datos o red debe ir en el Repositorio, luego exponerse desde el ViewModel como `LiveData`, y ser observada desde el Fragment o Activity. Nunca accedas a `AppDatabase` directamente desde la UI salvo en `DetallesEjercicioActivity`, `EjerciciosFragment` y `AñadirEjercicioBottomSheet` donde aún queda acceso directo al DAO (pendiente de refactorizar).

**Base de datos Room — versión 4:** Cinco tablas:
- `tabla_ejercicios` — ejercicios cacheados desde la API
- `rutinas` — rutinas del usuario, con campo `userId` de Firebase
- `rutina_ejercicio_cross_ref` — relación many-to-many entre rutinas y ejercicios
- `sesiones` — cada entrenamiento registrado (rutinaId + userId + fecha)
- `series_registro` — cada serie de un entrenamiento (sesionId + ejercicioId + kg + repeticiones)

**DAOs disponibles:** `EjercicioDao`, `RutinaDao`, `SesionDao`.

**ViewModels disponibles:**
- `EjercicioViewModel` — ejercicios con filtro reactivo por músculo
- `RutinaViewModel` — CRUD de rutinas filtrado por userId
- `SesionViewModel` — historial agrupado, resumen semanal y evolución por ejercicio

**Repositorios disponibles:** `RepositorioEjercicio`, `RepositorioRutina`, `RepositorioSesion`.

**Autenticación:** Firebase Auth con email/password y Google Sign-In. El UID se obtiene con `FirebaseAuth.getInstance().getCurrentUser().getUid()`. Los ViewModels lo obtienen automáticamente en el constructor.

**UI:** ViewBinding en todos los Fragments y Activities. Diálogos con `MaterialAlertDialogBuilder` y estilo `R.style.DialogRedondeado`. Evita caracteres especiales (ñ, acentos) en nombres de archivos XML para que el binding se genere correctamente.

**Navegación:** Manual con `FragmentManager`, sin Navigation Component. Contenedor: `R.id.nav_host_fragment`. Cuatro secciones: Ejercicios, Rutinas, Progreso y Perfil (este último se abre desde el botón de la toolbar).

**Toolbar global:** En `MainActivity` hay una toolbar con título dinámico y botón de perfil. El título se cambia llamando a `setTituloToolbar(String)` o pasando el título en `reemplazarFragment(fragment, titulo)`.

**Adaptadores:**
- `AdaptadorEjercicios` — flag `esModoQuitar` y `rutinaId`. En modo quitar muestra botón rojo; pasa `ya_en_rutina=true` y `rutina_id` por Intent al abrir el detalle.
- `AdaptadorRutinas` — usa `RutinaConConteo` con contador de ejercicios.
- `AdaptadorSeries` — RecyclerView editable con TextWatcher para kg y reps en tiempo real.
- `AdaptadorHistorial` — lista de entrenamientos agrupados por sesión, expandibles.
- `AdaptadorEjerciciosSesion` — muestra nombre del ejercicio + series con kg y reps dentro de cada sesión.

**Bottom Sheets disponibles:**
- `RegistroSeriesBottomSheet` — registrar series (kg + reps) de un ejercicio. Reutiliza la sesión del día si ya existe.
- `AñadirEjercicioBottomSheet` — añadir ejercicios a una rutina. Excluye los que ya están. Tiene buscador en tiempo real.

**Registro de progreso:** Botón "Registrar progreso" visible solo cuando `ya_en_rutina=true`. Abre `RegistroSeriesBottomSheet` que precarga las últimas series y guarda `Sesion` + `SerieRegistro` al confirmar. Si ya existe sesión del día para esa rutina la reutiliza.

**Pantalla de progreso:** `ProgresoFragment` con resumen semanal (✅/⬜ por día) e historial de sesiones expandible con ejercicios, kg y reps. Usa `SesionViewModel`.

**Pantalla de perfil:** `PerfilFragment` muestra nombre, email, foto (con CircleImageView + Glide) y proveedor de autenticación. Botón de cerrar sesión.

**POJOs especiales:**
- `RutinaConConteo` — rutina con campo `numEjercicios` calculado con COUNT + LEFT JOIN
- `PuntoGrafica` — fecha + kg máximo para gráficas de evolución (pendiente de implementar en UI)
- `SesionConDetalle` — resultado de JOIN entre sesiones, rutinas, series y ejercicios
- `EjercicioConSeries` — ejercicio con su lista de series para el historial
- `EntrenamientoDia` — agrupa ejercicios y series de una misma sesión

**Pendiente de implementar:** Gráfica de evolución por ejercicio en la pantalla de progreso.

**Cuando me pases código:** dame siempre el archivo completo si has cambiado más de 3 líneas, o solo el fragmento exacto si es un cambio puntual. Indica siempre en qué archivo y método va cada cambio.
---

## 📱 Flujo principal de la app

```
Login / Registro
      ↓
MainActivity (navegación inferior)
      ↓
┌─────────────────────┬──────────────────────┐
│  EjerciciosFragment │   RutinasFragment    │
│  (buscar y filtrar) │   (mis rutinas)      │
│         ↓           │        ↓             │
│  DetallesEjercicio  │  DetalleRutina       │
│  Activity           │  Fragment            │
│  (ver + añadir)     │  (ver + quitar)      │
└─────────────────────┴──────────────────────┘
```
