# FitGit - App Android de Rutinas de Ejercicio

## Descripcion

App Android nativa en Java para gestionar rutinas de ejercicio. Permite buscar ejercicios desde una API, organizarlos en rutinas, registrar entrenamientos con kg y repeticiones, y ver el progreso con graficas y resumen semanal.

## Arquitectura

MVVM + Repository, con Navigation Component para la navegacion entre fragments.

```
UI (Fragments / Activities)
        |
   ViewModel
        |
   Repositorio
        |
  DAO (Room) / API (Retrofit) / Firestore
        |
Base de datos local / API remota / Nube
```

## Estructura de paquetes

```
com.example.fitgit
|
├── adapter
│   ├── AdaptadorEjercicios.java          # Lista de ejercicios (modo completo con GIF o compacto en rutina)
│   ├── AdaptadorEjerciciosSesion.java    # Ejercicios + series en el historial
│   ├── AdaptadorEntrenamiento.java       # Ejercicios durante un entrenamiento activo
│   ├── AdaptadorHistorial.java           # Entrenamientos agrupados por sesion
│   ├── AdaptadorRutinas.java             # Lista de rutinas con contador de ejercicios
│   └── AdaptadorSerie.java              # Series editables (kg/reps) en tiempo real
│
├── api
│   ├── ClienteRetrofit.java              # Cliente HTTP con interceptor para API key
│   ├── ServicioEjercicios.java           # Endpoints de ExerciseDB
│   └── ServicioTraduccion.java           # Traduccion automatica con Google Cloud Translation
│
├── database
│   ├── AppDatabase.java                  # Singleton Room (version 7)
│   ├── Converters.java                   # Conversor List<String> <-> JSON para Room
│   ├── EjercicioDao.java                 # CRUD ejercicios
│   ├── RutinaDao.java                    # CRUD rutinas y relaciones
│   └── SesionDao.java                    # CRUD sesiones y series
│
├── model
│   ├── Ejercicio.java                    # Entidad: ejercicios cacheados de la API
│   ├── EjercicioConSeries.java           # POJO: ejercicio con sus series
│   ├── EntrenamientoDia.java             # POJO: sesion agrupada con ejercicios y series
│   ├── PuntoGrafica.java                 # POJO: fecha + kg maximo para la grafica
│   ├── Rutina.java                       # Entidad: rutinas del usuario
│   ├── RutinaConConteo.java              # POJO: rutina con COUNT de ejercicios
│   ├── RutinaEjercicioCrossRef.java      # Tabla intermedia many-to-many
│   ├── SerieRegistro.java                # Entidad: serie con kg y repeticiones
│   ├── Sesion.java                       # Entidad: sesion de entrenamiento
│   └── SesionConDetalle.java             # POJO: JOIN sesiones + rutinas + series + ejercicios
│
├── repository
│   ├── RepositorioEjercicio.java         # Ejercicios (Room + Retrofit)
│   ├── RepositorioFirestore.java         # Sincronizacion con Firestore
│   ├── RepositorioRutina.java            # Rutinas (Room + Firestore)
│   └── RepositorioSesion.java            # Sesiones y series (Room + Firestore)
│
├── ui
│   ├── AnadirEjercicioBottomSheet.java   # Bottom sheet para anadir ejercicio con filtros y buscador
│   ├── DetalleRutinaFragment.java        # Ejercicios dentro de una rutina (vista compacta)
│   ├── DetallesEjercicioActivity.java    # Detalle con GIF, instrucciones y traduccion
│   ├── EjerciciosFragment.java           # Buscador general con filtros por musculo
│   ├── EntrenamientoActivity.java        # Entrenamiento activo con cronometro
│   ├── LoginActivity.java                # Login con email/password y Google
│   ├── MainActivity.java                 # Activity principal con NavController y BottomNav
│   ├── PerfilFragment.java               # Perfil con foto (Supabase Storage), nombre y logout
│   ├── ProgresoFragment.java             # Resumen semanal, historial y grafica de evolucion
│   ├── RegistroActivity.java             # Registro de usuario nuevo
│   ├── RegistroSeriesBottomSheet.java    # Registrar series (kg + reps) de un ejercicio
│   └── RutinasFragment.java              # Lista de rutinas con crear/eliminar/empezar
│
└── viewmodel
    ├── EjercicioViewModel.java           # Filtros reactivos con MediatorLiveData
    ├── RutinaViewModel.java              # CRUD rutinas por userId
    └── SesionViewModel.java              # Historial agrupado, resumen semanal y evolucion
```

## Base de datos (Room v7)

| Tabla | Descripcion |
|---|---|
| `tabla_ejercicios` | Ejercicios cacheados de la API con traduccion |
| `rutinas` | Rutinas del usuario (nombre, userId, fecha) |
| `rutina_ejercicio_cross_ref` | Relacion many-to-many rutinas <-> ejercicios |
| `sesiones` | Entrenamientos registrados (rutinaId, userId, fecha) |
| `series_registro` | Series de cada sesion (ejercicioId, kg, repeticiones) |

## Servicios externos

| Servicio | Uso |
|---|---|
| ExerciseDB (RapidAPI) | Ejercicios con GIFs e instrucciones |
| Google Cloud Translation | Traduccion automatica al espanol |
| Firebase Auth | Login con email y Google Sign-In |
| Firebase Firestore | Sincronizacion de rutinas, sesiones y series en la nube |
| Supabase Storage | Almacenamiento de fotos de perfil |

## API Keys

Las claves de API estan en `local.properties` (no se sube a GitHub) y se acceden via `BuildConfig`:
- `BuildConfig.RAPIDAPI_KEY` - ExerciseDB
- `BuildConfig.GOOGLE_TRANSLATE_KEY` - Google Translation
- `BuildConfig.SUPABASE_URL` - URL de Supabase
- `BuildConfig.SUPABASE_KEY` - Key de Supabase

## Navegacion

Usa Navigation Component con `nav_graph.xml`. La navegacion principal es con `BottomNavigationView` integrado con `NavController`:

```
LoginActivity / RegistroActivity
        |
  MainActivity (NavController + BottomNav)
        |
  ┌─────────────┬──────────────┬──────────────┬────────────┐
  │ Ejercicios  │  Mis Rutinas │   Progreso   │   Perfil   │
  │  Fragment   │   Fragment   │   Fragment   │  Fragment  │
  │      |      │       |      │              │            │
  │  Detalles   │  DetalleRut. │              │            │
  │  Activity   │   Fragment   │              │            │
  │             │       |      │              │            │
  │             │ Entrenamiento│              │            │
  │             │   Activity   │              │            │
  └─────────────┴──────────────┴──────────────┴────────────┘
```

## Stack tecnologico

| Tecnologia | Uso |
|---|---|
| Java 11 | Lenguaje principal |
| Room 2.6.1 | Base de datos local |
| LiveData + ViewModel | Datos reactivos y ciclo de vida |
| Navigation Component 2.7.7 | Navegacion entre fragments |
| Retrofit 2.11.0 | Llamadas API REST |
| Glide 4.16.0 | Carga de imagenes y GIFs |
| Firebase BOM 33.0.0 | Auth + Firestore + Storage |
| Supabase Storage | Fotos de perfil |
| MPAndroidChart 3.1.0 | Grafica de evolucion |
| Material Components 1.12.0 | UI (chips, cards, dialogs, bottom sheets) |
| OkHttp | Interceptor para API keys y subida de fotos |

## Contexto para IAs

Si eres una IA ayudando con este proyecto:

- Es Java, no Kotlin. Android nativo, no Flutter ni React Native.
- MVVM con Repository. La UI observa LiveData del ViewModel, que llama al Repositorio, que usa Room y/o Retrofit.
- Navigation Component con nav_graph.xml. No usar FragmentManager manual.
- Las API keys van en local.properties y se acceden con BuildConfig. Nunca hardcodearlas.
- Firebase Auth para el userId. Siempre hacer null check de getCurrentUser().
- Firestore sincroniza rutinas, sesiones y series. Cada operacion local se replica en Firestore.
- ViewBinding en todas las vistas. No usar findViewById.
- Dialogos con MaterialAlertDialogBuilder y estilo R.style.DialogRedondeado.
- AdaptadorEjercicios tiene dos ViewHolders: ViewHolderCompleto (con GIF, para la lista general) y ViewHolderRutina (compacto, para dentro de rutinas).
- Las fotos de perfil se redimensionan a 256x256 antes de subir a Supabase.
- Los ejercicios se traducen automaticamente al espanol con Google Translation y se cachean en Room.
- Min SDK 24, compileSdk 34.
