plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cp470project"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cp470project"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localPropertiesFile = rootProject.file("local.properties")
        var apiKey = ""
        if (localPropertiesFile.exists()) {
            val properties = localPropertiesFile.readText()
            val apiKeyMatch = Regex("OPENAI_API_KEY=(.+)").find(properties)
            if (apiKeyMatch != null) {
                apiKey = apiKeyMatch.groupValues[1]
            }
        }
        buildConfigField("String", "OPENAI_API_KEY", "\"$apiKey\"")
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
        buildConfig = true
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
    implementation("androidx.cardview:cardview:1.0.0")

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.room:room-ktx:$roomVersion")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}