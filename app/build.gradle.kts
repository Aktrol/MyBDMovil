// build.gradle.kts (Module :app)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.clase" // ¡Este es tu namespace correcto!
    compileSdk = 35


    defaultConfig {
        applicationId = "com.example.clase" // ¡Este es tu ID de aplicación correcto!
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
    buildFeatures {
        compose = true // Mantener, estás usando Jetpack Compose
        viewBinding = true // <--- ¡IMPORTANTE! Añadir esta línea
        // La necesitamos porque las pantallas de login y manager
        // que te di usan XML y View Binding para los EditText, Button, RecyclerView.
        // Si solo usarás Compose para TODA la UI, podrías quitarla,
        // pero para este proyecto híbrido es necesaria.
    }
    // Si estás usando Compose, también podrías necesitar:
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // O la versión que te sugiera Android Studio para tu compose BOM
    }
}

dependencies {
    // Dependencias de Compose (mantener todas)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Material Design 3 for Compose

    // Dependencias ADICIONALES para el gestor de base de datos (Widgets de XML/Material Design 2)
    // Aunque uses Compose, tus layouts de login y manager (activity_login.xml, activity_main_manager.xml)
    // están escritos en XML y usan estos componentes.
    implementation("androidx.appcompat:appcompat:1.7.0") // Componentes básicos de la interfaz de usuario
    implementation("com.google.android.material:material:1.12.0") // ¡Para TextInputLayout, Button y CardView "bonitos"!
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Si tus layouts XML usan ConstraintLayout
    implementation("androidx.recyclerview:recyclerview:1.3.2") // Para las listas de tablas y resultados

    // Testing dependencies (mantener las tuyas)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}