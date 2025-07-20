plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.billmanagerapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.billmanagerapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Room框架库
    implementation("androidx.room:room-runtime:2.7.1")
    // Java 项目使用 annotationProcessor（Kotlin 用 kapt）
    annotationProcessor("androidx.room:room-compiler:2.7.1")
    //可选：支持 Room 的 KTX 扩展（比如协程等）
    implementation("androidx.room:room-ktx:2.7.1")

    implementation("com.google.code.gson:gson:2.10.1")
}