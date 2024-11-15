plugins {
    `java-library`
    jacoco
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.16.3"
    kotlin("jvm") version "2.0.20"
}

group = "space.iseki.pefile"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnlyApi("org.jetbrains:annotations:26.0.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

val java22 by sourceSets.creating

tasks.getByName(java22.compileJavaTaskName) {
    this as JavaCompile
    sourceCompatibility = "22"
    targetCompatibility = "22"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
    into("META-INF/versions/22") {
        from(java22.output)
    }
    manifest {
        attributes["Multi-Release"] = "true"
    }
}

tasks.withType<AbstractArchiveTask> {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.test {
    useJUnitPlatform {
        javaLauncher = javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    classpath = files(tasks.jar.get().archiveFile, classpath) - sourceSets.main.get().output
    dependsOn(tasks.jar)
}

tasks.withType<JavaCompile> {
    options.isDeprecation = true
    options.compilerArgs.add("-Xlint:unchecked")
}

val emptyJavadoc = tasks.create("emptyJavadoc", Jar::class) {
    archiveClassifier.set("javadoc")
}

tasks.create("testJava22", Test::class) {
    useJUnitPlatform {
        javaLauncher = javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(22))
        }
    }
    classpath = files(tasks.jar.get().archiveFile, classpath) - sourceSets.main.get().output
    dependsOn(tasks.jar)
}

tasks.jacocoTestReport {
    dependsOn("testJava22", "test")
}

tasks.check {
    dependsOn("testJava22", "test")
}


