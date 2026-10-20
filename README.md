# Junta de Vecinos población "Zaror". (JuntaZaror)

Aplicación móvil Android para la **gestión de arriendos y la transparencia financiera** de la Junta de Vecinos **Población Zaror**.

Proyecto de la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**.

> Todos los datos de la app son ficticios y tienen fines educativos.

---

##  Descripción

La app permite que los vecinos soliciten el arriendo de la sede de la junta, que la directiva revise esas solicitudes y que tesorería registre los ingresos y gastos, todo de forma transparente y visible para la comunidad.

Cada usuario ve solo lo que le corresponde según su **rol**.

##  Funcionalidades

### Para todos los roles
- Registro de cuenta e inicio de sesión con validación de formularios.
- Contraseñas almacenadas con hash **PBKDF2-HMAC-SHA256** con salt (nunca en texto plano).
- Pantalla de **Transparencia financiera**: ingresos, gastos, saldo y listado de movimientos.
- Perfil de usuario y cierre de sesión.
- Navegación adaptativa (barra inferior o rail lateral según el tamaño de pantalla) con íconos animados.

### Por rol

| Rol | Qué puede hacer |
|---|---|
| **Vecino/a** | Solicitar el arriendo de la sede (fecha, bloque horario, motivo y cantidad de personas) y ver el estado de sus solicitudes. |
| **Directiva** | Revisar las solicitudes pendientes y confirmarlas o rechazarlas. |
| **Tesorería** | Registrar ingresos y gastos, adjuntando un comprobante con la **cámara** o desde la galería. |

### Notificaciones
- La **directiva** recibe un aviso cuando llega una nueva solicitud de arriendo.
- El **vecino** recibe un aviso cuando su solicitud es aprobada o rechazada.
- Cada notificación se muestra una sola vez.

### Bloques horarios de arriendo
- Mañana (09:00 – 13:00)
- Tarde (14:00 – 18:00)
- Noche (19:00 – 23:00)

##  Tecnologías

- **Lenguaje:** Kotlin 2.0.21
- **UI:** Jetpack Compose + Material Design 3
- **Arquitectura:** MVVM (Model – ViewModel – Repository)
- **Persistencia local:** Room 2.6.1 (SQLite) con KSP
- **Navegación:** Navigation Compose + `NavigationSuiteScaffold`
- **Imágenes:** Coil
- **Tipografía:** Poppins
- **Build:** Gradle (Kotlin DSL), Android Gradle Plugin 8.12.3

##  Estructura del proyecto

```
app/src/main/java/com/duoc/juntazaror/
├── data/
│   ├── local/
│   │   ├── dao/            # CuentaDao, ReservaDao, MovimientoDao
│   │   ├── entity/         # Cuenta, Reserva, Movimiento
│   │   └── AppDatabase.kt  # Base de datos Room
│   └── repository/         # Repositorios de cuentas, reservas y movimientos
├── model/                  # Rol, EstadoReserva, BloqueHorario, TipoMovimiento, modelos de formularios
├── notificaciones/         # Canales, notificador y registro de avisos ya enviados
├── ui/
│   ├── components/         # Selector de comprobante, imagen inteligente
│   ├── screens/            # Login, Registro, Home, Arriendos, Solicitudes, Movimientos, Transparencia, Perfil
│   └── theme/              # Colores, tema y tipografía
├── util/                   # PasswordHasher, fábrica de URIs para la cámara
├── viewmodel/              # ViewModels de cada pantalla
└── MainActivity.kt
```

##  Cómo ejecutar el proyecto

### Requisitos
- Android Studio (versión reciente, compatible con AGP 8.12)
- JDK 11 o superior
- Un emulador o dispositivo con **Android 13 (API 33) o superior**

### Pasos
1. Clona el repositorio:
   ```bash
   git clone https://github.com/P0llo9003/JuntaZaror.git
   ```
2. Abre la carpeta en Android Studio y espera a que termine la sincronización de Gradle.
3. Selecciona un emulador o dispositivo y presiona **Run ▶**.
4. Acepta el permiso de notificaciones cuando la app lo pida.

##  Usuarios de prueba

Al abrir la app por primera vez se crean automáticamente dos cuentas de demostración:

| Rol | Correo | Contraseña |
|---|---|---|
| Directiva | `directiva@demo.cl` | `Demo1234` |
| Tesorería | `tesoreria@demo.cl` | `Demo1234` |

Las cuentas de **vecino** se crean desde la pantalla de registro.

##  Diseño

Paleta de colores tierra:

| Nombre | Color |
|---|---|
| Earth | `#A16D47` |
| Sand | `#D8CABA` |
| Latte | `#C0AD8D` |
| Olive | `#776F4F` |
| Midnight | `#1D1D1D` |
| Linen | `#F7F3EB` |

El ícono de la app muestra tres casas (la comunidad) y una moneda con un check (la transparencia financiera).

## 🛣️ Próximos pasos

- [ ] Integración con un microservicio propio en Spring Boot mediante Retrofit
- [ ] Pruebas unitarias de ViewModels y repositorios
- [ ] Generación del APK firmado en modo release
- [ ] Entre otros...

##  Autores

| Integrante | GitHub |
|---|---|
| Tomás Rojas | [@tomasrojasm-duoc](https://github.com/tomasrojasm-duoc) |
| Sebastián Gómez | [@P0llo9003](https://github.com/P0llo9003) |
