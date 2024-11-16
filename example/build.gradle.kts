plugins {
    java
    kotlin("jvm")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") apply false
}

dependencies {
    implementation(project(":"))
    implementation(kotlin("stdlib"))
}
