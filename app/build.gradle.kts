import java.util.Properties
import java.io.FileInputStream

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
}

android {
  namespace = "com.catcode.note_app"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.catcode.noteapp"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0.0"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  signingConfigs {
    create("release") {
      val localProperties = Properties()
      val localPropertiesFile = rootProject.file("local.properties")
      if (localPropertiesFile.exists()) {
          localProperties.load(FileInputStream(localPropertiesFile))
      }

      val sPassword = System.getenv("KEYSTORE_PASSWORD") ?: localProperties.getProperty("KEYSTORE_PASSWORD")
      val kPassword = System.getenv("KEY_PASSWORD") ?: localProperties.getProperty("KEY_PASSWORD")
      val sFile = System.getenv("KEYSTORE_PATH") ?: "../noteapp-release.keystore"

      storeFile = file(sFile)
      storePassword = sPassword
      keyAlias = "noteapp"
      keyPassword = kPassword
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      signingConfig = signingConfigs.getByName("release")
    }
  }

  kotlinOptions {
    jvmTarget = "17"
  }

  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.12.0")

  implementation("androidx.appcompat:appcompat:1.6.1")

  // Material UI clasic
  implementation("com.google.android.material:material:1.11.0")

  implementation("androidx.room:room-runtime:2.6.1")
  kapt("androidx.room:room-compiler:2.6.1")

  implementation("androidx.room:room-ktx:2.6.1")

  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

  // Layout
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")

  // List
  implementation("androidx.recyclerview:recyclerview:1.3.2")

  // PDF
  implementation("com.itextpdf:itext-core:8.0.2")
}
