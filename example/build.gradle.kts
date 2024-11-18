plugins {
    java
    kotlin("jvm")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") apply false
    application
}

dependencies {
    implementation(project(":"))
    implementation(kotlin("stdlib"))
}

application {
    mainClass = "example.MainKt"
}

tasks.compileJava {
    options.compilerArgumentProviders.add(CommandLineArgumentProvider {
        listOf("--patch-module", "pefile.example.main=${sourceSets["main"].output.asPath}")
    })
}
