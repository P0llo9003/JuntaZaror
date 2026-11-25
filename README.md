# Junta de Vecinos población "Zaror". (JuntaZaror)

Aplicación móvil Android para la **gestión de arriendos y la transparencia financiera** de la Junta de Vecinos **Población Zaror**.

Proyecto de la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles**.

---

##  Integrantes

| Integrante | GitHub | Nombres que aparecen en los commits |
|---|---|---|
| Tomás Rojas | [@tomasrojasm-duoc](https://github.com/tomasrojasm-duoc) | `tomasrojasm-duoc`, `tomasrojas` |
| Sebastián Gómez | [@P0llo9003](https://github.com/P0llo9003) | `P0llo9003`, `Pollo` |

> Algunos commits aparecen como `AV-Alumno` porque se hicieron desde un computador de Duoc.



##  Descripción

La app permite que los vecinos soliciten el arriendo de la sede de la junta, que la directiva revise esas solicitudes y que tesorería registre los ingresos y gastos, todo de forma transparente y visible para la comunidad.

Cada usuario ve solo lo que le corresponde según su **rol**. Todos los datos se guardan en un **backend propio de microservicios en Spring Boot**, al que la app se conecta mediante **Retrofit**.

##  Funcionalidades

### Para todos los roles
- Registro de cuenta e inicio de sesión con validación de formularios.
- Contraseñas protegidas: el backend las guarda con hash **PBKDF2-HMAC-SHA256** con salt (nunca en texto plano).
- Pantalla de **Transparencia financiera**: ingresos, gastos, saldo y listado de movimientos.
- Perfil de usuario y cierre de sesión.
- Navegación adaptativa (barra inferior o rail lateral según el tamaño de pantalla) con íconos animados.

### Por rol

| Rol | Qué puede hacer |
|---|---|
| **Vecino/a** | Solicitar el arriendo de la sede (fecha, bloque horario, motivo y cantidad de personas) y ver el estado de sus solicitudes. Los **feriados** aparecen bloqueados en el calendario. |
| **Directiva** | Revisar las solicitudes pendientes y confirmarlas o rechazarlas. Ver y eliminar usuarios. |
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

**App móvil**
- **Lenguaje:** Kotlin 2.0.21
- **UI:** Jetpack Compose + Material Design 3
- **Arquitectura:** MVVM (Model – ViewModel – Repository)
- **Conexión con el backend y la API externa:** Retrofit + Gson + OkHttp
- **Navegación:** Navigation Compose + `NavigationSuiteScaffold`
- **Imágenes:** Coil (los comprobantes se envían al backend en Base64)
- **Pruebas:** Kotest, MockK, kotlinx-coroutines-test, Compose UI Test y Kover
- **Tipografía:** Poppins
- **Build:** Gradle (Kotlin DSL), Android Gradle Plugin 8.12.3

**Backend**
- Spring Boot, con un microservicio por dominio y un API Gateway
- MySQL (XAMPP), con una base de datos por microservicio creada por **Liquibase**
- Docker / Docker Compose

##  Arquitectura

```
App Android ──Retrofit──► API Gateway (9090) ──┬──► ms-admin    (9091) ─► bd_admin
     │                                          ├──► ms-reservas (9092) ─► bd_reservas
     │                                          └──► ms-finanzas (9093) ─► bd_finanzas
     │
     └──Retrofit (instancia separada)──► API externa Nager.Date (feriados de Chile)
```

##  Endpoints utilizados

### Microservicios propios (a través del API Gateway `http://localhost:9090/`)

**ms-admin — cuentas**

| Método | Ruta | Uso en la app |
|---|---|---|
| POST | `/cuentas/registro` | Pantalla de registro (crea una cuenta de vecino) |
| POST | `/cuentas/login` | Inicio de sesión |
| GET | `/cuentas` | Lista de usuarios (directiva) |
| DELETE | `/cuentas/{id}` | Eliminar un usuario (directiva) |

**ms-reservas — arriendos**

| Método | Ruta | Uso en la app |
|---|---|---|
| GET | `/reservas/cuenta/{cuentaId}` | "Mis solicitudes" del vecino |
| GET | `/reservas/solicitudes` | Solicitudes para la directiva (con el nombre del solicitante) |
| GET | `/reservas/disponibilidad?fecha=YYYY-MM-DD` | Bloques ocupados de una fecha (se deshabilitan en el formulario) |
| GET | `/reservas/disponible?fecha=...&bloque=...` | Comprobar el bloque antes de enviar la solicitud |
| POST | `/reservas` | Crear una solicitud de arriendo |
| PUT | `/reservas/{id}/aprobar?resolutorId=` | Aprobar una solicitud (directiva) |
| PUT | `/reservas/{id}/rechazar?resolutorId=` | Rechazar una solicitud (directiva) |

**ms-finanzas — movimientos**

| Método | Ruta | Uso en la app |
|---|---|---|
| GET | `/movimientos` | Listado de movimientos (Movimientos y Transparencia) |
| GET | `/movimientos/resumen` | Totales de ingresos, gastos y saldo |
| POST | `/movimientos` | Registrar un ingreso o gasto (tesorería) |

### API externa: feriados de Chile (Nager.Date)

| Método | Endpoint | Uso en la app |
|---|---|---|
| GET | `https://date.nager.at/api/v3/PublicHolidays/{año}/CL` | En **Solicitar arriendo**: los feriados nacionales se bloquean en el calendario, se muestra el próximo feriado y no se puede enviar una solicitud para un feriado. |

- Es gratuita, no necesita API key y usa HTTPS.
- Se consume con una **instancia de Retrofit propia** (`remote/externa/FeriadosApi.kt`), separada de la de los microservicios, para no interferir con ellos.
- Si la API no responde, la app sigue funcionando y solo muestra un aviso.

##  Estructura del proyecto

```
app/src/main/java/com/duoc/juntazaror/
├── data/
│   └── repository/         # CuentaRemoteRepository, ReservaRemoteRepository,
│                           # MovimientoRemoteRepository y FeriadoRepository
├── model/                  # Rol, EstadoReserva, BloqueHorario, TipoMovimiento, modelos de formularios
├── notificaciones/         # Canales, notificador y registro de avisos ya enviados
├── remote/
│   ├── ApiService.kt       # Endpoints de los microservicios
│   ├── RetrofitInstance.kt # Retrofit hacia el API Gateway
│   ├── ApiCall.kt          # Manejo de errores de la API (ApiException)
│   ├── dto/                # Objetos que se envían y reciben del backend
│   └── externa/            # API externa de feriados (Retrofit propio)
├── ui/
│   ├── components/         # Selector de comprobante, imagen inteligente
│   ├── screens/            # Login, Registro, Home, Arriendos, Solicitudes, Movimientos, Transparencia, Perfil
│   └── theme/              # Colores, tema y tipografía
├── util/                   # Fábrica de URIs para la cámara, conversión del comprobante a Base64
├── viewmodel/              # ViewModels de cada pantalla
└── MainActivity.kt
```

##  Pasos para ejecutar

### Requisitos
- Android Studio (versión reciente, compatible con AGP 8.12)
- JDK 17 o superior
- XAMPP (MySQL) y Docker Desktop
- Un celular o emulador con **Android 13 (API 33) o superior**

### 1. Levantar el backend
1. Abre **XAMPP** y enciende **MySQL** (y Apache).
2. En la carpeta del backend, ejecuta `.\build-all.bat` y espera a que se compilen los `.jar`.
3. Con **Docker Desktop** abierto, ejecuta `docker-compose up`.
4. Espera a que los 4 servicios terminen de iniciar. Para comprobarlo, abre `http://localhost:9090/reservas/disponibilidad?fecha=2030-01-10` en el navegador: debe mostrar un JSON con los 3 bloques.

### 2. Ejecutar la app
1. Clona el repositorio:
   ```bash
   git clone https://github.com/P0llo9003/JuntaZaror.git
   ```
2. Ábrelo en Android Studio y espera a que termine la sincronización de Gradle.
3. Conecta el celular por USB (con depuración USB activada) o abre un emulador.
4. Presiona **Run ▶**.
5. Redirige el puerto del backend hacia el celular. La app se conecta a `http://localhost:9090/`, así que en la terminal de Android Studio (PowerShell) ejecuta:
   ```powershell
   & "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" reverse tcp:9090 tcp:9090
   ```
   Este comando funciona tanto en el celular como en el emulador, y hay que repetirlo cada vez que se desconecta el dispositivo.
6. Acepta el permiso de notificaciones cuando la app lo pida.

> La API de feriados usa el internet del celular (WiFi o datos). El backend, en cambio, llega por el cable USB gracias a `adb reverse`.

##  APK firmado y llave (.jks)

La app se entrega como **APK de release firmado digitalmente** con una llave propia, generada desde Android Studio y guardada dentro del repositorio junto a su configuración.

| Elemento | Ubicación en el repositorio |
|---|---|
| APK firmado | `app/release/app-release.apk` |
| Llave de firma (.jks) | `app/keystore/juntazaror.jks` |
| Configuración de la firma | `app/build.gradle.kts` (bloque `signingConfigs`) |

**Datos de la llave**

| Dato | Valor |
|---|---|
| Alias | `juntazaror` |
| Algoritmo | RSA 2048 bits |
| Validez | 25 años |
| Certificado | TomasRojas SebastianGomez · Duoc UC · Santiago, CL |
| Contraseñas | Las usadas en `storePassword` y `keyPassword` del `app/build.gradle.kts` |

### Configuración de la firma (`app/build.gradle.kts`)

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("keystore/juntazaror.jks")
        storePassword = "..."   // ver app/build.gradle.kts
        keyAlias = "juntazaror"
        keyPassword = "..."     // ver app/build.gradle.kts
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        ...
    }
}
```

> Las contraseñas están en el `build.gradle.kts` solo porque es un trabajo académico y la rúbrica pide dejar el proyecto funcional en el repositorio. En una app real irían en un archivo fuera del control de versiones.

### Cómo generar el APK firmado

**Desde Android Studio**
1. **Build > Generate Signed App Bundle / APK…** y elegir **APK**.
2. En *Key store path* elegir `app/keystore/juntazaror.jks` (**Choose existing…**), con el alias `juntazaror`.
3. Marcar la variante **release** y pulsar **Create**.
4. El APK queda en `app/release/app-release.apk`.

**Desde la terminal** (usa la `signingConfig` del Gradle)
```bash
./gradlew assembleRelease   # el APK queda en app/build/outputs/apk/release/
```

### Verificar la firma
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\<versión>\apksigner.bat" verify --verbose app\release\app-release.apk
```
Debe indicar `Verified using v2 scheme: true`.

### Instalar el APK
Con un emulador abierto o un celular conectado por USB, se puede arrastrar `app-release.apk` al dispositivo o instalarlo con:
```powershell
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" install -r app\release\app-release.apk
```
Para que la app llegue al backend hay que ejecutar antes el `adb reverse` del paso 5 de *Ejecutar la app*.

### Captura del APK firmado y del .jks

![APK firmado y llave .jks](docs/captura-apk-firmado.png)

##  Usuarios de prueba

Liquibase crea estas cuentas en el backend la primera vez que se levanta. Según el rol, cada cuenta entra a pantallas distintas, y los vecinos no pueden acceder a ellas:

| Rol | Correo | Contraseña |
|---|---|---|
| Directiva | `directiva@demo.cl` | `Demo1234` |
| Tesorería | `tesoreria@demo.cl` | `Demo1234` |

Las cuentas de **vecino** se crean desde la pantalla de registro.

##  Pruebas unitarias

Estrategia de testing híbrido (Guía 15):

| Tipo | Carpeta | Herramientas | Qué se prueba |
|---|---|---|---|
| Unitarias (lógica) | `app/src/test` | Kotest + MockK + kotlinx-coroutines-test (JUnit 5) | `LoginViewModel`, `SolicitarArriendoViewModel` (flujo completo de reserva, incluidos los feriados) y `CuentaRemoteRepository` |
| UI | `app/src/androidTest` | JUnit 4 + Compose UI Test | `LoginScreen` (errores de validación) |

Los ViewModels y repositorios reciben sus dependencias por constructor. Así, en las pruebas el backend y la API externa se reemplazan por mocks de MockK y no se necesita red.

**Ejecutar las pruebas unitarias:**
```bash
./gradlew testDebugUnitTest
```

**Reporte de cobertura (Kover):**
```bash
./gradlew koverHtmlReportDebug   # reporte en app/build/reports/kover/htmlDebug/index.html
```

**Pruebas de UI** (requieren emulador o celular conectado):
```bash
./gradlew connectedDebugAndroidTest
```


##  Diseño

Paleta de colores tierra:

| Nombre | Color |
|---|---|
| Earth(Tierra) | `#A16D47` |
| Sand(Arena) | `#D8CABA` |
| Latte | `#C0AD8D` |
| Olive(Oliva) | `#776F4F` |
| Midnight | `#1D1D1D` |
| Linen | `#F7F3EB` |

El ícono de la app fue pensado para representar un vecindario, por eso las casas reunidas, manteniendo la paleta de colores que se usa en la aplicación. Estos colores, así como la tipografía y los detalles en la animación de íconos, fueron elegidos para transmitir cierta tranquilidad, aire familiar, y ser ligero a la vista.
