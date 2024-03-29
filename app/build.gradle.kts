import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.clementcorporation.levosonusii"
    compileSdk = 34
    val localPropertiesFile = rootProject.file("local.properties")
    val localProperties = Properties()
    localProperties.load(FileInputStream(localPropertiesFile))
    defaultConfig {
        applicationId = "com.clementcorporation.levosonusii"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "PLACES_API_KEY", localProperties["placesApiKey"] as String)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val daggerVersion = "2.48"
    val lifecycleVersion = "2.7.0"
    val hiltVersion = "1.1.0"

    //Volley
    implementation ("com.android.volley:volley:1.2.1")

    //Location
    implementation ("com.google.android.gms:play-services-location:21.1.0")

    //firebase/firestore
    implementation (platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-database-ktx")
    implementation ("com.google.firebase:firebase-storage-ktx")

    // Dagger - Hilt
    implementation ("com.google.dagger:hilt-android:$daggerVersion")
    implementation ("androidx.hilt:hilt-navigation-compose:$hiltVersion")
    ksp("androidx.hilt:hilt-compiler:$hiltVersion")
    ksp("com.google.dagger:hilt-android-compiler:$daggerVersion")

    // Coil
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.compose.material:material-icons-extended:1.6.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.compose.material:material:1.6.1")
    implementation ("androidx.navigation:navigation-compose:2.7.7")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation ("androidx.datastore:datastore:1.0.0")
    implementation ("androidx.datastore:datastore-core:1.0.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    implementation ("com.google.accompanist:accompanist-permissions:0.21.1-beta")
    testImplementation("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:4.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}