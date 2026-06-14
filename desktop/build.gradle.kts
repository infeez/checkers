plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.gdx)
    implementation(libs.gdx.backend.lwjgl3)
    runtimeOnly(libs.gdx.platform) {
        artifact {
            classifier = "natives-desktop"
        }
    }
    testImplementation(libs.junit)
}

application {
    mainClass.set("com.infeez.simple.desktop.DesktopLauncherKt")
}

sourceSets {
    main {
        resources.srcDir(rootProject.layout.projectDirectory.dir("core/assets"))
    }
}

tasks.withType<Test>().configureEach {
    useJUnit()
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.layout.projectDirectory.dir("core/assets").asFile
    standardInput = System.`in`
}

tasks.register<Jar>("dist") {
    group = "build"
    description = "Builds a runnable desktop jar with runtime dependencies and assets."
    dependsOn(tasks.classes)
    dependsOn(":core:jar")

    archiveClassifier.set("desktop")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }

    from(sourceSets.main.get().output)
    from(rootProject.layout.projectDirectory.dir("core/assets"))
    from(configurations.runtimeClasspath.get().map { file ->
        if (file.isDirectory) file else zipTree(file)
    })
}
