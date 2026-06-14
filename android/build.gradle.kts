plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.infeez.simple"
    compileSdk = 36
    compileSdkMinor = 1

    defaultConfig {
        applicationId = "com.infeez.simple"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets["main"].assets.directories.add(rootProject.layout.projectDirectory.dir("core/assets").asFile.path)

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.gdx)
    implementation(libs.gdx.backend.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}

val androidNativeConfigurations = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
    .associateWith { abi ->
        configurations.create("gdxNatives${abi.replace("-", "").replace("_", "")}") {
            isCanBeConsumed = false
            isCanBeResolved = true
        }
    }

androidNativeConfigurations.forEach { (abi, configuration) ->
    dependencies.add(
        configuration.name,
        "com.badlogicgames.gdx:gdx-platform:${libs.versions.libgdx.get()}:natives-$abi",
    )
}

tasks.register("syncAndroidNatives") {
    val outputDir = layout.projectDirectory.dir("src/main/jniLibs")
    inputs.files(androidNativeConfigurations.values)
    outputs.dir(outputDir)

    doLast {
        androidNativeConfigurations.forEach { (abi, configuration) ->
            copy {
                from(configuration.files.map { zipTree(it) }) {
                    include("*.so")
                }
                into(outputDir.dir(abi))
            }
        }
    }
}

tasks.named("preBuild") {
    dependsOn("syncAndroidNatives")
}
