plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"
    // Kover: mide la cobertura de las pruebas unitarias (reporte HTML + verificación del 80%)
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

android {
    namespace = "com.duoc.juntazaror"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.duoc.juntazaror"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "BASE_URL", "\"http://localhost:9090/\"")


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    testOptions {
        unitTests {
            // Las clases de Android (Log, Uri, etc.) devuelven valores por defecto en vez de lanzar error en las pruebas JVM
            isReturnDefaultValues = true
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)


    // ---------- Pruebas unitarias (Guía 15) ----------
    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    // MockK
    testImplementation("io.mockk:mockk:1.13.10")
    // Corrutinas en pruebas (runTest, Dispatchers.setMain)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation("androidx.compose.animation:animation-graphics")
    implementation("io.coil-kt:coil-compose:2.7.0")
    ksp("androidx.room:room-compiler:2.6.1")

}

// Obligatorio para usar JUnit 5 / Kotest
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Cobertura: solo se mide la LÓGICA (ViewModels, repositorios, manejo de errores de la API y hash de claves).
// La UI (Compose), DTOs, entidades y código autogenerado no son lógica de negocio.
kover {
    reports {
        filters {
            includes {
                packages(
                    "com.duoc.juntazaror.viewmodel",
                    "com.duoc.juntazaror.data.repository"
                )
                classes(
                    "com.duoc.juntazaror.remote.ApiCallKt*",
                    "com.duoc.juntazaror.remote.ApiException",
                    "com.duoc.juntazaror.util.PasswordHasher*"
                )
            }
        }
    }
}
