plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.myapplicationapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplicationapp"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    // Google Play Services para Wear OS
    implementation(libs.play.services.wearable)
    implementation("com.google.accompanist:accompanist-pager:0.34.0")

    // Jetpack Compose BOM
    implementation(platform(libs.compose.bom))

    // Jetpack Compose UI core
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)

    // Material para Compose
    implementation(libs.compose.material)

    // Foundation para layouts
    implementation(libs.compose.foundation)

    // Tooling para Wear
    implementation(libs.wear.tooling.preview)

    // Actividad con Compose
    implementation(libs.activity.compose)

    // SplashScreen (pantalla de inicio)
    implementation(libs.core.splashscreen)

    // Navegación Compose (esto es lo que te hacía falta)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Íconos extendidos de Material (también faltaban)
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    // Soporte para Wear Compose Material
    implementation("androidx.wear.compose:compose-material:1.3.1")

    implementation("androidx.compose.ui:ui-graphics:1.6.7")
    implementation("androidx.compose.foundation:foundation:1.6.7")
    // Material clásico de Jetpack Compose (para usar Text, TextField, Divider, Icon, etc.)
    implementation("androidx.compose.material:material:1.6.7")
    implementation("androidx.compose.material:material-icons-extended:1.6.7")
    implementation("androidx.compose.foundation:foundation:1.5.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.34.0")
// Pager de Accompanist (para HorizontalPager y rememberPagerState)
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.34.0")
    implementation ( "androidx.core:core:1.13.")
    implementation( "androidx.core:core-ktx:1.13.1")
    implementation( "androidx.compose.ui:ui-util:1.6.0")
    implementation( "androidx.compose.ui:ui:1.6.0")
    implementation( "androidx.compose.material:material:1.6.0")
    implementation ("androidx.compose.ui:ui-tooling:1.6.0")
    implementation ("androidx.compose.foundation:foundation:1.6.0")
    implementation ("com.google.code.gson:gson:2.10.1")


    // Test
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
