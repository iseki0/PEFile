import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    jacoco
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.16.3"
    kotlin("jvm") version "2.0.20"
    `maven-publish`
    signing
}

allprojects {
    group = "space.iseki.pefile"
    version = "0.3-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnlyApi("org.jetbrains:annotations:26.0.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

val java22: SourceSet by sourceSets.creating

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
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("17")
    }
}

tasks.jar {
    into("META-INF/versions/22") {
        from(java22.output)
    }
    from("LICENSE", "NOTICE")
    manifest {
        attributes["Multi-Release"] = "true"
    }
}

tasks.getByName("sourcesJar") {
    this as Jar
    into("META-INF/versions/22") {
        from(java22.allSource)
    }
    from("LICENSE", "NOTICE")
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

publishing {
    repositories {
        maven {
            name = "Central"
            afterEvaluate {
                url = if (version.toString().endsWith("SNAPSHOT")) {
                    // uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
                    uri("https://oss.sonatype.org/content/repositories/snapshots")
                } else {
                    // uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
                    uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                }
            }
            credentials {
                username = properties["ossrhUsername"]?.toString() ?: System.getenv("OSSRH_USERNAME")
                password = properties["ossrhPassword"]?.toString() ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(emptyJavadoc)
        }
        withType<MavenPublication> {
            pom {
                val projectUrl = "https://github.com/iseki0/PEFile"
                name = "PEFile"
                description = "A library for PE file"
                url = projectUrl
                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }
                developers {
                    developer {
                        id = "iseki0"
                        name = "iseki zero"
                        email = "iseki@iseki.space"
                    }
                }
                inceptionYear = "2024"
                scm {
                    connection = "scm:git:$projectUrl.git"
                    developerConnection = "scm:git:$projectUrl.git"
                    url = projectUrl
                }
                issueManagement {
                    system = "GitHub"
                    url = "$projectUrl/issues"
                }
            }
        }
    }

}

signing {
    useGpgCmd()
    sign(publishing.publications)
}
