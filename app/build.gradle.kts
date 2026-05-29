plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fitgit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fitgit"
        minSdk = 24
        targetSdk = 34
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
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Auth viene de tu catálogo de versiones (libs.versions.toml)
    implementation(libs.firebase.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Glide(GIFs)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")


    val roomVersion = "2.6.1"

    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // Soporte para LiveData con Room
    implementation("androidx.room:room-ktx:$roomVersion")

    // 🔥 FIREBASE BOM (El Director de Orquesta) 🔥
    // Controla las versiones de todas las piezas de Firebase para que no choquen
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // Librerías de Firebase SIN VERSIÓN (el BOM asigna las correctas)
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Google Sign-In (Este no es de Firebase directamente, así que sí lleva versión)
    implementation("com.google.android.gms:play-services-auth:21.1.1")

    // UI Extras
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //Gemini
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
}