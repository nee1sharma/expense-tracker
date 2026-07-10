import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

base {
    archivesName.set("ExpenseTracker")
}

val versionPropsFile = rootProject.file("version.properties")
val versionProps = Properties().apply {
    if (versionPropsFile.exists()) {
        load(FileInputStream(versionPropsFile))
    }
}

val verMajor = versionProps.getProperty("VERSION_MAJOR", "1").toInt()
val verMinor = versionProps.getProperty("VERSION_MINOR", "0").toInt()
val verPatch = versionProps.getProperty("VERSION_PATCH", "0").toInt()
val verBuild = versionProps.getProperty("VERSION_BUILD", "1").toInt()

android {
    namespace = "com.hitstudio.expensetracker"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.hitstudio.expensetracker"
        minSdk = 34
        targetSdk = 36
        versionCode = verBuild
        versionName = "$verMajor.$verMinor.$verPatch"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
        }
        release {
            signingConfig = if (keystorePropertiesFile.exists()) signingConfigs.getByName("release") else null
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

tasks.register("incrementBuildNumber") {
    doLast {
        val props = Properties()
        if (versionPropsFile.exists()) {
            props.load(FileInputStream(versionPropsFile))
        }
        val currentBuild = props.getProperty("VERSION_BUILD", "0").toInt()
        props.setProperty("VERSION_BUILD", (currentBuild + 1).toString())
        props.store(versionPropsFile.writer(), "Auto-incremented build number")
        println("Build number incremented to ${currentBuild + 1}")
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.process)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.material)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.recyclerview)
    implementation(libs.room.runtime)
    implementation(libs.work.runtime)
    annotationProcessor(libs.room.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
