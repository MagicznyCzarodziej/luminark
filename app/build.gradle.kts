plugins {
    id("kotlin-kapt")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

hilt {
    // https://stackoverflow.com/questions/78760124/issue-with-hilt-application-class-gradle-dependency-conflict
    enableAggregatingTask = false
}

kotlin {
    jvmToolchain(21)
}

android {
    namespace = "pl.przemyslawpitus.luminark"
    compileSdk = 35

    packaging {
        resources {
            excludes.add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
        }
    }

    defaultConfig {
        applicationId = "pl.przemyslawpitus.luminark"
        minSdk = 30 // Android 11
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

    buildFeatures {
        compose = true
        buildConfig = true
    }
}


dependencies {
    kapt(libs.hilt.android.compiler)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose.android)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.smbj)
    implementation(libs.snakeyaml)
    implementation(libs.material.icons)
    implementation(libs.coil)
    implementation(libs.timber)
    implementation(libs.kotlin.serialization.json)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}