import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.daedan.festabook"
    compileSdk = 36

    val localProperties =
        gradle.rootProject
            .file("local.properties")
            .inputStream()
            .use { Properties().apply { load(it) } }

    val baseUrl =
        checkNotNull(localProperties["BASE_URL"] as? String) {
            "BASE_URL is missing or not a String in local.properties"
        }

    val naverMapClientId =
        checkNotNull(localProperties["NAVER_MAP_CLIENT_ID"] as? String) {
            "NAVER_MAP_CLIENT_ID is missing or not a String in local.properties"
        }

    defaultConfig {
        applicationId = "com.daedan.festabook"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "FESTABOOK_URL",
            baseUrl,
        )

        buildConfigField(
            "String",
            "NAVER_MAP_CLIENT_ID",
            naverMapClientId,
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.map.sdk)
    implementation(libs.play.services.location)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.shimmer)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
