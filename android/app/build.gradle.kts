import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.serialization)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.daedan.festabook"
    compileSdk = 36

    val localProperties =
        gradle.rootProject
            .file("local.properties")
            .inputStream()
            .use { Properties().apply { load(it) } }

    val naverMapClientId =
        checkNotNull(localProperties["NAVER_MAP_CLIENT_ID"] as? String) {
            "NAVER_MAP_CLIENT_ID is missing or not a String in local.properties"
        }

    val naverMapStyleId =
        checkNotNull(localProperties["NAVER_MAP_STYLE_ID"] as? String) {
            "NAVER_MAP_STYLE_ID is missing or not a String in local.properties"
        }

    val jksFilePath =
        checkNotNull(localProperties["JKS_FILE_PATH"] as? String) {
            "JKS_FILE_PATH가 local.properties에 존재하지 않습니다."
        }
    val storePasswordValue =
        checkNotNull(localProperties["STORE_PASSWORD"] as? String) {
            "STORE_PASSWORD가 local.properties에 존재하지 않습니다."
        }
    val keyPasswordValue =
        checkNotNull(localProperties["KEY_PASSWORD"] as? String) {
            "KEY_PASSWORD가 local.properties에 존재하지 않습니다."
        }
    val keyAliasValue =
        checkNotNull(localProperties["KEY_ALIAS"] as? String) {
            "KEY_ALIAS가 local.properties에 존재하지 않습니다."
        }

    signingConfigs {
        if (jksFilePath.isNotBlank() &&
            storePasswordValue.isNotBlank() &&
            keyPasswordValue.isNotBlank() &&
            keyAliasValue.isNotBlank()
        ) {
            create("release") {
                storeFile = file(jksFilePath)
                storePassword = storePasswordValue
                keyPassword = keyPasswordValue
                keyAlias = keyAliasValue
            }
        }
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
            "NAVER_MAP_CLIENT_ID",
            naverMapClientId,
        )

        buildConfigField(
            "String",
            "NAVER_MAP_STYLE_ID",
            naverMapStyleId,
        )

        buildConfigField(
            "String",
            "VERSION_NAME",
            "\"${versionName}\"",
        )
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "(Debug)Festabook")

            val baseUrl =
                checkNotNull(localProperties["BASE_URL_DEV"] as? String) {
                    "BASE_URL is missing or not a String in local.properties"
                }

            buildConfigField(
                "String",
                "FESTABOOK_URL",
                baseUrl,
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            resValue("string", "app_name", "Festabook")
            signingConfig = signingConfigs["release"]

            val baseUrl =
                checkNotNull(localProperties["BASE_URL"] as? String) {
                    "BASE_URL is missing or not a String in local.properties"
                }

            buildConfigField(
                "String",
                "FESTABOOK_URL",
                baseUrl,
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
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.shimmer)
    implementation(libs.timber)
    implementation(libs.lottie)
    implementation(libs.circleindicator)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.androidx.core.splashscreen)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.assertj.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.logging.interceptor)
}
