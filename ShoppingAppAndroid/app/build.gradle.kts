configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.intellij" && requested.name == "annotations") {
            useTarget("org.jetbrains:annotations:23.0.0")
        }
    }
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.shoppingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.shoppingapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.play.services.analytics.impl)
    implementation(libs.ext.junit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.swiperefreshlayout)

}