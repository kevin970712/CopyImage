plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.qianxu.copyimage"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.qianxu.copyimage"
    minSdk = 29
    targetSdk = 35
    versionCode = 4
    versionName = "2.0.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = JavaVersion.VERSION_17.toString() }
  buildFeatures { compose = true }
}

dependencies {
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.material3)
  implementation(libs.coil.compose)
  implementation(libs.androidx.appcompat)
}
